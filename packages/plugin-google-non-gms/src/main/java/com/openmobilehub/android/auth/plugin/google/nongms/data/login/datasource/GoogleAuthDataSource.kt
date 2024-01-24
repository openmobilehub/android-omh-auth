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

package com.openmobilehub.android.auth.plugin.google.nongms.data.login.datasource

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toUri
import com.openmobilehub.android.auth.plugin.google.nongms.data.login.GoogleAuthREST
import com.openmobilehub.android.auth.plugin.google.nongms.data.login.models.AuthTokenResponse
import com.openmobilehub.android.auth.plugin.google.nongms.domain.models.ApiResult
import com.openmobilehub.android.auth.plugin.google.nongms.utils.Constants

internal class GoogleAuthDataSource(
    private val authService: GoogleAuthREST,
    private val sharedPreferences: SharedPreferences
) : AuthDataSource {

    override suspend fun getToken(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): ApiResult<AuthTokenResponse> {
        return authService.getToken(
            clientId = clientId,
            code = authCode,
            redirectUri = redirectUri,
            codeVerifier = codeVerifier
        )
    }

    /**
     * Builds the login URL for the Custom Tabs screen. This only works when your app is setup in the
     * Google Console in the OAuth Credentials sections. If the login is successful, an auth code
     * will be returned with the redirectUri. If not, an error code will be attached as a query param.
     *
     * @param scopes -> requested scopes by the application
     * @param clientId -> clientId from google console of the Android Application type.
     * @param codeChallenge -> PKCE implementation against man in the middle attacks
     * @param redirectUri -> URI used to redirect back to the application.
     */
    override fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String
    ): Uri {
        return AUTH_URI.toUri().buildUpon()
            .appendQueryParameter(Constants.PARAM_SCOPE, scopes)
            .appendQueryParameter(Constants.PARAM_RESPONSE_TYPE, CODE_VALUE)
            .appendQueryParameter(Constants.PARAM_REDIRECT_URI, redirectUri)
            .appendQueryParameter(Constants.PARAM_CLIENT_ID, clientId)
            .appendQueryParameter(Constants.PARAM_CHALLENGE_METHOD, Constants.SHA256)
            .appendQueryParameter(Constants.PARAM_CODE_CHALLENGE, codeChallenge)
            .build()
    }

    override fun storeToken(tokenType: String, token: String) {
        sharedPreferences.edit {
            putString(tokenType, token)
        }
    }

    override fun getToken(tokenType: String): String? {
        return sharedPreferences.getString(tokenType, null)
    }

    private fun getRefreshToken(): String? {
        return sharedPreferences.getString(AuthDataSource.REFRESH_TOKEN, null)
    }

    override suspend fun refreshAccessToken(clientId: String): ApiResult<AuthTokenResponse> {
        val refreshToken = getRefreshToken() ?: return ApiResult.Error.RuntimeError(
            IllegalStateException("No refresh token")
        )
        return authService.refreshToken(clientId, refreshToken)
    }

    override suspend fun revokeToken(token: String): ApiResult<Unit> {
        return (authService.revokeToken(token))
    }

    override fun clearData() {
        sharedPreferences.edit(action = SharedPreferences.Editor::clear)
    }

    companion object {
        private const val AUTH_URI = "https://accounts.google.com/o/oauth2/auth"
        private const val CODE_VALUE = "code"
    }
}
