package com.openmobilehub.android.auth.plugin.dropbox

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2

internal object DropboxClient {
    private var instance: DbxClientV2? = null

    fun getInstance(credential: DbxCredential?, forceNewInstance: Boolean = false): DbxClientV2 {
        if (instance == null || forceNewInstance) {
            instance = DbxClientV2(DbxRequestConfig("db-${credential?.appKey}"), credential)
        }

        return instance!!
    }
}
