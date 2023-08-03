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

package com.omh.android.auth.nongms.presentation

import com.omh.android.auth.api.OmhCredentials
import com.omh.android.auth.nongms.domain.auth.AuthUseCase
import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.utils.ThreadUtils
import kotlinx.coroutines.runBlocking

internal class OmhCredentialsImpl(
    private val authUseCase: AuthUseCase,
    private val clientId: String
) : OmhCredentials {

    override fun blockingRefreshToken(): String? {
        ThreadUtils.checkForMainThread()
        return runBlocking {
            when (val apiResult = authUseCase.blockingRefreshToken(clientId)) {
                is ApiResult.Success -> apiResult.data
                else -> null
            }
        }
    }

    override val accessToken: String?
        get() = authUseCase.getAccessToken()
}
