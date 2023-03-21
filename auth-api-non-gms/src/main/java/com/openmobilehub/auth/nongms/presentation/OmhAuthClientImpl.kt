package com.openmobilehub.auth.nongms.presentation

import android.content.Context
import android.content.Intent
import com.openmobilehub.auth.nongms.data.user.UserRepositoryImpl
import com.openmobilehub.auth.nongms.domain.user.ProfileUseCase
import com.openmobilehub.auth.nongms.presentation.redirect.RedirectActivity
import com.openmobilehub.auth.api.OmhAuthClient
import com.openmobilehub.auth.api.OmhCredentials
import com.openmobilehub.auth.api.models.OmhUserProfile
import com.openmobilehub.auth.nongms.data.login.AuthRepositoryImpl
import com.openmobilehub.auth.nongms.domain.auth.AuthRepository
import com.openmobilehub.auth.nongms.domain.auth.AuthUseCase

/**
 * Non GMS implementation of the OmhAuthClient abstraction. Required a clientId and defined scopes as
 * no extra scopes can be accessed in the future.
 */
internal class OmhAuthClientImpl(private val clientId: String, private val scopes: String) :
    OmhAuthClient {

    override fun getLoginIntent(context: Context): Intent {
        return Intent(context, RedirectActivity::class.java)
            .putExtra(RedirectActivity.CLIENT_ID, clientId)
            .putExtra(RedirectActivity.SCOPES, scopes)
    }

    override fun getUser(context: Context): OmhUserProfile? {
        val userRepository = UserRepositoryImpl.getUserRepository(context)
        val profileUseCase = ProfileUseCase.createUserProfileUseCase(userRepository)
        return profileUseCase.getProfileData()
    }

    internal class Builder(
        private var clientId: String,
        private var authScope: String
    ) : OmhAuthClient.Builder {

        // TODO Add optional parameters like scopes

        override fun build(): OmhAuthClient {
            return OmhAuthClientImpl(clientId, authScope)
        }
    }

    override fun getCredentials(context: Context): OmhCredentials {
        return OmhAuthFactory.getCredentials(clientId, context)
    }

    override fun signOut(context: Context) {
        val authRepository = AuthRepositoryImpl.getAuthRepository(context)
        val authUseCase = AuthUseCase.createAuthUseCase(authRepository)
        authUseCase.logout()
    }
}
