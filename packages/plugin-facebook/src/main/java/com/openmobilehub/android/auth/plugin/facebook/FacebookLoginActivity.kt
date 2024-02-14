package com.openmobilehub.android.auth.plugin.facebook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.CallbackManager
import com.facebook.login.LoginManager

internal class FacebookLoginActivity : Activity() {
    private val callbackManager = CallbackManager.Factory.create()
    private val facebookLoginCallback = FacebookLoginCallback(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scopes = intent.getStringArrayListExtra("scopes")

        LoginManager.getInstance()
            .registerCallback(callbackManager, facebookLoginCallback.getLoginCallback())

        LoginManager.getInstance().logIn(this, scopes)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
