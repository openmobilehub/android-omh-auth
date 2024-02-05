package com.openmobilehub.android.auth.plugin.facebook

import com.facebook.AccessToken
import com.facebook.AuthenticationToken

data class FacebookCredentials(
    val authenticationToken: AuthenticationToken?,
    val accessToken: AccessToken?
)
