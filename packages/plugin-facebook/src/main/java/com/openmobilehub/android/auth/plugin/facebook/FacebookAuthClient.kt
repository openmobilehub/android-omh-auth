package com.openmobilehub.android.auth.plugin.facebook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.HttpMethod
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

        fetchAndSaveUserData()
    }

    override fun getUser(): OmhUserProfile? {
        val userData = EncryptedSharedPreferences(context).getUserData() ?: return null

        return OmhUserProfile(
            userData.firstName,
            userData.lastName,
            userData.email,
            userData.pictureUrl
        )
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

    private fun fetchAndSaveUserData() {
        val params = Bundle().apply {
            putString("fields", "first_name,last_name,email,picture")
        }

        val request = GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me",
            params,
            HttpMethod.GET,
            GraphRequest.Callback { response ->
                EncryptedSharedPreferences(context).saveUserData(
                    UserData(
                        id = response.jsonObject?.getString("id"),
                        firstName = response.jsonObject?.getString("first_name"),
                        lastName = response.jsonObject?.getString("last_name"),
                        email = response.jsonObject?.optString("email", "test@test.com"),
                        pictureUrl = response.jsonObject?.getJSONObject("picture")
                            ?.getJSONObject("data")?.getString("url"),
                    )
                )
            }
        )

        request.executeAsync()
    }
}
