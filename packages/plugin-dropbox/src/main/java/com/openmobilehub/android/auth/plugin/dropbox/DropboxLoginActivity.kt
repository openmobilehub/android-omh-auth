package com.openmobilehub.android.auth.plugin.dropbox

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.openmobilehub.android.auth.core.models.OmhAuthException

internal class DropboxLoginActivity : Activity() {
    private val dropboxRepository = DropboxRepository.getInstance(this)
    private var isFirstResume = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scopes = intent.getStringArrayListExtra("scopes")

        val appId = intent.getStringExtra("appId")

        val requestConfig = DbxRequestConfig("db-${appId}")

        Auth.startOAuth2PKCE(
            this, appId, requestConfig, scopes
        )
    }

    override fun onResume() {
        super.onResume()

        if (isFirstResume) {
            isFirstResume = false
            return
        }

        val credential = Auth.getDbxCredential()

        if (credential?.accessToken == null) {
            setResult(
                RESULT_CANCELED,
                Intent().putExtra(
                    "errorMessage",
                    OmhAuthException.UnrecoverableLoginException().message
                )
            )
        } else {
            dropboxRepository.credential = credential

            setResult(
                RESULT_OK,
                Intent().putExtra("accessToken", credential.accessToken)
            )
        }

        finish()
    }
}
