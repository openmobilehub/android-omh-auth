package com.omh.android.auth.gms

import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.async.OmhTask
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhUserProfile
import com.omh.android.auth.api.utils.OmhAuthUtils
import com.omh.android.auth.gms.util.mapToOmhExceptions
import com.omh.android.auth.gms.util.toOmhLoginException

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

    override fun getAccountFromIntent(data: Intent?): OmhUserProfile {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            return account.toOmhProfile()
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
