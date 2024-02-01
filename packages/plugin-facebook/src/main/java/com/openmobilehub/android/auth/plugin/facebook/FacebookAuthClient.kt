package com.openmobilehub.android.auth.plugin.facebook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.Profile
import com.facebook.ProfileManager
import com.facebook.bolts.Task
import com.facebook.login.LoginManager
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhCancellable
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhAuthStatusCodes
import com.openmobilehub.android.auth.core.models.OmhUserProfile

class FacebookAuthClient(
    val scopes: ArrayList<String>,
    val context: Context,
) :
    OmhAuthClient {
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
        val profile = Profile.getCurrentProfile() ?: return null

        return OmhUserProfile(
            name = profile.firstName,
            surname = profile.lastName,
            email = null,
            profileImage = profile.getProfilePictureUri(1024, 1024).toString(),
        )
    }

    override fun getCredentials(): Any? {
        TODO()
    }

    override fun signOut(): OmhTask<Unit> {
        LoginManager.getInstance().logOut()
        return SimpleTask
    }

    override fun revokeToken(): OmhTask<Unit> {
        TODO()
    }

    companion object SimpleTask: OmhTask<Unit>() {
        override fun execute(): OmhCancellable? {
            return null
        }
    }
}
