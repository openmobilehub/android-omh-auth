package com.openmobilehub.android.auth.plugin.facebook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.AuthenticationToken
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.Profile
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import io.mockk.EqMatcher
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.slot
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

        val intent = authClient.getLoginIntent()

        Assert.assertNotNull(intent)
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

    @Test
    fun shouldRevokeTokenSuccessfully() = runTest {
        val testProfileId = "12345"
        val graphResponseMock = mockk<GraphResponse>()
        val callbackSlot = slot<GraphRequest.Callback>()

        mockkConstructor(GraphRequest::class)
        mockkObject(Profile.Companion)
        every { Profile.getCurrentProfile()?.id } returns testProfileId

        every { graphResponseMock.error } returns null

        every {
            anyConstructed<GraphRequest>().apply {
                accessToken = mockAccessToken
                graphPath = "/%s/permissions".format(testProfileId)
                httpMethod = com.facebook.HttpMethod.DELETE
                callback = capture(callbackSlot)
            }.executeAsync()
        } answers {
            callbackSlot.captured.onCompleted(graphResponseMock)
            mockk()
        }

        authClient.revokeTokenRequest()

        verify {
            anyConstructed<GraphRequest>().apply {
                accessToken = mockAccessToken
                graphPath = "/%s/permissions".format(testProfileId)
                httpMethod = com.facebook.HttpMethod.DELETE
                callback = any()
            }.executeAsync()
        }
    }


    @Test
    fun shouldRevokeTokenFailure() = runTest {
        val testProfileId = "12345"
        val graphResponseMock = mockk<GraphResponse>()
        val facebookException = mockk<FacebookException>()
        val callbackSlot = slot<GraphRequest.Callback>()

        mockkConstructor(GraphRequest::class)
        mockkObject(Profile.Companion)
        every { Profile.getCurrentProfile()?.id } returns testProfileId

        every { graphResponseMock.error?.exception } returns facebookException

        every {
            anyConstructed<GraphRequest>().apply {
                accessToken = mockAccessToken
                graphPath = "/%s/permissions".format(testProfileId)
                httpMethod = com.facebook.HttpMethod.DELETE
                callback = capture(callbackSlot)
            }.executeAsync()
        } answers {
            callbackSlot.captured.onCompleted(graphResponseMock)
            mockk()
        }

        try {
            authClient.revokeTokenRequest()
        } catch (e: Exception) {
            Assert.assertEquals(e, facebookException)
        }

        verify {
            anyConstructed<GraphRequest>().apply {
                accessToken = mockAccessToken
                graphPath = "/%s/permissions".format(testProfileId)
                httpMethod = com.facebook.HttpMethod.DELETE
                callback = any()
            }.executeAsync()
        }
    }
}
