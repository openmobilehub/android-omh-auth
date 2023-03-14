package com.github.authnongms.data.login.datasource

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toUri
import com.github.authnongms.data.login.GoogleAuthREST
import com.github.authnongms.data.login.models.AuthTokenResponse
import com.github.authnongms.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GoogleAuthDataSource(
    private val authService: GoogleAuthREST,
    private val sharedPreferences: SharedPreferences
) : AuthDataSource {

    override suspend fun getToken(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): Flow<AuthTokenResponse> = flow {
        val authTokenResponse: AuthTokenResponse = authService.getToken(
            clientId = clientId,
            code = authCode,
            redirectUri = redirectUri,
            codeVerifier = codeVerifier
        )
        emit(authTokenResponse)
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

    override fun refreshAccessToken(clientId: String): Flow<AuthTokenResponse> = flow {
        val refreshToken = checkNotNull(getRefreshToken())
        emit(authService.refreshToken(clientId, refreshToken))
    }

    companion object {
        private const val AUTH_URI = "https://accounts.google.com/o/oauth2/auth"
        private const val CODE_VALUE = "code"
    }
}
