package com.openmobilehub.auth.nongms.data.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.openmobilehub.auth.nongms.utils.Constants

internal fun getEncryptedSharedPrefs(
    context: Context,
    name: String = Constants.PROVIDER_GOOGLE // Default to Google as it's the only one we're using for now
): SharedPreferences {
    val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    return EncryptedSharedPreferences.create(
        Constants.SHARED_PREFS_TOKEN_FORMAT.format(name),
        masterKey,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
