package com.openmobilehub.android.auth.plugin.facebook

import android.content.Context
import android.content.Intent
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhCancellable
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhAuthStatusCodes
import com.openmobilehub.android.auth.core.models.OmhUserProfile

const val PROFILE_PICTURE_SIZE = 1024

class FacebookAuthClient(val scopes: ArrayList<String>, val context: Context) : OmhAuthClient {
    override fun getLoginIntent(): Intent {
        return Intent(
            context, FacebookLoginActivity::class.java
        ).putStringArrayListExtra("scopes", scopes)
    }

    override fun handleLoginIntentResponse(data: Intent?) {
        if (data == null || !data.hasExtra(("accessToken"))) {
            throw OmhAuthException.LoginCanceledException()
        }

        if (data.hasExtra("error")) {
            throw OmhAuthException.RecoverableLoginException(
                OmhAuthStatusCodes.DEVELOPER_ERROR,
                data.getSerializableExtra("error") as Throwable
            )
        }
    }

    override fun getUser(): OmhUserProfile? {
        val profile = Profile.getCurrentProfile() ?: return null

        return OmhUserProfile(
            name = profile.firstName,
            surname = profile.lastName,
            email = null,
            profileImage = profile.getProfilePictureUri(PROFILE_PICTURE_SIZE, PROFILE_PICTURE_SIZE)
                .toString(),
        )
    }

    override fun getCredentials(): Any? {
        TODO()
    }

    override fun signOut(): OmhTask<Unit> {
        val task = FacebookTask()

        task.addOnExecute { LoginManager.getInstance().logOut() }

        return task
    }

    override fun revokeToken(): OmhTask<Unit> {
        TODO()
    }
}
