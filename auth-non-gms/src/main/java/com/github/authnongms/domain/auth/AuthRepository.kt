package com.github.authnongms.domain.auth

import com.github.authnongms.domain.models.OAuthTokens
import kotlinx.coroutines.flow.Flow

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
    ): Flow<OAuthTokens>

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
}
