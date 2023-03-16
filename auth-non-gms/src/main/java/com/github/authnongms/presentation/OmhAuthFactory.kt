package com.github.authnongms.presentation

import android.content.Context
import com.github.authnongms.data.login.AuthRepositoryImpl
import com.github.authnongms.domain.auth.AuthRepository
import com.github.authnongms.domain.auth.AuthUseCase
import com.github.openmobilehub.auth.api.OmhAuthClient
import com.github.openmobilehub.auth.api.OmhCredentials

object OmhAuthFactory {

    /**
     * Creates an auth client for the user of the non GMS type and returns it as the abstraction.
     * This should be used by the core plugin only.
     */
    fun getAuthClient(clientId: String, scopes: String): OmhAuthClient {
        val builder = OmhAuthClientImpl.Builder(clientId, scopes)
        return builder.build()
    }

    fun getCredentials(clientId: String, context: Context): OmhCredentials {
        val authRepository: AuthRepository = AuthRepositoryImpl.getAuthRepository(context)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        return OmhCredentialsImpl(authUseCase, clientId)
    }
}
