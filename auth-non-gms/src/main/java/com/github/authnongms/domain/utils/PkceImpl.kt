package com.github.authnongms.domain.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

internal class PkceImpl : Pkce {

    override val codeVerifier: String = generateCodeVerifier()

    private fun getEncoding() = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP

     private fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(SIXTYFOUR_BIT_SIZE)
        secureRandom.nextBytes(bytes)
        return Base64.encodeToString(bytes, getEncoding())
    }

     override fun generateCodeChallenge(): String {
        val bytes = codeVerifier.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes)
        val digest = messageDigest.digest()
        return Base64.encodeToString(digest, getEncoding())
    }

    companion object {
        private const val SIXTYFOUR_BIT_SIZE = 64
    }
}
