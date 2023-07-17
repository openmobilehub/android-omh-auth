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

import android.content.Context
import androidx.annotation.Keep
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.OmhAuthFactory
import com.omh.android.auth.api.OmhCredentials
import com.omh.android.auth.nongms.data.login.AuthRepositoryImpl
import com.omh.android.auth.nongms.domain.auth.AuthRepository
import com.omh.android.auth.nongms.domain.auth.AuthUseCase

@Keep
internal object OmhAuthFactoryImpl : OmhAuthFactory {

    /**
     * Creates an auth client for the user of the non GMS type and returns it as the abstraction.
     * This should be used by the core plugin only.
     */
    override fun getAuthClient(
        context: Context,
        scopes: Collection<String>,
        clientId: String
    ): OmhAuthClient {
        val builder = OmhAuthClientImpl.Builder(clientId)
        scopes.forEach(builder::addScope)
        return builder.build(context)
    }

    internal fun getCredentials(clientId: String, context: Context): OmhCredentials {
        val authRepository: AuthRepository = AuthRepositoryImpl.getAuthRepository(context)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return OmhCredentialsImpl(authUseCase, clientId)
    }
}
