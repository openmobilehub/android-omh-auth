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

package com.openmobilehub.android.auth.plugin.google.gms

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import com.openmobilehub.android.auth.plugin.google.gms.util.mapToOmhExceptions

internal class OmhAuthClientImpl(
    private val googleSignInClient: GoogleSignInClient
) : OmhAuthClient {

    override fun initialize(): OmhTask<Unit> {
        return OmhTask({
            // No initialization needed for Google Sign-In
        })
    }

    override fun getLoginIntent(): Intent {
        return Intent(googleSignInClient.applicationContext, GmsLoginActivity::class.java)
            .putExtra("gmsIntent", googleSignInClient.signInIntent)
//        return googleSignInClient.signInIntent
    }

    override fun getUser(): OmhTask<OmhUserProfile> {
        return OmhTask({
            val googleUser =
                GoogleSignIn.getLastSignedInAccount(googleSignInClient.applicationContext)

            if (googleUser == null) {
                throw OmhAuthException.UnrecoverableLoginException(
                    cause = Throwable(message = "No user profile stored")
                )
            }

            return@OmhTask googleUser.toOmhProfile()
        })
    }

    private fun GoogleSignInAccount.toOmhProfile(): OmhUserProfile {
        return OmhUserProfile(
            name = givenName,
            surname = familyName,
            email = email,
            profileImage = photoUrl.toString(),
            idToken = idToken
        )
    }

    override fun getCredentials(): GmsCredentials {
        return GmsCredentials(googleSignInClient.applicationContext)
    }

    override fun signOut(): OmhGmsTask<Unit> {
        val task = googleSignInClient.signOut().mapToOmhExceptions()
        return OmhGmsTask(task)
    }

    override fun revokeToken(): OmhGmsTask<Unit> {
        val task = googleSignInClient.revokeAccess().mapToOmhExceptions()
        return OmhGmsTask(task)
    }
}
