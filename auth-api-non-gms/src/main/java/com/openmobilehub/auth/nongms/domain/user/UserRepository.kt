package com.openmobilehub.auth.nongms.domain.user

import com.openmobilehub.auth.api.models.OmhUserProfile

interface UserRepository {

    suspend fun handleIdToken(idToken: String, clientId: String)

    fun getProfileData(): OmhUserProfile?
}
