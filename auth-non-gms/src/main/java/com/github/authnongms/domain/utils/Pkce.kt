package com.github.authnongms.domain.utils

interface Pkce {
    val codeVerifier: String

    fun generateCodeChallenge(): String
}
