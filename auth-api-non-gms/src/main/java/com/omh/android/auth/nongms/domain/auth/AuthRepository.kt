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

package com.omh.android.auth.nongms.domain.auth

import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.domain.models.OAuthTokens

internal interface AuthRepository {

    /**
     * Requests OAuth tokens from the auth provider.
     *
     * @param clientId -> your client id from the console
     * @param authCode -> the auth code received after the user executed their login
     * @param redirectUri -> the same redirect URI from the login screen
     * @param codeVerifier -> code verifier of the PKCE protocol
     */
    suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): ApiResult<OAuthTokens>

    /**
     * Builds the login URL to use in the custom tabs implementation. This will show the user the
     * login screen of the auth provider.
     *
     * @param scopes -> requested scopes from the application
     * @param clientId -> your client id from the console
     * @param codeChallenge -> code challenge of the PCKE protocol
     * @param redirectUri -> redirect URI for the application to catch the deeplink with params
     */
    fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String
    ): String

    /**
     * Requests the access token.
     *
     * @return null if not available.
     */
    fun getAccessToken(): String?

    /**
     * Refreshes the access token from the auth provider.
     *
     * @param clientId -> clientId from the auth console
     *
     * @return a [ApiResult] with the token inside.
     */
    suspend fun refreshAccessToken(clientId: String): ApiResult<String>

    /**
     * Revokes the access token of the user from the auth provider.
     */
    suspend fun revokeToken(): ApiResult<Unit>

    /**
     * Clears all local data of the user, including stored tokens.
     */
    fun clearData()
}
