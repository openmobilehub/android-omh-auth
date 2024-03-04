package com.openmobilehub.android.auth.plugin.dropbox

import android.content.Context
import android.content.Intent
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhUserProfile

class DropboxAuthClient(
    val scopes: ArrayList<String>,
    val context: Context
) : OmhAuthClient {
    private val dropboxRepository = DropboxRepository.getInstance(context)

    override fun initialize(): OmhTask<Unit> {
        return OmhTask(
            {
                // No initialization needed for Dropbox Sign-In
            },
        )
    }

    override fun getLoginIntent(): Intent {
        return Intent(
            context, DropboxLoginActivity::class.java
        )
            .putStringArrayListExtra("scopes", scopes)
    }

    override fun getUser(): OmhTask<OmhUserProfile> {
        TODO()
    }

    override fun getCredentials(): OmhCredentials {
        return DropboxCredentials(dropboxRepository, scopes)
    }

    override fun revokeToken(): OmhTask<Unit> {
        TODO()
    }

    override fun signOut(): OmhTask<Unit> {
        TODO()
    }
}
