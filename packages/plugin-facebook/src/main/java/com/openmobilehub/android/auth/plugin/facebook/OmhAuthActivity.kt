package com.openmobilehub.android.auth.plugin.facebook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

class OmhAuthActivity: Activity() {
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LoginManager.getInstance().registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                println("[FB Auth] - SUCCESS: Access Token: ${result.accessToken.token}")
                finish()
            }

            override fun onCancel() {
                println("[FB Auth] - CANCEL")
                finish()
            }

            override fun onError(error: FacebookException) {
                println("[FB Auth] - ERROR ${error.message}")
                finish()
            }
        })

        val scopes = listOf("public_profile");

        LoginManager.getInstance().logIn(this, scopes)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
