package com.openmobilehub.android.auth.plugin.facebook

import FacebookCredentials
import ThreadUtils
import com.facebook.AccessToken
import com.facebook.FacebookException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class FacebookCredentialsTest {
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

        mockkObject(ThreadUtils)
        every { ThreadUtils.checkForMainThread() } returns Unit

        mockkObject(AccessToken.Companion)
        every { AccessToken.refreshCurrentAccessTokenAsync(any()) } answers {
            val callback = args[0] as AccessToken.AccessTokenRefreshCallback
            callback.OnTokenRefreshed(accessTokenMock)
        }

        val token = FacebookCredentials().blockingRefreshToken()

        verify {
            AccessToken.refreshCurrentAccessTokenAsync(any())
            assertEquals(token, testAccessToken)
        }
    }

    @Test
    fun shouldRefreshAccessTokenFailure() = runTest {
        val mockException = mockk<FacebookException>()

        mockkObject(ThreadUtils)
        every { ThreadUtils.checkForMainThread() } returns Unit

        mockkObject(AccessToken.Companion)
        every { AccessToken.refreshCurrentAccessTokenAsync(any()) } answers {
            val callback = args[0] as AccessToken.AccessTokenRefreshCallback
            callback.OnTokenRefreshFailed(mockException)
        }

        try {
            FacebookCredentials().blockingRefreshToken()
        } catch (e: Exception) {
            assertEquals(e, mockException)
        }
    }
}
