package com.openmobilehub.android.auth.plugin.facebook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhAuthStatusCodes
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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

    override suspend fun getUser(): OmhUserProfile = suspendCoroutine { continuation ->
        val request: GraphRequest = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken(),
        ) { jsonObject, _ ->
            if (jsonObject == null) {
                continuation.resumeWithException(Exception("Failed to get user data"))
            } else {
                continuation.resume(
                    OmhUserProfile(
                        jsonObject.getString("first_name"),
                        jsonObject.getString("last_name"),
                        jsonObject.optString("email", ""),
                        jsonObject.getJSONObject("picture").getJSONObject("data").getString("url")
                    )
                )
            }
        }

        val params = Bundle().apply {
            putString("fields", "first_name,last_name,email,picture")
        }

        request.parameters = params

        request.executeAsync()
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
