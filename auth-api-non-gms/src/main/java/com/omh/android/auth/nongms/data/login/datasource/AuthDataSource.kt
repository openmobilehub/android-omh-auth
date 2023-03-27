package com.omh.android.auth.nongms.data.login.datasource

import android.net.Uri
import com.omh.android.auth.nongms.data.login.models.AuthTokenResponse
import retrofit2.Response

interface AuthDataSource {

    /**
     * Requests the token from the auth provider REST services. This can return HTTP errors.
     *
     * @param authCode -> the auth code returned from the custom tab login screen.
     * @param clientId -> clientId from auth console.
     * @param redirectUri -> the same redirectUri used for the custom tabs
     * @param codeVerifier -> PKCE implementation against man in the middle attacks.
     */
    suspend fun getToken(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): Response<AuthTokenResponse>

    /**
     * Builds the login URL for the Custom Tabs screen. If the login is successful, an auth code
     * will be returned with the [redirectUri]. If not, an error code will be attached as a query param.
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

    /**
     * Stores token in local storage of key value type.
     *
     * @param tokenType -> one of [ACCESS_TOKEN] or [REFRESH_TOKEN]
     * @param token -> token to store.
     */
    fun storeToken(tokenType: String, token: String)

    /**
     * Accesses a token from the local storage.
     *
     * @param tokenType -> the type of token that is to be retrieved ([ACCESS_TOKEN] or [REFRESH_TOKEN])
     *
     * @return a token from the storage if available, null if not.
     */
    fun getToken(tokenType: String): String?

    /**
     * Refreshes the access token from the provider cloud. This can return HTTP errors.
     *
     * @param clientId -> clientId from the auth console
     *
     * @return a [Response] with the [AuthTokenResponse]
     */
    suspend fun refreshAccessToken(clientId: String): Response<AuthTokenResponse>

    /**
     * Indicates the auth provider that the token should be revoked. When logging out, this step is
     * essential to assure that token can't be used by third parties. This can return HTTP errors.
     *
     * @param token -> token to revoke.
     */
    suspend fun revokeToken(token: String): Response<Nothing>

    /**
     * Clears all local data of the user, including any stored tokens.
     */
    fun clearData()

    companion object {
        const val ACCESS_TOKEN = "accesstoken"
        const val REFRESH_TOKEN = "refreshtoken"
    }
}
