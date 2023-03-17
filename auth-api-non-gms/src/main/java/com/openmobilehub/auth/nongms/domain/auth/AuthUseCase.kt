package com.openmobilehub.auth.nongms.domain.auth

import com.openmobilehub.auth.nongms.domain.models.OAuthTokens
import com.openmobilehub.auth.nongms.domain.utils.Pkce
import com.openmobilehub.auth.nongms.domain.utils.PkceImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion

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

    suspend fun refreshToken(): Flow<String> = authRepository.refreshAccessToken(_clientId)

    fun getAccessToken(): String? = authRepository.getAccessToken()

    suspend fun logout(): Flow<Unit> {
        return authRepository
            .revokeToken()
            .onCompletion { authRepository.clearData() }
    }

    companion object {
        const val REDIRECT_FORMAT = "%s:/oauth2redirect"

        fun createAuthUseCase(authRepository: AuthRepository): AuthUseCase {
            return AuthUseCase(authRepository, PkceImpl())
        }
    }
}
