package com.github.authnongms.data.login

import android.content.Context
import android.content.SharedPreferences
import com.github.authnongms.data.GoogleRetrofitImpl
import com.github.authnongms.data.login.datasource.AuthDataSource
import com.github.authnongms.data.login.datasource.GoogleAuthDataSource
import com.github.authnongms.data.utils.getEncryptedSharedPrefs
import com.github.authnongms.domain.auth.AuthRepository
import com.github.authnongms.domain.models.OAuthTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AuthRepositoryImpl(private val googleAuthDataSource: AuthDataSource) : AuthRepository {

    override suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): Flow<OAuthTokens> {
        return googleAuthDataSource.getToken(
            clientId = clientId,
            authCode = authCode,
            redirectUri = redirectUri,
            codeVerifier = codeVerifier
        ).map { response ->
            googleAuthDataSource.storeToken(
                tokenType = ACCESS_TOKEN,
                token = checkNotNull(response.accessToken)
            )
            googleAuthDataSource.storeToken(
                tokenType = REFRESH_TOKEN,
                token = checkNotNull(response.refreshToken)
            )
            OAuthTokens(
                response.accessToken,
                checkNotNull(response.refreshToken),
                response.idToken
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

    companion object {
        private const val ACCESS_TOKEN = "accesstoken"
        private const val REFRESH_TOKEN = "refreshtoken"

        private var authRepository: AuthRepository? = null

        fun getAuthRepository(context: Context): AuthRepository {
            if (authRepository == null) {
                val authService: GoogleAuthREST = GoogleRetrofitImpl.instance.googleAuthREST
                val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context)
                val googleAuthDataSource: AuthDataSource = GoogleAuthDataSource(
                    authService = authService,
                    sharedPreferences = sharedPreferences
                )
                authRepository = AuthRepositoryImpl(googleAuthDataSource)
            }

            return authRepository!!
        }
    }
}
