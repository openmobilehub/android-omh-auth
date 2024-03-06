package com.openmobilehub.android.auth.plugin.dropbox

import android.content.Context
import android.content.Intent
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhUserProfile

class DropboxAuthClient(
    val scopes: ArrayList<String>,
    val context: Context,
    val appId: String
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
            .putExtra("appId", appId)
    }

    override fun getUser(): OmhTask<OmhUserProfile> {
        return OmhTask(
            {
                val currentAccount =
                    DropboxClient.getInstance(dropboxRepository.credential).users().currentAccount

                OmhUserProfile(
                    currentAccount.name.givenName,
                    currentAccount.name.surname,
                    currentAccount.email,
                    currentAccount.profilePhotoUrl,
                )
            },
        )
    }

    override fun getCredentials(): OmhCredentials {
        return DropboxCredentials(dropboxRepository)
    }

    override fun revokeToken(): OmhTask<Unit> {
        return OmhTask(
            {
                DropboxClient.getInstance(dropboxRepository.credential).auth().tokenRevoke()
            },
        )
    }

    override fun signOut(): OmhTask<Unit> {
        return OmhTask(
            {
                dropboxRepository.credential = null
            },
        )
    }
}
