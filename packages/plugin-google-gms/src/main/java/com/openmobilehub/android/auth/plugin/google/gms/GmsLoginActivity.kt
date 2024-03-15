package com.openmobilehub.android.auth.plugin.google.gms

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.utils.OmhAuthUtils
import com.openmobilehub.android.auth.plugin.google.gms.util.toOmhLoginException

private const val GMS_REQUEST_CODE = 0

internal class GmsLoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gmsIntent = intent.getParcelableExtra("gmsIntent") as Intent?

        startActivityForResult(gmsIntent, GMS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GMS_REQUEST_CODE) {
            try {
                handleGmsIntentResponse(data)
                setResult(RESULT_OK)
            } catch (e: OmhAuthException) {
                setResult(RESULT_CANCELED,
                    Intent().apply {
                        putExtra("errorMessage", e.message)
                    }
                )
            } finally {
                finish()
            }
        }
    }

    private fun handleGmsIntentResponse(data: Intent?) {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

        try {
            task.getResult(ApiException::class.java)
        } catch (apiException: ApiException) {
            val isRunningOnNonGms =
                !OmhAuthUtils.isGmsDevice(applicationContext)
            val omhException: OmhAuthException = toOmhLoginException(
                apiException = apiException,
                isNonGmsDevice = isRunningOnNonGms
            )
            throw omhException
        }
    }
}
