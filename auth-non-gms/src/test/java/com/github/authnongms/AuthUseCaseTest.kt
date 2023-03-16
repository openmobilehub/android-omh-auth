package com.github.authnongms

import com.github.authnongms.domain.auth.AuthRepository
import com.github.authnongms.domain.auth.AuthUseCase
import com.github.authnongms.domain.models.OAuthTokens
import com.github.authnongms.domain.utils.Pkce
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class AuthUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private val pkce: Pkce = mockk() {
        every { codeVerifier } returns "codeverifier"
        every { generateCodeChallenge() } returns "codechallenge"
    }
    private val authUseCase = AuthUseCase(authRepository, pkce)

    // TODO when there is more use case logic, create useful tests.

    @Before
    fun setupClientId() {
        authUseCase.clientId = "clientid"
    }

    @After
    fun resetClientId() {
        authUseCase.clientId = null
    }

    @Test
    fun `when given scope and packageName a correct Uri is returned`() {
        val scope = "scope"
        val packageName = "com.package.name"
        val expectedRedirect: String = AuthUseCase.REDIRECT_FORMAT.format(packageName)

        val expectedResult = "www.link.com/path?scopes=$scope&redirect=$expectedRedirect"
        every {
            authRepository.buildLoginUrl(
                scopes = any(),
                clientId = any(),
                codeChallenge = any(),
                redirectUri = any()
            )
        } returns expectedResult

        val result: String = authUseCase.getLoginUrl(scope, packageName)

        assertTrue(result.contains(scope))
        assertTrue(result.contains(expectedRedirect))
    }

    @Test(expected = IllegalStateException::class)
    fun `when no clientId is setup then an error is thrown for get login url`() {
        authUseCase.clientId = null

        val scope = "scope"
        val packageName = "com.package.name"
        val expectedRedirect: String = AuthUseCase.REDIRECT_FORMAT.format(packageName)
        val expectedResult = "www.link.com/path?scopes=$scope&redirect=$expectedRedirect"

        every {
            authRepository.buildLoginUrl(
                scopes = any(),
                clientId = any(),
                codeChallenge = any(),
                redirectUri = any()
            )
        } returns expectedResult

        authUseCase.getLoginUrl(scope, packageName)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when given auth code and package name an AuthTokenResponse is returned`() = runTest {
        val authCode = "auth code"
        val packageName = "com.package.name"
        val mockedResponse: OAuthTokens = mockk()

        coEvery {
            authRepository.requestTokens(
                clientId = any(),
                authCode = any(),
                redirectUri = any(),
                codeVerifier = any(),
            )
        } returns flow { emit(mockedResponse) }

        val result = authUseCase.requestTokens(authCode, packageName).first()

        assertEquals(mockedResponse, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = IllegalStateException::class)
    fun `when no clientId is setup then an error is thrown for request tokens`() = runTest {
        authUseCase.clientId = null
        val authCode = "auth code"
        val packageName = "com.package.name"
        val mockedResponse: OAuthTokens = mockk()

        coEvery {
            authRepository.requestTokens(
                clientId = any(),
                authCode = any(),
                redirectUri = any(),
                codeVerifier = any(),
            )
        } returns flow { emit(mockedResponse) }

        authUseCase.requestTokens(authCode, packageName).first()
    }

    @Test
    fun `when the access token is requested then the token is returned`() {
        val expectedToken = "accesstoken"
        every { authRepository.getAccessToken() } returns expectedToken

        val result: String? = authUseCase.getAccessToken()

        assertEquals(result, expectedToken)
    }

    @Test
    fun `when the access token is requested but no token is stored then null is returned`() {
        val expectedToken = null
        every { authRepository.getAccessToken() } returns expectedToken

        val result: String? = authUseCase.getAccessToken()

        assertEquals(result, expectedToken)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when a token refresh is requested then a new token is returned`() = runTest {
        val expectedToken = "newtoken"

        coEvery { authRepository.refreshAccessToken(any()) } returns flow { emit(expectedToken) }

        val newToken = authUseCase.refreshToken().first()

        assertEquals(expectedToken, newToken)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = IllegalStateException::class)
    fun `when a token refresh is requested with no clientId then an exception is thrown`() {
        runTest {
            val expectedToken = "newtoken"
            authUseCase.clientId = null

            coEvery { authRepository.refreshAccessToken(any()) } returns flow { emit(expectedToken) }

            authUseCase.refreshToken().first()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when logout is requested then a revoke function is called and storage is cleaned up`() {
        runTest {
            coEvery { authRepository.revokeToken() } returns flow { emit(Unit) }
            coEvery { authRepository.clearData() } returns Unit

            authUseCase.logout().collect()

            coVerify { authRepository.revokeToken() }
            coVerify { authRepository.clearData() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = Exception::class)
    fun `when logout is requested and the revoke function fails then storage is cleaned up`() {
        runTest {
            coEvery { authRepository.revokeToken() } throws Exception("Revoke failed")
            coEvery { authRepository.clearData() } returns Unit

            authUseCase.logout().collect()

            coVerify { authRepository.revokeToken() }
            coVerify { authRepository.clearData() }
        }
    }
}
