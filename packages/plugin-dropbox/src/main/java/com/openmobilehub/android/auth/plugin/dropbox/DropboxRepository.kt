package com.openmobilehub.android.auth.plugin.dropbox

import android.content.Context
import android.content.SharedPreferences
import com.dropbox.core.oauth.DbxCredential
import com.openmobilehub.android.auth.core.utils.EncryptedSharedPreferences

private const val PROVIDER_NAME = "dropbox"

class DropboxRepository(private val sharedPreferences: SharedPreferences) {
    var credential: DbxCredential?
        get() {
            val serializedCredentialJson = sharedPreferences.getString("credential", null)

            @Suppress("SwallowedException", "TooGenericExceptionCaught")
            return try {
                DbxCredential.Reader.readFully(serializedCredentialJson)
            } catch (e: Exception) {
                null
            }
        }
        set(credential) {
            if (credential == null) {
                sharedPreferences.edit().remove("credential")
                    .apply()
                return
            }

            sharedPreferences.edit()
                .putString("credential", DbxCredential.Writer.writeToString(credential))
                .apply()

            DropboxClient.getInstance(credential, true)
        }

    companion object {
        private var instance: DropboxRepository? = null

        fun getInstance(context: Context): DropboxRepository {
            if (instance == null) {
                val sharedPreferences =
                    EncryptedSharedPreferences.getEncryptedSharedPrefs(context, PROVIDER_NAME)
                instance = DropboxRepository(sharedPreferences)
            }

            return instance!!
        }
    }
}
