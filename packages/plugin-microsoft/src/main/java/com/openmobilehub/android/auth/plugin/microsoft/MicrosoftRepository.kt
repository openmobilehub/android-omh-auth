package com.openmobilehub.android.auth.plugin.microsoft

import android.content.Context
import android.content.SharedPreferences
import com.openmobilehub.android.auth.core.utils.EncryptedSharedPreferences

private const val PROVIDER_NAME = "microsoft"

internal class MicrosoftRepository(private val sharedPreferences: SharedPreferences) {
    var token: String?
        get() {
            return sharedPreferences.getString("token", null)
        }
        set(newToken) {
            sharedPreferences.edit().putString("token", newToken).apply()
        }

    companion object {
        private var instance: MicrosoftRepository? = null

        fun getInstance(context: Context): MicrosoftRepository {
            if (instance == null) {
                val sharedPreferences =
                    EncryptedSharedPreferences.getEncryptedSharedPrefs(context, PROVIDER_NAME)
                instance = MicrosoftRepository(sharedPreferences)
            }

            return instance!!
        }
    }
}
