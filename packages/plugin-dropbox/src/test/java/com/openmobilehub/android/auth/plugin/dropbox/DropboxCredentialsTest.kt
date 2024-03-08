package com.openmobilehub.android.auth.plugin.dropbox

import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.oauth.DbxRefreshResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DropboxCredentialsTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun shouldGetAccessToken() {
        val accessToken = "accessToken"
        val dropboxRepository = mockk<DropboxRepository>()

        every { dropboxRepository.credential?.accessToken } returns accessToken

        val credentials = DropboxCredentials(dropboxRepository)

        assertEquals(credentials.accessToken, accessToken)
    }

    @Test
    fun shouldRefreshAccessToken() = runTest {
        val accessToken = "accessToken"
        val expiresAt = 999L
        val refreshToken = "refreshToken"
        val appKey = "appKey"
        val newCredentials = DbxCredential(accessToken, expiresAt, refreshToken, appKey)

        val dropboxRepository = mockk<DropboxRepository>()
        val currentCredentialMock = mockk<DbxCredential>()
        val newAccessTokenMock = mockk<DbxRefreshResult>()

        mockkObject(DropboxClient)

        every { dropboxRepository.credential } returns currentCredentialMock
        every {
            DropboxClient.getInstance(currentCredentialMock).refreshAccessToken()
        } returns newAccessTokenMock
        every { newAccessTokenMock.accessToken } returns accessToken
        every { newAccessTokenMock.expiresAt } returns expiresAt
        every { currentCredentialMock.refreshToken } returns refreshToken
        every { currentCredentialMock.appKey } returns appKey
        every {
            dropboxRepository.credential = newCredentials
        } returns Unit

        val credentials = DropboxCredentials(dropboxRepository)

        credentials.refreshAccessToken().addOnSuccess { newAccessToken ->
            assertEquals(newAccessToken, accessToken)
        }.execute()

        verify {
            DropboxClient.getInstance(currentCredentialMock).refreshAccessToken()
        }
    }
}
