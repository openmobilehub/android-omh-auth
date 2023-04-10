package com.omh.android.auth.nongms.presentation

import com.omh.android.auth.api.OmhCredentials
import com.omh.android.auth.nongms.domain.auth.AuthUseCase
import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.utils.ThreadUtils
import kotlinx.coroutines.runBlocking

internal class OmhCredentialsImpl(
    private val authUseCase: AuthUseCase,
    private val clientId: String
) : OmhCredentials {

    override fun blockingRefreshToken(): String? {
        ThreadUtils.checkForMainThread()
        return runBlocking {
            when (val apiResult = authUseCase.blockingRefreshToken(clientId)) {
                is ApiResult.Success -> apiResult.data
                else -> null
            }
        }
    }

    override val accessToken: String?
        get() = authUseCase.getAccessToken()
}
