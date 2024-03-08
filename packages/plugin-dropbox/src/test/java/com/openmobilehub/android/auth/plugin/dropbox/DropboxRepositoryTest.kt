package com.openmobilehub.android.auth.plugin.dropbox

import android.content.SharedPreferences
import com.dropbox.core.oauth.DbxCredential
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class DropboxRepositoryTest {
    @Test
    fun shouldGetCredential() {
        val accessToken = "accessToken"
        val credentialJSON = "{ \"access_token\": \"${accessToken}\" }"
        val sharedPreferences = mockk<SharedPreferences>()

        every { sharedPreferences.getString("credential", null) } returns credentialJSON

        val dropboxRepository = DropboxRepository(sharedPreferences)

        val result = dropboxRepository.credential

        assertEquals(result?.accessToken, accessToken)
    }

    @Test
    fun shouldResetCredential() {
        val sharedPreferences = mockk<SharedPreferences>()

        every { sharedPreferences.edit().remove("credential").apply() } returns Unit

        val dropboxRepository = DropboxRepository(sharedPreferences)

        dropboxRepository.credential = null

        verify {
            sharedPreferences.edit().remove("credential").apply()
        }
    }

    @Test
    fun shouldSetCredential() {
        val accessToken = "accessToken"
        val expiresAt = 0L
        val refreshToken = "refreshToken"
        val appKey = "appKey"
        val newCredential = DbxCredential(accessToken, expiresAt, refreshToken, appKey)
        val sharedPreferences = mockk<SharedPreferences>()

        mockkObject(DropboxClient)
        every { DropboxClient.getInstance(newCredential, true) } returns mockk()
        every {
            sharedPreferences.edit()
                .putString("credential", any())
                .apply()
        } returns Unit

        val dropboxRepository = DropboxRepository(sharedPreferences)

        dropboxRepository.credential = newCredential

        verify {
            DropboxClient.getInstance(newCredential, true)
            sharedPreferences.edit()
                .putString("credential", any())
                .apply()
        }
    }
}
