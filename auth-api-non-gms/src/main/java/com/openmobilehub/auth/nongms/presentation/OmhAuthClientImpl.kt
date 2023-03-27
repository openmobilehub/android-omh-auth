package com.openmobilehub.auth.nongms.presentation

import android.content.Context
import android.content.Intent
import com.openmobilehub.auth.nongms.data.user.UserRepositoryImpl
import com.openmobilehub.auth.nongms.domain.user.ProfileUseCase
import com.openmobilehub.auth.nongms.presentation.redirect.RedirectActivity
import com.openmobilehub.auth.api.OmhAuthClient
import com.openmobilehub.auth.api.models.OmhUserProfile
import com.openmobilehub.auth.nongms.data.login.AuthRepositoryImpl
import com.openmobilehub.auth.nongms.domain.auth.AuthUseCase

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

    override fun signOut() {
        val authRepository = AuthRepositoryImpl.getAuthRepository(applicationContext)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        authUseCase.logout()
    }
}
