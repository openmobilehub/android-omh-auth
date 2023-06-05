package com.omh.android.auth.nongms.data.user.datasource

import android.content.SharedPreferences
import androidx.core.content.edit
import com.omh.android.auth.nongms.utils.Constants
import com.omh.android.auth.api.models.OmhUserProfile
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import java.util.Collections

internal class GoogleUserDataSource(private val sharedPreferences: SharedPreferences) : UserDataSource {

    /**
     * Handles the ID token returned from the Google Auth Provider. This uses the googleapis lib
     * to verify the token and validate it. The operation can't run on the main thread.
     *
     * Once it passes the checks, the data is stored in the Encrypted Shared Preferences.
     *
     * @param idToken -> token to validate and handle.
     * @param clientId -> clientId from google console of the Android Application type.
     */
    override suspend fun handleIdToken(idToken: String, clientId: String) {
        val verifier = GoogleIdTokenVerifier.Builder(
            NetHttpTransport.Builder().build(),
            GsonFactory.getDefaultInstance()
        )
            // Specify the CLIENT_ID of the app that accesses the backend:
            .setAudience(Collections.singletonList(clientId))
            .build()

        // (Receive idTokenString by HTTPS POST)
        val googleIdToken: GoogleIdToken = verifier.verify(idToken)
        val payload: GoogleIdToken.Payload = googleIdToken.payload

        // Get profile information from payload
        val email: String = payload.email
        val name = payload[Constants.NAME_KEY].toString()
        val surname = payload[Constants.SURNAME_KEY].toString()
        val picture = payload[Constants.PICTURE_KEY].toString()
        val id = payload.subject
        // Use or store profile information
        sharedPreferences.edit {
            putString(Constants.NAME_KEY, name)
            putString(Constants.SURNAME_KEY, surname)
            putString(Constants.EMAIL_KEY, email)
            putString(Constants.PICTURE_KEY, picture)
            putString(Constants.ID_KEY, id)
            putString(Constants.ID_TOKEN, idToken)
        }
    }

    /**
     * Checks if there's any relevant data stored for the user. If any of the required values are
     * null, then it's assumed that no user is stored and a null object is returned.
     */
    override fun getProfileData(): OmhUserProfile? {
        val name = sharedPreferences.getString(Constants.NAME_KEY, null)
        val email = sharedPreferences.getString(Constants.EMAIL_KEY, null)
        val surname = sharedPreferences.getString(Constants.SURNAME_KEY, null)
        val picture = sharedPreferences.getString(Constants.PICTURE_KEY, null)
        val idToken = sharedPreferences.getString(Constants.ID_TOKEN, null)

        if (name == null || email == null || surname == null) return null

        return OmhUserProfile(
            name = name,
            surname = surname,
            email = email,
            profileImage = picture,
            idToken = idToken
        )
    }
}
