package com.openmobilehub.android.auth.plugin.facebook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.AuthenticationToken
import com.facebook.GraphRequest
import com.facebook.Profile
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import io.mockk.EqMatcher
import io.mockk.MockKAnnotations
import io.mockk.MockKGateway
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class FacebookAuthClientTest {
    private val contextMock = mockk<Context>()
    private val scopes = arrayListOf("email", "public_profile")
    private val authClient = FacebookAuthClient(scopes = scopes, context = contextMock)

    @MockK
    lateinit var mockAccessToken: AccessToken

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkObject(AccessToken.Companion)
        mockkObject(AuthenticationToken.Companion)

        every { AccessToken.getCurrentAccessToken() } returns mockAccessToken
    }

    @After
    fun tearDown() {
        clearAllMocks()

        unmockkObject(AccessToken.Companion)
        unmockkObject(AuthenticationToken.Companion)
    }

    @Test
    fun shouldGetLoginIntent() {
        val intentMock = mockk<Intent>()

        mockkConstructor(Intent::class)
        every {
            constructedWith<Intent>(
                EqMatcher(contextMock),
                EqMatcher(FacebookLoginActivity::class.java)
            )
                .putStringArrayListExtra(
                    "scopes",
                    scopes
                )
        } returns intentMock
        every { intentMock.getStringArrayListExtra("scopes") } returns scopes

        val intent = authClient.getLoginIntent()

        Assert.assertNotNull(intent)
        Assert.assertEquals(intent.getStringArrayListExtra("scopes"), scopes)
    }

    @Test
    fun shouldThrowErrorOnNoLoginIntent() {
        val intentMock = null

        Assert.assertThrows(OmhAuthException.LoginCanceledException::class.java) {
            authClient.handleLoginIntentResponse(intentMock)
        }
    }

    @Test
    fun shouldThrowLoginErrorOnNoAccessToken() {
        val intentMock = mockk<Intent>()

        every { intentMock.hasExtra("accessToken") } returns false

        Assert.assertThrows(OmhAuthException.LoginCanceledException::class.java) {
            authClient.handleLoginIntentResponse(intentMock)
        }
    }

    @Test
    fun shouldReThrowIntentError() {
        val intentMock = mockk<Intent>()
        val intentError = Exception("Test error")

        every { intentMock.hasExtra("accessToken") } returns true
        every { intentMock.hasExtra("error") } returns true
        every { intentMock.getSerializableExtra("error") } returns intentError

        val error = Assert.assertThrows(OmhAuthException.RecoverableLoginException::class.java) {
            authClient.handleLoginIntentResponse(intentMock)
        }

        Assert.assertEquals(error.cause, intentError)
    }

    @Test
    fun shouldGetCredentials() {
        val testAccessToken = "TestAccessToken"
        val credentials = authClient.getCredentials()

        every { mockAccessToken.token } returns testAccessToken

        Assert.assertEquals(credentials.accessToken, testAccessToken)
    }

    @Test
    fun shouldFetchUserSuccessfully() = runTest {
        val mockGraphRequest = mockk<GraphRequest>(relaxed = true)

        val jsonFacebookUser = JSONObject().apply {
            put("first_name", "John")
            put("last_name", "Doe")
            put("email", "john.doe@example.com")
            put("picture", JSONObject().apply {
                put("data", JSONObject().apply {
                    put("url", "https://example.com/picture.jpg")
                })
            })
        }

        val expectedOmhUser = OmhUserProfile(
            name = "John",
            surname = "Doe",
            email = "john.doe@example.com",
            profileImage = "https://example.com/picture.jpg"
        )

        mockkObject(GraphRequest.Companion)
        mockkConstructor(Bundle::class)

        every {
            Bundle().apply {
                putString("fields", "first_name,last_name,email,picture")
            }
        } returns mockk(relaxed = true)

        every { GraphRequest.newMeRequest(mockAccessToken, any()) } answers {
            val callback = arg<GraphRequest.GraphJSONObjectCallback>(1)
            callback.onCompleted(jsonFacebookUser, null)
            mockGraphRequest
        }

        val user = authClient.getUserRequest()

        verify {
            constructedWith<Bundle>().apply {
                putString("fields", "first_name,last_name,email,picture")
            }
            mockGraphRequest.executeAsync()

            Assert.assertEquals(user.email, expectedOmhUser.email)
            Assert.assertEquals(user.name, expectedOmhUser.name)
            Assert.assertEquals(user.surname, expectedOmhUser.surname)
            Assert.assertEquals(user.profileImage, expectedOmhUser.profileImage)
        }
    }
}
