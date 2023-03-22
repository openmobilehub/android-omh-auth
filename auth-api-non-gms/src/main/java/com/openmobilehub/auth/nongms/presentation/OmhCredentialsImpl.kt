package com.openmobilehub.auth.nongms.presentation

import com.openmobilehub.auth.api.OmhCredentials
import com.openmobilehub.auth.nongms.domain.auth.AuthUseCase
import com.openmobilehub.auth.nongms.domain.models.ApiResult
import com.openmobilehub.auth.nongms.utils.ThreadUtils
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
            when (val apiResult = authUseCase.blockingRefreshToken()) {
                is ApiResult.Error -> null
                is ApiResult.Success -> apiResult.data
            }
        }
    }

    override val accessToken: String?
        get() = authUseCase.getAccessToken()
}
