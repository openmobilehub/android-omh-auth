package com.openmobilehub.android.auth.sample.di

import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.plugin.facebook.FacebookAuthClient
import javax.inject.Inject

class AuthClientProvider @Inject constructor(val googleAuthClient: OmhAuthClient, val facebookAuthClient: FacebookAuthClient) {
    fun getClient(): OmhAuthClient {
        if (googleAuthClient.getUser() != null) {
            return googleAuthClient
        }

        if (facebookAuthClient.getUser() != null) {
            return facebookAuthClient
        }

        throw Exception("No user logged in")
    }
}