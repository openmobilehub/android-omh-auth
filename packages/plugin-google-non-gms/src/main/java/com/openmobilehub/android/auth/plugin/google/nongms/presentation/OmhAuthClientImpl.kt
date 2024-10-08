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

import android.content.Context
import android.content.Intent
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import com.openmobilehub.android.auth.plugin.google.nongms.data.login.AuthRepositoryImpl
import com.openmobilehub.android.auth.plugin.google.nongms.data.user.UserRepositoryImpl
import com.openmobilehub.android.auth.plugin.google.nongms.domain.auth.AuthUseCase
import com.openmobilehub.android.auth.plugin.google.nongms.domain.models.ApiResult
import com.openmobilehub.android.auth.plugin.google.nongms.domain.user.ProfileUseCase
import com.openmobilehub.android.auth.plugin.google.nongms.presentation.redirect.RedirectActivity

/**
 * Non GMS implementation of the OmhAuthClient abstraction. Required a clientId and defined scopes as
 * no extra scopes can be accessed in the future.
 */
internal class OmhAuthClientImpl(
    private val clientId: String,
    private val scopes: String,
    context: Context
) : OmhAuthClient {

    private val applicationContext: Context

    init {
        applicationContext = context.applicationContext
    }

    override fun initialize(): OmhTask<Unit> {
        return OmhTask({
            // No initialization needed for Google Sign-In
        })
    }

    override fun getLoginIntent(): Intent {
        return Intent(applicationContext, RedirectActivity::class.java)
            .putExtra(RedirectActivity.CLIENT_ID, clientId)
            .putExtra(RedirectActivity.SCOPES, scopes)
    }

    override fun getUser(): OmhTask<OmhUserProfile> {
        val userRepository = UserRepositoryImpl.getUserRepository(applicationContext)
        val profileUseCase = ProfileUseCase.createUserProfileUseCase(userRepository)

        return OmhTask({
            val profileData = profileUseCase.getProfileData()

            if (profileData == null) {
                throw OmhAuthException.UnrecoverableLoginException(
                    cause = Throwable(message = "No user profile stored")
                )
            }

            return@OmhTask profileData
        })
    }

    internal class Builder(
        private var clientId: String,
    ) : OmhAuthClient.Builder {

        private var authScope: String = ""

        fun addScope(scope: String): Builder {
            authScope += " $scope"
            authScope.trim()
            return this
        }

        override fun build(context: Context): OmhAuthClient {
            return OmhAuthClientImpl(clientId, authScope, context)
        }
    }

    override fun getCredentials(): OmhCredentials {
        return OmhAuthFactoryImpl.getCredentials(clientId, applicationContext)
    }

    @SuppressWarnings("TooGenericExceptionCaught") // Until we find any specific errors for this.
    override fun signOut(): OmhTask<Unit> {
        val authRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return OmhTask(authUseCase::logout)
    }

    override fun revokeToken(): OmhTask<Unit> {
        val authRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return OmhTask({
            val apiResult: ApiResult<Unit> = authUseCase.revokeToken()
            return@OmhTask apiResult.extractResult()
        })
    }

    override fun getProviderSdk() =
        throw UnsupportedOperationException("Google non-gms implementation uses REST API underneath.")
}
