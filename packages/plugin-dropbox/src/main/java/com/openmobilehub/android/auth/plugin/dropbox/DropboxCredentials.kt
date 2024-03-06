package com.openmobilehub.android.auth.plugin.dropbox

import com.dropbox.core.oauth.DbxCredential
import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.OmhTask

class DropboxCredentials(
    private val dropboxRepository: DropboxRepository
) : OmhCredentials {
    override val accessToken: String?
        get() = dropboxRepository.credential?.accessToken

    override fun refreshAccessToken(): OmhTask<String?> {
        return OmhTask({
            val currentCredential = dropboxRepository.credential

            val newAccessTokenResult =
                DropboxClient.getInstance(currentCredential).refreshAccessToken()

            val newCredential = DbxCredential(
                newAccessTokenResult.accessToken,
                newAccessTokenResult.expiresAt,
                currentCredential?.refreshToken,
                currentCredential?.appKey
            )

            dropboxRepository.credential = newCredential

            return@OmhTask dropboxRepository.credential?.accessToken
        })
    }
}
