package com.omh.android.auth.nongms.data.user.datasource

import com.omh.android.auth.api.models.OmhUserProfile

interface UserDataSource {

    /**
     * Handles the ID token returned from the Auth Provider. The operation can't run on the main thread.
     *
     * Once it passes the checks, the data is stored in the Encrypted Shared Preferences.
     *
     * @param idToken -> token to validate and handle.
     * @param clientId -> clientId from console.
     */
    suspend fun handleIdToken(idToken: String, clientId: String)

    /**
     * Checks if there's any relevant data stored for the user. If any of the required values are
     * null, then it's assumed that no user is stored and a null object is returned.
     */
    fun getProfileData(): OmhUserProfile?
}
