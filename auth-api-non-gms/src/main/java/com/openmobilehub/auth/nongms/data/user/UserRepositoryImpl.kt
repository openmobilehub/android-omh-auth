package com.openmobilehub.auth.nongms.data.user

import android.content.Context
import android.content.SharedPreferences
import com.openmobilehub.auth.nongms.data.user.datasource.GoogleUserDataSource
import com.openmobilehub.auth.nongms.data.user.datasource.UserDataSource
import com.openmobilehub.auth.nongms.data.utils.getEncryptedSharedPrefs
import com.openmobilehub.auth.nongms.domain.user.UserRepository
import com.openmobilehub.auth.api.models.OmhUserProfile

internal class UserRepositoryImpl(private val googleUserDataSource: UserDataSource) :
    UserRepository {

    override suspend fun handleIdToken(idToken: String, clientId: String) {
        googleUserDataSource.handleIdToken(idToken, clientId)
    }

    override fun getProfileData(): OmhUserProfile? {
        return googleUserDataSource.getProfileData()
    }

    companion object {

        private var userRepository: UserRepository? = null

        fun getUserRepository(context: Context): UserRepository {
            if (userRepository == null) {
                val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context)
                val googleUserDataSource: UserDataSource = GoogleUserDataSource(sharedPreferences)
                userRepository = UserRepositoryImpl(googleUserDataSource)
            }
            return userRepository!!
        }
    }
}
