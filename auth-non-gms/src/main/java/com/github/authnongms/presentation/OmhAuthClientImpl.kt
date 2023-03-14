package com.github.authnongms.presentation

import android.content.Context
import android.content.Intent
import com.github.authnongms.domain.user.ProfileUseCase
import com.github.authnongms.presentation.redirect.RedirectActivity
import com.github.openmobilehub.auth.OmhAuthClient
import com.github.openmobilehub.auth.OmhCredentials
import com.github.openmobilehub.auth.models.OmhUserProfile

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
        val profileUseCase = ProfileUseCase.createUserProfileUseCase(context)
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
}
