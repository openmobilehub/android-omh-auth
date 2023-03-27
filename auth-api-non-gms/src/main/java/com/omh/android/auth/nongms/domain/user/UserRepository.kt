package com.omh.android.auth.nongms.domain.user

import com.omh.android.auth.api.models.OmhUserProfile

interface UserRepository {

    suspend fun handleIdToken(idToken: String, clientId: String)

    fun getProfileData(): OmhUserProfile?
}
