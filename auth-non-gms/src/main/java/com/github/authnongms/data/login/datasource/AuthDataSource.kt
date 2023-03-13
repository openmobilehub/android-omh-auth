package com.github.authnongms.data.login.datasource

import android.net.Uri
import com.github.authnongms.data.login.models.AuthTokenResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface AuthDataSource {

    /**
     * Requests the token from the Google REST services. This can return HTTP errors.
     *
     * @param authCode -> the auth code returned from the custom tab login screen.
     * @param clientId -> clientId from google console of the Android Application type.
     * @param redirectUri -> the same redirectUri used for the custom tabs
     * @param codeVerifier -> PKCE implementation against man in the middle attacks.
     */
    suspend fun getToken(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ) : Flow<AuthTokenResponse>

    /**
     * Builds the login URL for the Custom Tabs screen. If the login is successful, an auth code
     * will be returned with the redirectUri. If not, an error code will be attached as a query param.
     *
     * @param scopes -> requested scopes by the application
     * @param clientId -> clientId from auth console of the Android Application type.
     * @param codeChallenge -> PKCE implementation against man in the middle attacks
     * @param redirectUri -> URI used to redirect back to the application.
     */
    fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String
    ): Uri

    fun storeToken(tokenType: String, token: String)
}
