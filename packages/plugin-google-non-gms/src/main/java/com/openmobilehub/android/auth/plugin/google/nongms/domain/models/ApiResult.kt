/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.auth.plugin.google.nongms.domain.models

import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhAuthStatusCodes
import kotlin.jvm.Throws
import retrofit2.HttpException

internal sealed class ApiResult<out T> {

    data class Success<out R>(val data: R) : ApiResult<R>()

    sealed class Error(open val cause: Throwable) : ApiResult<Nothing>() {

        data class ApiError(override val cause: HttpException) : Error(cause)

        data class RuntimeError(override val cause: Throwable) : Error(cause)

        data class NetworkError(override val cause: Throwable) : Error(cause)
    }

    /**
     * Maps a result of type [T] to another type [R] with error catching, returning a
     * [Error.RuntimeError] to represent any exceptions.
     */
    @SuppressWarnings("TooGenericExceptionCaught")
    inline fun <R> map(mapFunction: (T) -> R): ApiResult<R> {
        return when (this) {
            is Success -> {
                try {
                    val transformedData = mapFunction(data)
                    Success(transformedData)
                } catch (e: RuntimeException) {
                    Error.RuntimeError(e)
                }
            }
            is Error -> this
        }
    }

    @Throws(OmhAuthException::class)
    fun extractResult(): T {
        return when (this) {
            is Success -> data
            is Error -> throw getOmhException()
        }
    }

    @Throws(OmhAuthException::class)
    private fun Error.getOmhException(): OmhAuthException {
        val statusCode = when (this) {
            is Error.ApiError -> OmhAuthStatusCodes.HTTPS_ERROR
            is Error.NetworkError -> OmhAuthStatusCodes.NETWORK_ERROR
            is Error.RuntimeError -> OmhAuthStatusCodes.INTERNAL_ERROR
        }
        return OmhAuthException.ApiException(statusCode, cause)
    }
}
