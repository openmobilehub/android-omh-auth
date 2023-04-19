package com.omh.android.auth.nongms.domain.models

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
}
