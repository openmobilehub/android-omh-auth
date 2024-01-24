/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.auth.plugin.google.nongms.domain.utils

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
