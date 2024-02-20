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

sealed class OmhAuthException(val statusCode: Int) : Exception() {

    class LoginCanceledException(
        override val cause: Throwable? = null,
    ) : OmhAuthException(OmhAuthStatusCodes.SIGN_IN_CANCELED)

    class NotInitializedException : OmhAuthException(OmhAuthStatusCodes.NOT_INITIALIZED)

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

    override val message: String?
        get() = OmhAuthStatusCodes.getStatusCodeString(statusCode)
}
