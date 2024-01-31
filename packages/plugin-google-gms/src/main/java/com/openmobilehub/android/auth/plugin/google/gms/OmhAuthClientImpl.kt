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
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import com.openmobilehub.android.auth.core.utils.OmhAuthUtils
import com.openmobilehub.android.auth.plugin.google.gms.util.mapToOmhExceptions
import com.openmobilehub.android.auth.plugin.google.gms.util.toOmhLoginException

internal class OmhAuthClientImpl(
    private val googleSignInClient: GoogleSignInClient
) : OmhAuthClient {

    override fun getLoginIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    override fun getUser(): OmhUserProfile? {
        val googleUser = GoogleSignIn.getLastSignedInAccount(googleSignInClient.applicationContext)
        return googleUser?.toOmhProfile()
    }

    private fun GoogleSignInAccount.toOmhProfile(): OmhUserProfile {
        return OmhUserProfile(
            name = givenName,
            surname = familyName,
            email = email,
            profileImage = photoUrl.toString()
        )
    }

    override fun getCredentials(): Any? {
        val context = googleSignInClient.applicationContext
        val lastSignedInAccount: GoogleSignInAccount =
            GoogleSignIn.getLastSignedInAccount(context) ?: return null
        val scopes = lastSignedInAccount.grantedScopes.map { scope -> scope.scopeUri }
        return GoogleAccountCredential.usingOAuth2(context, scopes).apply {
            selectedAccount = lastSignedInAccount.account
        }
    }

    override fun signOut(): OmhTask<Unit> {
        val task: Task<Unit> = googleSignInClient.signOut().mapToOmhExceptions()
        return OmhGmsTask(task)
    }

    override fun handleLoginIntentResponse(data: Intent?) {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            task.getResult(ApiException::class.java)
        } catch (apiException: ApiException) {
            val isRunningOnNonGms = !OmhAuthUtils.isGmsDevice(googleSignInClient.applicationContext)
            val omhException: OmhAuthException = toOmhLoginException(
                apiException = apiException,
                isNonGmsDevice = isRunningOnNonGms
            )
            throw omhException
        }
    }

    override fun revokeToken(): OmhTask<Unit> {
        val task: Task<Unit> = googleSignInClient.revokeAccess().mapToOmhExceptions()
        return OmhGmsTask(task)
    }
}
