package com.omh.android.auth.nongms.domain.auth

import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.domain.models.OAuthTokens
import com.omh.android.auth.nongms.domain.utils.Pkce
import com.omh.android.auth.nongms.domain.utils.PkceImpl

internal class AuthUseCase(
    private val authRepository: AuthRepository,
    private val pkce: Pkce
) {

    fun getLoginUrl(scopes: String, packageName: String, clientId: String): String {
        return authRepository.buildLoginUrl(
            scopes = scopes,
            clientId = clientId,
            codeChallenge = pkce.generateCodeChallenge(),
            redirectUri = REDIRECT_FORMAT.format(packageName)
        )
    }

    suspend fun requestTokens(
        authCode: String,
        packageName: String,
        clientId: String,
    ): ApiResult<OAuthTokens> {
        return authRepository.requestTokens(
            clientId = clientId,
            authCode = authCode,
            redirectUri = REDIRECT_FORMAT.format(packageName),
            codeVerifier = pkce.codeVerifier
        )
    }

    suspend fun blockingRefreshToken(clientId: String): ApiResult<String> {
        return authRepository.refreshAccessToken(clientId)
    }

    fun getAccessToken(): String? = authRepository.getAccessToken()

    fun logout() = authRepository.clearData()

    suspend fun revokeToken(): ApiResult<Unit> {
        val result = authRepository.revokeToken()
        if (result is ApiResult.Success<*>) {
            authRepository.clearData()
        }
        return result
    }

    companion object {
        const val REDIRECT_FORMAT = "%s:/oauth2redirect"

        fun createAuthUseCase(authRepository: AuthRepository): AuthUseCase {
            return AuthUseCase(authRepository, PkceImpl())
        }
    }
}
