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

package com.openmobilehub.android.auth.plugin.google.nongms.presentation

import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.plugin.google.nongms.domain.auth.AuthUseCase
import com.openmobilehub.android.auth.plugin.google.nongms.domain.models.ApiResult

internal class NonGmsCredentials(
    private val authUseCase: AuthUseCase,
    private val clientId: String
) : OmhCredentials {

    override fun refreshToken(): OmhTask<String?> {
        return OmhTask {
            when (val apiResult = authUseCase.refreshToken(clientId)) {
                is ApiResult.Success -> apiResult.data
                else -> null
            }
        }
    }

    override val accessToken: String?
        get() = authUseCase.getAccessToken()
}
