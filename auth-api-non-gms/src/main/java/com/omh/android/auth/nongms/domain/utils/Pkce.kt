package com.omh.android.auth.nongms.domain.utils

internal interface Pkce {
    val codeVerifier: String

    fun generateCodeChallenge(): String
}
