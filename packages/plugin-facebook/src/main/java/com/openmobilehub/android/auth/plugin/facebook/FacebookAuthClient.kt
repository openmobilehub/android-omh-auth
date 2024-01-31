package com.openmobilehub.android.auth.plugin.facebook

import android.content.Context
import android.content.Intent
import android.util.Log
import com.facebook.FacebookSdk
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhAuthStatusCodes
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

    override fun handleLoginIntentResponse(data: Intent?) {
        if (data == null || !data.hasExtra(("accessToken"))) {
            throw OmhAuthException.LoginCanceledException();
        }

        if (data.hasExtra("error")) {
            throw OmhAuthException.RecoverableLoginException(
                OmhAuthStatusCodes.DEVELOPER_ERROR,
                data.getSerializableExtra("error") as Throwable
            );
        }
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

    override fun revokeToken(): OmhTask<Unit> {
        TODO()
    }
}
