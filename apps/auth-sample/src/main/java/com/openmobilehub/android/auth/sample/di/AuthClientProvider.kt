package com.openmobilehub.android.auth.sample.di

import com.facebook.AccessToken
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.plugin.facebook.FacebookAuthClient
import javax.inject.Inject

class AuthClientProvider @Inject constructor(
    val googleAuthClient: OmhAuthClient,
    val facebookAuthClient: FacebookAuthClient
) {
    suspend fun getClient(): OmhAuthClient {
        if (googleAuthClient.getUser() != null) {
            return googleAuthClient
        }

        val fbAccessToken = AccessToken.getCurrentAccessToken()
        if (fbAccessToken != null && !fbAccessToken.isExpired) {
            return facebookAuthClient
        }

        throw Exception("No user logged in")
    }
}