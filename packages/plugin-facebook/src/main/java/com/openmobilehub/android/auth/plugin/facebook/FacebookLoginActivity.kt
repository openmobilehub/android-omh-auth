package com.openmobilehub.android.auth.plugin.facebook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

class FacebookLoginActivity : Activity() {
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scopes = intent.getStringArrayListExtra("scopes")

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    setResult(Activity.RESULT_OK,
                        Intent()
                            .putExtra("accessToken", result.accessToken)
                            .putExtra("authenticationToken", result.accessToken.token)
                    )
                    finish()
                }

                override fun onCancel() {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }

                override fun onError(error: FacebookException) {
                    setResult(Activity.RESULT_CANCELED, Intent().putExtra("error", error))
                    finish()
                }
            })

        LoginManager.getInstance().logIn(this, scopes)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
