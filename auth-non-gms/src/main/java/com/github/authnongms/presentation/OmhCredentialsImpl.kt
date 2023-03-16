package com.github.authnongms.presentation

import com.github.authnongms.domain.auth.AuthUseCase
import com.github.authnongms.utils.ThreadUtils
import com.github.openmobilehub.auth.api.OmhCredentials
import com.github.openmobilehub.auth.api.OperationFailureListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

internal class OmhCredentialsImpl(
    private val authUseCase: AuthUseCase,
    clientId: String
) : OmhCredentials {

    init {
        authUseCase.clientId = clientId
    }

    override fun refreshAccessToken(operationFailureListener: OperationFailureListener): String? {
        ThreadUtils.checkForMainThread()
        return runBlocking(Dispatchers.IO) {
            authUseCase.refreshToken()
                .catch { e -> operationFailureListener.onFailure(Exception(e)) }
                .firstOrNull()
        }
    }

    override val accessToken: String?
        get() = authUseCase.getAccessToken()

    override fun logout(operationFailureListener: OperationFailureListener?) {
        ThreadUtils.checkForMainThread()
        runBlocking(Dispatchers.IO) {
            authUseCase.logout()
                .catch { throwable ->
                    val exception = Exception(throwable)
                    operationFailureListener?.onFailure(exception)
                }
                .collect()
        }
    }
}
