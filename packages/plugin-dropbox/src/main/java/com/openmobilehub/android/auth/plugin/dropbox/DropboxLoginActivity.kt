package com.openmobilehub.android.auth.plugin.dropbox

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.openmobilehub.android.auth.core.models.OmhAuthException

internal class DropboxLoginActivity : Activity() {
    private var isFirstResume = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scopes = intent.getStringArrayListExtra("scopes")

        val requestConfig = DbxRequestConfig("db-fhnsfpb2r74n6iv")

        Auth.startOAuth2PKCE(
            this, "fhnsfpb2r74n6iv", requestConfig, scopes
        )
    }

    override fun onResume() {
        super.onResume()

        if (isFirstResume) {
            isFirstResume = false
            return
        }

        val accessToken = Auth.getOAuth2Token()

        if (accessToken == null) {
            setResult(
                RESULT_CANCELED,
                Intent().putExtra(
                    "errorMessage",
                    OmhAuthException.UnrecoverableLoginException().message
                )
            )
        } else {
            DropboxRepository.getInstance(applicationContext).token = accessToken

            setResult(
                RESULT_OK,
                Intent().putExtra("accessToken", accessToken)
            )
        }

        finish()
    }
}
