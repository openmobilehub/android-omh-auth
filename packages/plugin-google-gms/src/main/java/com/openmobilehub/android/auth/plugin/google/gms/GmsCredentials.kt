package com.openmobilehub.android.auth.plugin.google.gms

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.OmhTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GmsCredentials(private val context: Context) : OmhCredentials {
    // Must be run from a background thread, not the main UI thread.
    override val accessToken: String?
        get() = googleAccountCredential?.token

    val googleAccountCredential: GoogleAccountCredential?
        get() {
            val lastSignedInAccount: GoogleSignInAccount =
                GoogleSignIn.getLastSignedInAccount(context) ?: return null
            val scopes = lastSignedInAccount.grantedScopes.map { scope -> scope.scopeUri }
            return GoogleAccountCredential.usingOAuth2(context, scopes).apply {
                selectedAccount = lastSignedInAccount.account
            }
        }

    override fun refreshToken(): OmhTask<String?> {
        return OmhTask({
            withContext(Dispatchers.IO) {
                googleAccountCredential?.token
            }
        })
    }
}
