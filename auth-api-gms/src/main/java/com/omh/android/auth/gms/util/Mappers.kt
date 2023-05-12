package com.omh.android.auth.gms.util

import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes

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
        else -> OmhAuthStatusCodes.INTERNAL_ERROR
    }
    return OmhAuthException.ApiException(
        statusCode = statusCode,
        cause = this
    )
}

internal fun toOmhLoginException(apiException: ApiException) = when (apiException.statusCode) {
    GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
        OmhAuthException.LoginCanceledException(apiException.cause)
    }
    GoogleSignInStatusCodes.SIGN_IN_FAILED -> {
        OmhAuthException.UnrecoverableLoginException(apiException.cause)
    }
    else -> {
        mapToRecoverableLoginException(apiException)
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
    return OmhAuthException.RecoverableLoginException(omhStatusCode, apiException.cause)
}
