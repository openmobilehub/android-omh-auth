package com.omh.android.auth.nongms.data.login

import android.content.Context
import android.content.SharedPreferences
import com.omh.android.auth.nongms.data.utils.GoogleRetrofitImpl
import com.omh.android.auth.nongms.data.login.datasource.AuthDataSource
import com.omh.android.auth.nongms.data.login.datasource.GoogleAuthDataSource
import com.omh.android.auth.nongms.data.login.models.AuthTokenResponse
import com.omh.android.auth.nongms.data.utils.getEncryptedSharedPrefs
import com.omh.android.auth.nongms.domain.auth.AuthRepository
import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.domain.models.OAuthTokens
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AuthRepositoryImpl(
    private val googleAuthDataSource: AuthDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): ApiResult<OAuthTokens> = withContext(ioDispatcher) {
        val result: ApiResult<AuthTokenResponse> = googleAuthDataSource.getToken(
            clientId = clientId,
            authCode = authCode,
            redirectUri = redirectUri,
            codeVerifier = codeVerifier
        )

        result.map { data: AuthTokenResponse ->
            googleAuthDataSource.storeToken(
                tokenType = AuthDataSource.ACCESS_TOKEN,
                token = data.accessToken
            )
            googleAuthDataSource.storeToken(
                tokenType = AuthDataSource.REFRESH_TOKEN,
                token = checkNotNull(data.refreshToken)
            )
            OAuthTokens(
                accessToken = data.accessToken,
                refreshToken = checkNotNull(data.refreshToken),
                idToken = data.idToken
            )
        }
    }

    override fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String
    ): String {
        return googleAuthDataSource.buildLoginUrl(
            scopes = scopes,
            clientId = clientId,
            codeChallenge = codeChallenge,
            redirectUri = redirectUri
        ).toString()
    }

    override fun getAccessToken(): String? {
        return googleAuthDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
    }

    override suspend fun refreshAccessToken(
        clientId: String
    ): ApiResult<String> = withContext(ioDispatcher) {
        googleAuthDataSource.refreshAccessToken(clientId).map { data: AuthTokenResponse ->
            googleAuthDataSource.storeToken(AuthDataSource.ACCESS_TOKEN, data.accessToken)
            data.accessToken
        }
    }

    override suspend fun revokeToken(): ApiResult<Unit> = withContext(ioDispatcher) {
        val accessToken = googleAuthDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
        if (accessToken == null) {
            val noTokenException = IllegalStateException("No token stored")
            return@withContext ApiResult.Error.RuntimeError(noTokenException)
        }

        return@withContext googleAuthDataSource.revokeToken(accessToken)
    }

    override fun clearData() {
        googleAuthDataSource.clearData()
    }

    companion object {

        private var authRepository: AuthRepository? = null

        fun getAuthRepository(
            context: Context,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): AuthRepository {
            if (authRepository == null) {
                val authService: GoogleAuthREST = GoogleRetrofitImpl.instance.googleAuthREST
                val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context)
                val googleAuthDataSource: AuthDataSource = GoogleAuthDataSource(
                    authService = authService,
                    sharedPreferences = sharedPreferences
                )
                authRepository = AuthRepositoryImpl(googleAuthDataSource, ioDispatcher)
            }

            return authRepository!!
        }
    }
}
