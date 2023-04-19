package com.omh.android.auth.gms

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes
import com.omh.android.auth.api.models.OmhUserProfile
import java.lang.Exception

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

    override fun signOut(
        onFailure: (OmhAuthException) -> Unit,
        onSuccess: () -> Unit,
        onComplete: () -> Unit
    ) {
        googleSignInClient.signOut()
            .addOnFailureListener { exception: Exception ->
                toOmhApiException(exception, onFailure)
            }
            .addOnSuccessListener { onSuccess() }
            .addOnCompleteListener { onComplete() }
    }

    override fun getAccountFromIntent(data: Intent?): OmhUserProfile {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            return account.toOmhProfile()
        } catch (apiException: ApiException) {
            val omhException: OmhAuthException = toOmhLoginException(apiException)
            throw omhException
        }
    }

    private fun toOmhLoginException(apiException: ApiException) = when (apiException.statusCode) {
        GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
            OmhAuthException.LoginCanceledException(apiException.cause)
        }
        GoogleSignInStatusCodes.SIGN_IN_FAILED -> {
            OmhAuthException.UnrecoverableLoginException(apiException.cause)
        }
        else -> {
            val omhStatusCode = when (apiException.statusCode) {
                CommonStatusCodes.NETWORK_ERROR -> OmhAuthStatusCodes.NETWORK_ERROR
                CommonStatusCodes.DEVELOPER_ERROR -> OmhAuthStatusCodes.DEVELOPER_ERROR
                else -> OmhAuthStatusCodes.INTERNAL_ERROR
            }
            OmhAuthException.RecoverableLoginException(omhStatusCode, apiException.cause)
        }
    }

    override fun revokeToken(
        onSuccess: () -> Unit,
        onFailure: (OmhAuthException) -> Unit,
        onComplete: () -> Unit
    ) {
        googleSignInClient.revokeAccess()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                toOmhApiException(exception, onFailure)
            }
            .addOnCompleteListener { onComplete() }
    }

    private fun toOmhApiException(
        exception: Exception,
        onFailure: (OmhAuthException) -> Unit
    ) {
        val apiException: ApiException? = exception as? ApiException
        val statusCode: Int = when (apiException?.statusCode) {
            CommonStatusCodes.API_NOT_CONNECTED -> OmhAuthStatusCodes.GMS_UNAVAILABLE
            else -> OmhAuthStatusCodes.INTERNAL_ERROR
        }
        val omhException = OmhAuthException.ApiException(
            statusCode = statusCode,
            cause = exception
        )
        onFailure(omhException)
    }
}
