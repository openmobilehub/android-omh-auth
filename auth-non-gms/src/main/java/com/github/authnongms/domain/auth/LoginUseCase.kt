package com.github.authnongms.domain.auth

import android.content.Context
import com.github.authnongms.data.login.AuthRepositoryImpl
import com.github.authnongms.domain.models.OAuthTokens
import com.github.authnongms.domain.utils.Pkce
import com.github.authnongms.domain.utils.PkceImpl
import kotlinx.coroutines.flow.Flow

internal class LoginUseCase(
    private val authRepository: AuthRepository,
    private val pkce: Pkce
) {

    var clientId: String? = null

    fun getLoginUrl(scopes: String, packageName: String): String {
        return authRepository.buildLoginUrl(
            scopes,
            checkNotNull(clientId),
            pkce.generateCodeChallenge(),
            redirectUri = REDIRECT_FORMAT.format(packageName)
        )
    }

    suspend fun requestTokens(authCode: String, packageName: String): Flow<OAuthTokens> {
        return authRepository.requestTokens(
            clientId = checkNotNull(clientId),
            authCode = authCode,
            redirectUri = REDIRECT_FORMAT.format(packageName),
            codeVerifier = pkce.codeVerifier
        )
    }

    companion object {
        const val REDIRECT_FORMAT = "%s:/oauth2redirect"

        fun createLoginUseCase(applicationContext: Context): LoginUseCase {
            val authRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
            return LoginUseCase(authRepository, PkceImpl())
        }
    }
}
