package com.openmobilehub.android.auth.plugin.microsoft

import android.content.Context
import android.content.Intent
import com.openmobilehub.android.auth.core.async.OmhSuccessListener
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
class MicrosoftAuthClientTest {
    private val scopes = arrayListOf("User.Read")
    private val configFileResourceId = 0

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var appMock: MicrosoftApplication

    @MockK
    private lateinit var repoMock: MicrosoftRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkObject(MicrosoftApplication.Companion)
        mockkObject(MicrosoftRepository.Companion)

        every { MicrosoftApplication.getInstance() } returns appMock
        every { MicrosoftRepository.getInstance(any()) } returns repoMock
    }

    @After
    fun tearDown() {
        unmockkObject(MicrosoftApplication.Companion)
        unmockkObject(MicrosoftRepository.Companion)
    }

    @Test
    fun shouldInitialize() = runTest {
        val callbackMock = mockk<OmhSuccessListener<Unit>>()

        every {
            appMock.getApplication()
        } throws OmhAuthException.NotInitializedException()
        coEvery { appMock.initialize(any(), any()) } returns mockk()

        val client = MicrosoftAuthClient(
            configFileResourceId = configFileResourceId,
            scopes = scopes,
            context = mockk()
        )

        client.initialize().addOnSuccess(callbackMock).execute()

        coVerify { appMock.initialize(any(), any()) }
        coVerify { callbackMock.onSuccess(Unit) }
    }

    @Test
    fun shouldGetLoginIntent() {
        val contextMock = mockk<Context>()
        val intentMock = mockk<Intent>()

        mockkConstructor(Intent::class)
        every {
            anyConstructed<Intent>()
                .putExtra("configFileResourceId", configFileResourceId)
                .putStringArrayListExtra(
                    "scopes",
                    scopes
                )
        } returns intentMock

        val client = MicrosoftAuthClient(
            configFileResourceId = configFileResourceId,
            scopes = scopes,
            context = contextMock
        )

        val intent = client.getLoginIntent()

        assertNotNull(intent)

        verify {
            anyConstructed<Intent>()
                .putExtra("configFileResourceId", configFileResourceId)
                .putStringArrayListExtra(
                    "scopes",
                    scopes
                )
        }
    }

    @Test
    fun shouldGetUserSuccessfully() = runTest {
        val user = User("John", "Doe", "johndoe@example.com")
        val omhUser = OmhUserProfile(user.givenName, user.surname, user.mail, null)
        val callMock = mockk<Call<User>>()

        mockkObject(MicrosoftApiService)
        every { MicrosoftApiService.service.getUserProfile(any()) } returns callMock
        every {
            callMock.enqueue(any())
        } answers {
            (args[0] as Callback<User>).onResponse(callMock, Response.success(user))
        }
        every { repoMock.token } returns "token"


        val client = MicrosoftAuthClient(
            configFileResourceId = configFileResourceId,
            scopes = scopes,
            context = mockk()
        )

        val result = client.getUserRequest()

        assertEquals(result.name, omhUser.name)
        assertEquals(result.surname, omhUser.surname)
        assertEquals(result.email, omhUser.email)
        assertEquals(result.profileImage, omhUser.profileImage)
    }

    @Test
    fun shouldSignOut() = runTest {
        every { appMock.getApplication().signOut() } returns true
        every { repoMock.token = null } returns Unit

        val client = MicrosoftAuthClient(
            configFileResourceId = configFileResourceId,
            scopes = scopes,
            context = mockk()
        )

        client.signOutRequest()

        verify {
            appMock.getApplication().signOut()
            repoMock.token = null
        }
    }
}
