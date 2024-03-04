package com.openmobilehub.android.auth.plugin.dropbox

import android.content.Context
import android.content.SharedPreferences
import com.openmobilehub.android.auth.core.utils.EncryptedSharedPreferences

private const val PROVIDER_NAME = "dropbox"

class DropboxRepository(private val sharedPreferences: SharedPreferences) {
    var token: String?
        get() {
            return sharedPreferences.getString("token", null)
        }
        set(newToken) {
            sharedPreferences.edit().putString("token", newToken).apply()
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