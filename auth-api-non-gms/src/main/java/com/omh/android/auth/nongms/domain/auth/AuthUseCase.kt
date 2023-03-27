package com.omh.android.auth.nongms.domain.auth

import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.domain.models.OAuthTokens
import com.omh.android.auth.nongms.domain.utils.Pkce
import com.omh.android.auth.nongms.domain.utils.PkceImpl

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

    suspend fun requestTokens(authCode: String, packageName: String): ApiResult<OAuthTokens> {
        return authRepository.requestTokens(
            clientId = _clientId,
            authCode = authCode,
            redirectUri = REDIRECT_FORMAT.format(packageName),
            codeVerifier = pkce.codeVerifier
        )
    }

    suspend fun blockingRefreshToken(): ApiResult<String> {
        return authRepository.refreshAccessToken(_clientId)
    }

    fun getAccessToken(): String? = authRepository.getAccessToken()

    fun logout() = authRepository.clearData()

    companion object {
        const val REDIRECT_FORMAT = "%s:/oauth2redirect"

        fun createAuthUseCase(authRepository: AuthRepository): AuthUseCase {
            return AuthUseCase(authRepository, PkceImpl())
        }
    }
}
