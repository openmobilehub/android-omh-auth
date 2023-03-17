package com.openmobilehub.auth.nongms.domain.user

import com.openmobilehub.auth.api.models.OmhUserProfile

class ProfileUseCase(private val userRepository: UserRepository) {

    suspend fun resolveIdToken(idToken: String, clientId: String) {
        if (idToken.trim().isEmpty() || clientId.trim().isEmpty()) return // todo add error handling
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
