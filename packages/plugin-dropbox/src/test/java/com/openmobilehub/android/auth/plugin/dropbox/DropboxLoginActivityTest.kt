package com.openmobilehub.android.auth.plugin.dropbox

import android.app.Activity
import android.content.Intent
import com.dropbox.core.android.Auth
import com.dropbox.core.oauth.DbxCredential
import com.openmobilehub.android.auth.core.models.OmhAuthException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

@RunWith(RobolectricTestRunner::class)
class DropboxLoginActivityTest {
    private val scopes = arrayListOf("account_info.read")
    private val appId = "appId"

    @Test
    fun shouldLoginSuccessfully() {
        val accessToken = "accessToken"
        val intent = Intent().putStringArrayListExtra("scopes", scopes).putExtra("appId", appId)
        val credentialMock = mockk<DbxCredential>()
        val dropboxRepositoryMock = mockk<DropboxRepository>()

        mockkObject(Auth)
        mockkObject(DropboxRepository)

        every { DropboxRepository.getInstance(any()) } returns dropboxRepositoryMock
        every { Auth.startOAuth2PKCE(any(), appId, any(), scopes) } returns Unit
        every { Auth.getDbxCredential() } returns credentialMock
        every { credentialMock.accessToken } returns accessToken
        every { dropboxRepositoryMock.credential = credentialMock } returns Unit

        val activity =
            Robolectric.buildActivity(DropboxLoginActivity::class.java, intent)
                .create()
                .start()
                .resume()
                .resume()
                .get()

        val shadow = Shadows.shadowOf(activity)

        Assert.assertEquals(shadow.resultCode, Activity.RESULT_OK)
        Assert.assertEquals(shadow.resultIntent.getStringExtra("accessToken"), accessToken)

        verify { Auth.startOAuth2PKCE(any(), appId, any(), scopes) }
    }

    @Test
    fun shouldLoginFail() {
        val intent = Intent().putStringArrayListExtra("scopes", scopes).putExtra("appId", appId)
        val dropboxRepositoryMock = mockk<DropboxRepository>()

        mockkObject(Auth)
        mockkObject(DropboxRepository)

        every { DropboxRepository.getInstance(any()) } returns dropboxRepositoryMock
        every { Auth.startOAuth2PKCE(any(), appId, any(), scopes) } returns Unit
        every { Auth.getDbxCredential() } returns null

        val activity =
            Robolectric.buildActivity(DropboxLoginActivity::class.java, intent)
                .create()
                .start()
                .resume()
                .resume()
                .get()

        val shadow = Shadows.shadowOf(activity)

        Assert.assertEquals(shadow.resultCode, Activity.RESULT_CANCELED)
        Assert.assertEquals(
            shadow.resultIntent.getStringExtra("errorMessage"),
            OmhAuthException.UnrecoverableLoginException().message
        )

        verify { Auth.startOAuth2PKCE(any(), appId, any(), scopes) }
    }
}
