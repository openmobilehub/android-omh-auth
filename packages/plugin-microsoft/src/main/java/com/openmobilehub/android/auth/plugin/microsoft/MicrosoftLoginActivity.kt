package com.openmobilehub.android.auth.plugin.microsoft

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.SignInParameters
import com.microsoft.identity.client.exception.MsalException

class MicrosoftLoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scopes = arrayListOf("User.Read")
        val params =
            SignInParameters
                .builder()
                .withActivity(this)
                .withScopes(scopes)
                .withCallback(object : AuthenticationCallback {
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        MicrosoftRepository.getInstance(applicationContext).token =
                            authenticationResult.accessToken
                        setResult(
                            RESULT_OK,
                            Intent().putExtra("accessToken", authenticationResult.accessToken)
                        )
                        finish()
                    }

                    override fun onError(exception: MsalException?) {
                        setResult(RESULT_CANCELED, Intent().putExtra("error", exception?.cause))
                        finish()
                    }

                    override fun onCancel() {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                })
                .build()

        MicrosoftApplication.getInstance().getApplication()
            .signIn(params)
    }
}
