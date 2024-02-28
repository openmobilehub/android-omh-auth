package com.openmobilehub.android.auth.plugin.facebook

import android.app.Activity
import android.os.Bundle
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult

internal class FacebookLoginCallback(val activity: Activity) {
    var isLoginSuccessful = false
    var callbackResult = Bundle()

    fun getLoginCallback(): FacebookCallback<LoginResult> {
        return object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                isLoginSuccessful = true
                activity.finish()
            }

            override fun onCancel() {
                isLoginSuccessful = false
                activity.finish()
            }

            override fun onError(error: FacebookException) {
                isLoginSuccessful = false
                callbackResult.putSerializable("error", error.cause)
                activity.finish()
            }
        }
    }
}
