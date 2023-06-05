package com.omh.android.auth.nongms.domain.models

internal data class OAuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String
)
