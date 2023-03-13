package com.github.authnongms.data.user

import android.content.Context
import android.content.SharedPreferences
import com.github.authnongms.data.user.datasource.GoogleUserDataSource
import com.github.authnongms.data.user.datasource.UserDataSource
import com.github.authnongms.data.utils.getEncryptedSharedPrefs
import com.github.authnongms.domain.user.UserRepository
import com.github.openmobilehub.auth.models.OmhUserProfile

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
