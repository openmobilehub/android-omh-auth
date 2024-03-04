package com.openmobilehub.android.auth.plugin.dropbox

import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.OmhTask

class DropboxCredentials(
    private val dropboxRepository: DropboxRepository,
    private val scopes: ArrayList<String>
) : OmhCredentials {
    override val accessToken: String?
        get() = dropboxRepository.token

    override fun refreshAccessToken(): OmhTask<String?> {
        TODO()
    }
}
