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

package com.openmobilehub.android.auth.plugin.google.gms.util

import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhAuthStatusCodes

internal fun Task<Void>.mapToOmhExceptions(): Task<Unit> = continueWithTask { task ->
    val completionSource = TaskCompletionSource<Unit>()
    try {
        task.getResult(ApiException::class.java)
        completionSource.setResult(Unit)
    } catch (e: ApiException) {
        completionSource.setException(e.toOmhApiException())
    }
    completionSource.task
}


internal fun Exception.toOmhApiException(): OmhAuthException.ApiException {
    val apiException: ApiException? = this as? ApiException
    val statusCode: Int = when (apiException?.statusCode) {
        CommonStatusCodes.API_NOT_CONNECTED -> OmhAuthStatusCodes.GMS_UNAVAILABLE
        CommonStatusCodes.SIGN_IN_REQUIRED -> OmhAuthStatusCodes.SIGN_IN_REQUIRED
        CommonStatusCodes.NETWORK_ERROR -> OmhAuthStatusCodes.NETWORK_ERROR
        else -> OmhAuthStatusCodes.INTERNAL_ERROR
    }
    return OmhAuthException.ApiException(
        statusCode = statusCode,
        cause = this
    )
}

internal fun toOmhLoginException(apiException: ApiException, isNonGmsDevice: Boolean): OmhAuthException {
    return when (apiException.statusCode) {
        GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
            OmhAuthException.LoginCanceledException(apiException)
        }

        GoogleSignInStatusCodes.SIGN_IN_FAILED -> {
            mapSignInFailed(isNonGmsDevice, apiException)
        }

        else -> {
            mapToRecoverableLoginException(apiException)
        }
    }
}

private fun mapSignInFailed(
    isNonGmsDevice: Boolean,
    apiException: ApiException
): OmhAuthException {
    return if (isNonGmsDevice) {
        OmhAuthException.ApiException(OmhAuthStatusCodes.GMS_UNAVAILABLE, apiException)
    } else {
        OmhAuthException.UnrecoverableLoginException(apiException)
    }
}

private fun mapToRecoverableLoginException(
    apiException: ApiException
): OmhAuthException.RecoverableLoginException {
    val omhStatusCode = when (apiException.statusCode) {
        CommonStatusCodes.NETWORK_ERROR -> OmhAuthStatusCodes.NETWORK_ERROR
        CommonStatusCodes.DEVELOPER_ERROR -> OmhAuthStatusCodes.DEVELOPER_ERROR
        else -> OmhAuthStatusCodes.INTERNAL_ERROR
    }
    return OmhAuthException.RecoverableLoginException(omhStatusCode, apiException)
}
