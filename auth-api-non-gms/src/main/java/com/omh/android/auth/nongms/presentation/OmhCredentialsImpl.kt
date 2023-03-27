package com.omh.android.auth.nongms.presentation

import com.omh.android.auth.api.OmhCredentials
import com.omh.android.auth.nongms.domain.auth.AuthUseCase
import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.utils.ThreadUtils
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
