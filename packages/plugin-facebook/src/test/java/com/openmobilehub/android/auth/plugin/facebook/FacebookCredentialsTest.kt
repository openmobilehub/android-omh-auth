package com.openmobilehub.android.auth.plugin.facebook

import com.facebook.AccessToken
import com.facebook.FacebookException
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
class FacebookCredentialsTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun shouldGetAccessToken() {
        val testAccessToken = "TestAccessToken"

        mockkObject(AccessToken.Companion)
        every { AccessToken.getCurrentAccessToken()?.token } returns testAccessToken

        assertEquals(FacebookCredentials().accessToken, testAccessToken)
    }

    @Test
    fun shouldRefreshAccessTokenSuccessfully() = runTest {
        val testAccessToken = "TestAccessToken"
        val accessTokenMock = mockk<AccessToken>()

        every { accessTokenMock.token } returns testAccessToken

        mockkObject(AccessToken.Companion)
        every { AccessToken.refreshCurrentAccessTokenAsync(any()) } answers {
            val callback = args[0] as AccessToken.AccessTokenRefreshCallback
            callback.OnTokenRefreshed(accessTokenMock)
        }

        var token: String? = ""
        FacebookCredentials().refreshToken().addOnSuccess { newToken ->
            token = newToken
        }.execute()

        verify {
            AccessToken.refreshCurrentAccessTokenAsync(any())
            assertEquals(token, testAccessToken)
        }
    }

    @Test
    fun shouldRefreshAccessTokenFailure() = runTest {
        val mockException = mockk<FacebookException>()

        mockkObject(AccessToken.Companion)
        every { AccessToken.refreshCurrentAccessTokenAsync(any()) } answers {
            val callback = args[0] as AccessToken.AccessTokenRefreshCallback
            callback.OnTokenRefreshFailed(mockException)
        }

        FacebookCredentials().refreshToken().addOnFailure { e ->
            assertEquals(e, mockException)
        }.execute()
    }
}
