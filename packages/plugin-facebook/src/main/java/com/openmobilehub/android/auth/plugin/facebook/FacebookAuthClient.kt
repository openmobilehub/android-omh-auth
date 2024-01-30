package com.openmobilehub.android.auth.plugin.facebook

import android.content.Context
import android.content.Intent
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile

class FacebookAuthClient(
    val scopes: ArrayList<String>,
    val context: Context,
    val applicationName: String,
    val applicationId: String,
    val clientToken: String
) :
    OmhAuthClient {
    init {
        FacebookSdk.setApplicationName(applicationName)
        FacebookSdk.setApplicationId(applicationId)
        FacebookSdk.setClientToken(clientToken)
        FacebookSdk.sdkInitialize(context)
    }

    override fun getLoginIntent(): Intent {
        return Intent(
            context, FacebookLoginActivity::class.java
        ).putStringArrayListExtra("scopes", scopes)
    }

    override fun getUser(): OmhUserProfile? {
        TODO()
    }

    override fun getCredentials(): Any? {
        TODO()
    }

    override fun signOut(): OmhTask<Unit> {
        TODO()
    }

    @Throws(OmhAuthException::class)
    override fun getAccountFromIntent(data: Intent?): OmhUserProfile {
        TODO()
    }

    override fun revokeToken(): OmhTask<Unit> {
        TODO()
    }
}
