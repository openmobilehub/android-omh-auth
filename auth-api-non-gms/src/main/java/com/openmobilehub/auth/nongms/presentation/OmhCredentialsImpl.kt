package com.openmobilehub.auth.nongms.presentation

import com.openmobilehub.auth.nongms.domain.auth.AuthUseCase
import com.openmobilehub.auth.nongms.utils.ThreadUtils
import com.openmobilehub.auth.api.OmhCredentials
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class OmhCredentialsImpl(
    private val authUseCase: AuthUseCase,
    clientId: String
) : OmhCredentials {

    init {
        authUseCase.clientId = clientId
    }

    override fun blockingRefreshToken(): String? {
        ThreadUtils.checkForMainThread()
        return runBlocking {
            authUseCase.blockingRefreshToken().first()
        }
    }

    override val accessToken: String?
        get() = authUseCase.getAccessToken()
}
