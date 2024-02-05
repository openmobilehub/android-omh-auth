package com.openmobilehub.android.auth.plugin.facebook

import android.content.Context
import android.content.Intent
import com.facebook.AccessToken
import com.facebook.AuthenticationToken
import com.openmobilehub.android.auth.core.models.OmhAuthException
import io.mockk.EqMatcher
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.Test


class FacebookAuthClientTest {
    val contextMock = mockk<Context>()
    val scopes = arrayListOf("email", "public_profile")

    @Test
    fun shouldGetLoginIntent() {
        val intentMock = mockk<Intent>()
        val authClient = FacebookAuthClient(scopes = scopes, context = contextMock)

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

        val authClient = FacebookAuthClient(scopes = scopes, context = contextMock)

        Assert.assertThrows(OmhAuthException.LoginCanceledException::class.java) {
            authClient.handleLoginIntentResponse(intentMock)
        }
    }

    @Test
    fun shouldThrowLoginErrorOnNoAccessToken() {
        val intentMock = mockk<Intent>()

        every { intentMock.hasExtra("accessToken") } returns false

        val authClient = FacebookAuthClient(scopes = scopes, context = contextMock)

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

        val authClient = FacebookAuthClient(scopes = scopes, context = contextMock)

        val error = Assert.assertThrows(OmhAuthException.RecoverableLoginException::class.java) {
            authClient.handleLoginIntentResponse(intentMock)
        }

        Assert.assertEquals(error.cause, intentError)
    }

    @Test
    fun shouldGetCredentials() {
        val mockAccessToken = mockk<AccessToken>()
        val mockAuthToken = mockk<AuthenticationToken>()

        mockkStatic(AccessToken::class)
        mockkObject(AccessToken.Companion)
        mockkStatic(AuthenticationToken::class)
        mockkObject(AuthenticationToken.Companion)

        every { AccessToken.getCurrentAccessToken() } returns mockAccessToken
        every { AuthenticationToken.getCurrentAuthenticationToken() } returns mockAuthToken

        val authClient = FacebookAuthClient(scopes = scopes, context = contextMock)

        val credentials = authClient.getCredentials()

        Assert.assertEquals(credentials.accessToken, mockAccessToken)
        Assert.assertEquals(credentials.authenticationToken, mockAuthToken)
    }
}
