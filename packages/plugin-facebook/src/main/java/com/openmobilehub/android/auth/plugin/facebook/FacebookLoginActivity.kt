package com.openmobilehub.android.auth.plugin.facebook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.openmobilehub.android.auth.core.OmhCallbackManager

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

    override fun onDestroy() {
        super.onDestroy()

        val callbackId = intent.getStringExtra("callbackId") ?: ""
        val callback =
            if (facebookLoginCallback.isLoginSuccessful) {
                OmhCallbackManager.instance.getSuccessCallback(callbackId)
            } else OmhCallbackManager.instance.getErrorCallback(callbackId)

        callback?.let { it(facebookLoginCallback.callbackResult) }

        OmhCallbackManager.instance.removeCallback(callbackId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
