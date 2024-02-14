package com.openmobilehub.android.auth.plugin.facebook

import android.app.Activity
import android.content.Intent
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult

internal class FacebookLoginCallback(val activity: Activity) {
    fun getLoginCallback(): FacebookCallback<LoginResult> {
        return object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                onSuccessCb(result)
            }

            override fun onCancel() {
                onCancelCb()
            }

            override fun onError(error: FacebookException) {
                onErrorCb(error)
            }
        }
    }

    private fun onSuccessCb(result: LoginResult) {
        activity.setResult(
            Activity.RESULT_OK,
            Intent()
                .putExtra("accessToken", result.accessToken)
                .putExtra("authenticationToken", result.authenticationToken)
        )
        activity.finish()
    }

    private fun onCancelCb() {
        activity.setResult(Activity.RESULT_CANCELED)
        activity.finish()
    }

    private fun onErrorCb(error: FacebookException) {
        activity.setResult(Activity.RESULT_CANCELED, Intent().putExtra("error", error.cause))
        activity.finish()
    }
}
