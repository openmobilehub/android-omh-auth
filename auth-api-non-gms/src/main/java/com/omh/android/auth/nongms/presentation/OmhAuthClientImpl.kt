package com.omh.android.auth.nongms.presentation

import android.content.Context
import android.content.Intent
import com.omh.android.auth.nongms.data.user.UserRepositoryImpl
import com.omh.android.auth.nongms.domain.user.ProfileUseCase
import com.omh.android.auth.nongms.presentation.redirect.RedirectActivity
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes
import com.omh.android.auth.api.models.OmhUserProfile
import com.omh.android.auth.nongms.data.login.AuthRepositoryImpl
import com.omh.android.auth.nongms.domain.auth.AuthUseCase
import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.utils.Constants
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Non GMS implementation of the OmhAuthClient abstraction. Required a clientId and defined scopes as
 * no extra scopes can be accessed in the future.
 */
internal class OmhAuthClientImpl(
    private val clientId: String,
    private val scopes: String,
    context: Context
) : OmhAuthClient {

    private val applicationContext: Context

    init {
        applicationContext = context.applicationContext
    }

    override fun getLoginIntent(): Intent {
        return Intent(applicationContext, RedirectActivity::class.java)
            .putExtra(RedirectActivity.CLIENT_ID, clientId)
            .putExtra(RedirectActivity.SCOPES, scopes)
    }

    override fun getUser(): OmhUserProfile? {
        val userRepository = UserRepositoryImpl.getUserRepository(applicationContext)
        val profileUseCase = ProfileUseCase.createUserProfileUseCase(userRepository)
        return profileUseCase.getProfileData()
    }

    internal class Builder(
        private var clientId: String,
    ) : OmhAuthClient.Builder {

        private var authScope: String = ""

        fun addScope(scope: String): Builder {
            authScope += " $scope"
            authScope.trim()
            return this
        }

        override fun build(context: Context): OmhAuthClient {
            return OmhAuthClientImpl(clientId, authScope, context)
        }
    }

    override fun getCredentials(): Any {
        return OmhAuthFactoryImpl.getCredentials(clientId, applicationContext)
    }

    @SuppressWarnings("TooGenericExceptionCaught") // Until we find any specific errors for this.
    override fun signOut(
        onFailure: (OmhAuthException) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ) {
        try {
            val authRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
            val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
            authUseCase.logout()
            onSuccess()
        } catch (exception: RuntimeException) {
            val omhException = OmhAuthException.ApiException(
                OmhAuthStatusCodes.INTERNAL_ERROR,
                exception
            )
            onFailure(omhException)
        } finally {
            onComplete()
        }
    }

    override fun getAccountFromIntent(data: Intent?): OmhUserProfile {
        if (data?.hasExtra(Constants.CAUSE_KEY) == true) {
            val exception = data.getSerializableExtra(Constants.CAUSE_KEY) as OmhAuthException
            throw exception
        }
        return getUser() ?: throw OmhAuthException.UnrecoverableLoginException(
            cause = Throwable(message = "No user profile stored")
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun revokeToken(
        onSuccess: () -> Unit,
        onFailure: (OmhAuthException) -> Unit,
        onComplete: () -> Unit,
    ) {
        val authRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        GlobalScope.launch { // TODO Look for better ways of handling this
            val result = authUseCase.revokeToken()
            withContext(Dispatchers.Main) {
                result.handleOutcome(onFailure, onSuccess)
                onComplete()
            }
        }
    }

    private fun ApiResult<Unit>.handleOutcome(
        onFailure: (OmhAuthException) -> Unit,
        onSuccess: () -> Unit
    ) {
        return when (this) {
            is ApiResult.Error -> handleError(onFailure)
            is ApiResult.Success -> onSuccess()
        }
    }

    private fun ApiResult.Error.handleError(onFailure: (OmhAuthException) -> Unit) {
        val statusCode = when (this) {
            is ApiResult.Error.ApiError -> OmhAuthStatusCodes.HTTPS_ERROR
            is ApiResult.Error.NetworkError -> OmhAuthStatusCodes.NETWORK_ERROR
            is ApiResult.Error.RuntimeError -> OmhAuthStatusCodes.INTERNAL_ERROR
        }
        val apiException = OmhAuthException.ApiException(statusCode, cause)
        onFailure(apiException)
    }
}
