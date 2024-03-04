package com.openmobilehub.android.auth.plugin.dropbox

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.openmobilehub.android.auth.core.models.OmhAuthException

internal class DropboxLoginActivity : Activity() {
    var isAwaitingResult: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("Create")

        val scopes = intent.getStringArrayListExtra("scopes")

        val requestConfig = DbxRequestConfig("db-fhnsfpb2r74n6iv")

        Auth.startOAuth2PKCE(
            this, "fhnsfpb2r74n6iv", requestConfig, scopes
        )

        isAwaitingResult = true
    }

    override fun onResume() {
        super.onResume()

        if (!isAwaitingResult) {
            return
        }

//        val credentials = Auth.getDbxCredential()

        val accessToken = Auth.getOAuth2Token()

        if (accessToken == null) {
            setResult(
                RESULT_CANCELED,
                Intent().putExtra(
                    "errorMessage",
                    OmhAuthException.UnrecoverableLoginException().message
                )
            )
            
            finish()
        }

        DropboxRepository.getInstance(applicationContext).token = accessToken

        setResult(
            RESULT_OK,
            Intent().putExtra("accessToken", accessToken)
        )

        finish()
    }
}
