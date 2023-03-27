package com.omh.android.auth.nongms.data.user

import android.content.Context
import android.content.SharedPreferences
import com.omh.android.auth.nongms.data.user.datasource.GoogleUserDataSource
import com.omh.android.auth.nongms.data.user.datasource.UserDataSource
import com.omh.android.auth.nongms.data.utils.getEncryptedSharedPrefs
import com.omh.android.auth.nongms.domain.user.UserRepository
import com.omh.android.auth.api.models.OmhUserProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class UserRepositoryImpl(
    private val googleUserDataSource: UserDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {

    override suspend fun handleIdToken(idToken: String, clientId: String) {
        withContext(ioDispatcher) {
            googleUserDataSource.handleIdToken(idToken, clientId)
        }
    }

    override fun getProfileData(): OmhUserProfile? {
        return googleUserDataSource.getProfileData()
    }

    companion object {

        private var userRepository: UserRepository? = null

        fun getUserRepository(
            context: Context,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): UserRepository {
            if (userRepository == null) {
                val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context)
                val googleUserDataSource: UserDataSource = GoogleUserDataSource(sharedPreferences)
                userRepository = UserRepositoryImpl(googleUserDataSource, ioDispatcher)
            }
            return userRepository!!
        }
    }
}
