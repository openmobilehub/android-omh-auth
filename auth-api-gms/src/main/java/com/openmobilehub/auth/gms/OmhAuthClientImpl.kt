package com.openmobilehub.auth.gms

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.openmobilehub.auth.api.OmhAuthClient
import com.openmobilehub.auth.api.OmhCredentials
import com.openmobilehub.auth.api.models.OmhUserProfile

internal class OmhAuthClientImpl(
    private val googleSignInClient: GoogleSignInClient
) : OmhAuthClient {

    override fun getLoginIntent(context: Context): Intent {
        return googleSignInClient.signInIntent
    }

    override fun getUser(context: Context): OmhUserProfile? {
        val googleUser = GoogleSignIn.getLastSignedInAccount(context)
        return googleUser?.toOmhProfile()
    }

    private fun GoogleSignInAccount?.toOmhProfile(): OmhUserProfile? {
        if (this == null) return null
        return OmhUserProfile(
            name = givenName,
            surname = familyName,
            email = email,
            profileImage = photoUrl.toString()
        )
    }

    override fun getCredentials(context: Context): OmhCredentials {
        return OmhAuthFactory.getCredentials()
    }

    override fun signOut(context: Context) {
        googleSignInClient.signOut()
    }
}
