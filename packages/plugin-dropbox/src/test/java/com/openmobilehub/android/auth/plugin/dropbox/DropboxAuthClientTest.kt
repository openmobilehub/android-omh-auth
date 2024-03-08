package com.openmobilehub.android.auth.plugin.dropbox

import android.content.Context
import android.content.Intent
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.users.FullAccount
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class DropboxAuthClientTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val scopes = arrayListOf("account_info.read")
    private val appId = "appId"
    private val contextMock = mockk<Context>()
    private val dropboxRepositoryMock = mockk<DropboxRepository>()

    @Before
    fun setUp() {
        mockkObject(DropboxRepository)

        every {
            DropboxRepository.getInstance(contextMock)
        } returns dropboxRepositoryMock
    }

    @After
    fun tearDown() {
        unmockkObject(DropboxRepository)
        clearAllMocks()
    }

    @Test
    fun shouldGetLoginIntent() {
        val intentMock = mockk<Intent>()

        mockkConstructor(Intent::class)

        every {
            anyConstructed<Intent>()
                .putStringArrayListExtra(
                    "scopes",
                    scopes
                )
                .putExtra("appId", appId)
        } returns intentMock

        val intent = DropboxAuthClient(
            scopes = scopes,
            appId = appId,
            context = contextMock
        ).getLoginIntent()

        Assert.assertEquals(intent, intentMock)
    }

    @Test
    fun shouldGetUser() = runTest {
        val name = "name"
        val surname = "surname"
        val email = "email"
        val profilePhotoUrl = "profilePhotoUrl"

        val dropboxRepositoryMock = mockk<DropboxRepository>()
        val credentialMock = mockk<DbxCredential>()
        val currentAccountMock = mockk<FullAccount>()

        mockkObject(DropboxClient)

        every { dropboxRepositoryMock.credential } returns credentialMock
        every {
            DropboxClient.getInstance(credentialMock).users().currentAccount
        } returns currentAccountMock
        every { currentAccountMock.name.givenName } returns name
        every { currentAccountMock.name.surname } returns surname
        every { currentAccountMock.email } returns email
        every { currentAccountMock.profilePhotoUrl } returns profilePhotoUrl

        val authClient = DropboxAuthClient(scopes = scopes, appId = appId, context = contextMock)

        authClient.getUser().addOnSuccess { user ->
            Assert.assertEquals(user.name, name)
            Assert.assertEquals(user.surname, surname)
            Assert.assertEquals(user.email, email)
            Assert.assertEquals(user.profileImage, profilePhotoUrl)
        }.execute()
    }

    @Test
    fun shouldRevokeToken() = runTest {
        mockkObject(DropboxClient)

        every { dropboxRepositoryMock.credential } returns mockk()
        every {
            DropboxClient.getInstance(dropboxRepositoryMock.credential).auth().tokenRevoke()
        } returns Unit

        val authClient = DropboxAuthClient(scopes = scopes, appId = appId, context = contextMock)

        authClient.revokeToken().execute()

        verify {
            DropboxClient.getInstance(dropboxRepositoryMock.credential).auth().tokenRevoke()
        }
    }

    @Test
    fun shouldSignOut() = runTest {
        val authClient = DropboxAuthClient(scopes = scopes, appId = appId, context = contextMock)

        authClient.signOut().execute()

        verify {
            dropboxRepositoryMock.credential = null
        }
    }
}
