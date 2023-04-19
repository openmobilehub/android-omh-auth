package com.omh.android.auth.api.models

sealed class OmhAuthException(val statusCode: Int) : Exception() {

    class LoginCanceledException(
        override val cause: Throwable? = null,
    ) : OmhAuthException(OmhAuthStatusCodes.SIGN_IN_CANCELED)

    class UnrecoverableLoginException(
        override val cause: Throwable? = null,
    ) : OmhAuthException(OmhAuthStatusCodes.SIGN_IN_FAILED)

    class RecoverableLoginException(
        statusCode: Int,
        override val cause: Throwable? = null,
    ) : OmhAuthException(statusCode)

    class ApiException(
        statusCode: Int,
        override val cause: Throwable? = null
    ) : OmhAuthException(statusCode)
}
