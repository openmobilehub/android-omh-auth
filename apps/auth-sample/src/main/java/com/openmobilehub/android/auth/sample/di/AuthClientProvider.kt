package com.openmobilehub.android.auth.sample.di

import com.facebook.AccessToken
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.plugin.facebook.FacebookAuthClient
import javax.inject.Inject

class AuthClientProvider @Inject constructor(
    val googleAuthClient: OmhAuthClient,
    val facebookAuthClient: FacebookAuthClient
) {
    fun getClient(): OmhAuthClient {
        // TODO: Include other providers and make it async

        val fbAccessToken = AccessToken.getCurrentAccessToken()
        if (fbAccessToken != null && !fbAccessToken.isExpired) {
            return facebookAuthClient
        }

        return googleAuthClient
    }
}