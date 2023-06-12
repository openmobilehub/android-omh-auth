package com.omh.android.auth.api.models

object OmhAuthStatusCodes {

    const val SIGN_IN_CANCELED = 1
    const val DEVELOPER_ERROR = 2
    const val INTERNAL_ERROR = 3
    const val NETWORK_ERROR = 4
    const val SIGN_IN_FAILED = 5
    const val ACCESS_DENIED = 6
    const val DEFAULT_ERROR = -1
    const val GMS_UNAVAILABLE = 7
    const val HTTPS_ERROR = 8
    const val SIGN_IN_REQUIRED = 9

    @JvmStatic
    fun getStatusCodeString(code: Int): String {
        return when (code) {
            SIGN_IN_CANCELED -> "Sign in action cancelled"
            DEVELOPER_ERROR -> "DEVELOPER_ERROR"
            INTERNAL_ERROR -> "INTERNAL_ERROR"
            NETWORK_ERROR -> "NETWORK_ERROR"
            SIGN_IN_FAILED -> "A non-recoverable sign in failure occurred"
            ACCESS_DENIED -> "Access denied"
            DEFAULT_ERROR -> "An error has occurred."
            GMS_UNAVAILABLE -> "GMS not available."
            HTTPS_ERROR -> "An HTTPS error has occurred."
            SIGN_IN_REQUIRED -> "Sign in required."
            else -> "Unknown status code: $code"
        }
    }
}
