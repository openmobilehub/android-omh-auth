package com.github.authnongms.domain.auth

import android.content.Context
import com.github.authnongms.data.login.AuthRepositoryImpl
import com.github.authnongms.domain.models.OAuthTokens
import com.github.authnongms.domain.utils.Pkce
import com.github.authnongms.domain.utils.PkceImpl
import kotlinx.coroutines.flow.Flow

internal class AuthUseCase(
    private val authRepository: AuthRepository,
    private val pkce: Pkce
) {

    var clientId: String? = null
    private val _clientId: String
        get() = checkNotNull(clientId)

    fun getLoginUrl(scopes: String, packageName: String): String {
        return authRepository.buildLoginUrl(
            scopes = scopes,
            clientId = _clientId,
            codeChallenge = pkce.generateCodeChallenge(),
            redirectUri = REDIRECT_FORMAT.format(packageName)
        )
    }

    suspend fun requestTokens(authCode: String, packageName: String): Flow<OAuthTokens> {
        return authRepository.requestTokens(
            clientId = _clientId,
            authCode = authCode,
            redirectUri = REDIRECT_FORMAT.format(packageName),
            codeVerifier = pkce.codeVerifier
        )
    }

    suspend fun refreshToken(): Flow<String> {
        return authRepository.refreshAccessToken(_clientId)
    }

    fun getAccessToken(): String? {
        return authRepository.getAccessToken()
    }

    companion object {
        const val REDIRECT_FORMAT = "%s:/oauth2redirect"

        fun createAuthUseCase(applicationContext: Context): AuthUseCase {
            val authRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
            return AuthUseCase(authRepository, PkceImpl())
        }
    }
}
