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

package com.openmobilehub.android.auth.core.models

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
    const val NOT_INITIALIZED = 10
    const val PROVIDER_ERROR = 11
    const val CANCELED = 12

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
            PROVIDER_ERROR -> "Provider error."
            CANCELED -> "Cancelled."
            else -> "Unknown status code: $code"
        }
    }
}
