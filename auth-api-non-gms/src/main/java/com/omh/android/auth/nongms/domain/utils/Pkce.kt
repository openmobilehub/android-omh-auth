package com.omh.android.auth.nongms.domain.utils

interface Pkce {
    val codeVerifier: String

    fun generateCodeChallenge(): String
}
