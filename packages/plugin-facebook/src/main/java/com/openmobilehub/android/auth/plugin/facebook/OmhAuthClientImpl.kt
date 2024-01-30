package com.openmobilehub.android.auth.plugin.facebook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.openmobilehub.android.auth.core.models.OmhUserProfile

class OmhAuthClientImpl {
    fun getLoginIntent(activity: Activity): Intent {
        return Intent(activity, OmhAuthActivity::class.java)
    }

//    override fun getUser(): OmhUserProfile? {
//    }

    fun getUser() {
        val params = Bundle().apply {
            putString("fields", "first_name,last_name,email,picture")
        }

        val request = GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me",
            params,
            HttpMethod.GET,
            GraphRequest.Callback { response ->
                println("[FB Auth]- Response $response")

//                val omhUserProfile = OmhUserProfile(
//                    name = response.jsonObject?.getString("first_name"),
//                    surname = response.jsonObject?.getString("last_name"),
//                    email = response.jsonObject?.optString("email", "test@email.com"),
//                    profileImage = response.jsonObject?.getJSONObject("picture")?.
//                        getJSONObject("data")?.getString("url")
//                )
            }
        )

        request.executeAsync()
    }

//    override fun getCredentials(): Any? {
//        TODO()
//    }
//
//    override fun signOut(): OmhTask<Unit> {
//        TODO()
//    }
//
//    override fun getAccountFromIntent(data: Intent?): OmhUserProfile {
//        TODO()
//    }
//
//    override fun revokeToken() {
//       TODO()
//    }
}
