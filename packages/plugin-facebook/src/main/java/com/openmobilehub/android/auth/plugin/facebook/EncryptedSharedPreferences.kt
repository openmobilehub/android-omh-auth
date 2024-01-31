package com.openmobilehub.android.auth.plugin.facebook

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys


class EncryptedSharedPreferences(val context: Context) {
    private var sharedPreferences: SharedPreferences

    init {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        sharedPreferences = EncryptedSharedPreferences.create(
            "token_storage_facebook",
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private object Keys {
        const val ID = "id"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val EMAIL = "email"
        const val PICTURE_URL = "picture_url"
    }

    fun getUserData(): UserData? {
        val userData = UserData(
            id = sharedPreferences.getString(Keys.ID, null),
            firstName = sharedPreferences.getString(Keys.FIRST_NAME, null),
            lastName = sharedPreferences.getString(Keys.LAST_NAME, null),
            email = sharedPreferences.getString(Keys.EMAIL, null),
            pictureUrl = sharedPreferences.getString(Keys.PICTURE_URL, null)
        )

        if (userData.firstName == null || userData.lastName == null || userData.email == null) {
            return null
        }

        return userData
    }

    fun saveUserData(userData: UserData) {
        sharedPreferences.edit {
            putString(Keys.ID, userData.id)
            putString(Keys.FIRST_NAME, userData.firstName)
            putString(Keys.LAST_NAME, userData.lastName)
            putString(Keys.EMAIL, userData.email)
            putString(Keys.PICTURE_URL, userData.pictureUrl)
        }
    }
}

class UserData(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val pictureUrl: String?
)
