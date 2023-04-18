package com.omh.android.auth.nongms.domain.user

import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhUserProfile

internal class ProfileUseCase(private val userRepository: UserRepository) {

    @Throws(OmhAuthException.UnrecoverableLoginException::class)
    suspend fun resolveIdToken(idToken: String, clientId: String) {
        if (idToken.trim().isEmpty() || clientId.trim().isEmpty()) {
            throw OmhAuthException.UnrecoverableLoginException(
                cause = IllegalStateException("idToken or clientId is empty")
            )
        }
        userRepository.handleIdToken(idToken, clientId)
    }

    fun getProfileData(): OmhUserProfile? {
        return userRepository.getProfileData()
    }

    companion object {

        fun createUserProfileUseCase(userRepository: UserRepository): ProfileUseCase {
            return ProfileUseCase(userRepository)
        }
    }
}
