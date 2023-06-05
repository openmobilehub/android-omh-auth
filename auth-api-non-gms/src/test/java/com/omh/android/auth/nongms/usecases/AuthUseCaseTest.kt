package com.omh.android.auth.nongms.usecases

import com.omh.android.auth.nongms.domain.auth.AuthRepository
import com.omh.android.auth.nongms.domain.auth.AuthUseCase
import com.omh.android.auth.nongms.domain.models.ApiResult
import com.omh.android.auth.nongms.domain.models.OAuthTokens
import com.omh.android.auth.nongms.domain.utils.Pkce
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class AuthUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private val pkce: Pkce = mockk() {
        every { codeVerifier } returns "codeverifier"
        every { generateCodeChallenge() } returns "codechallenge"
    }
    private val authUseCase = AuthUseCase(authRepository, pkce)

    @Test
    fun `given a scope and package name when requesting login URl a correct string is returned`() {
        val scope = "scope"
        val packageName = "com.package.name"
        val expectedRedirect: String = AuthUseCase.REDIRECT_FORMAT.format(packageName)
        val clientId = "client ID"

        val expectedResult = "www.link.com/path?scopes=$scope&redirect=$expectedRedirect"
        every {
            authRepository.buildLoginUrl(
                scopes = any(),
                clientId = any(),
                codeChallenge = any(),
                redirectUri = any()
            )
        } returns expectedResult

        val result: String = authUseCase.getLoginUrl(scope, packageName, clientId)

        assertTrue(result.contains(scope))
        assertTrue(result.contains(expectedRedirect))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given an auth code and package name when tokens are requested then they are returned`() {
        runTest {
            val authCode = "auth code"
            val packageName = "com.package.name"
            val mockedResponse: OAuthTokens = mockk()
            val expectedResult = ApiResult.Success(mockedResponse)
        val clientId = "client ID"

            coEvery {
                authRepository.requestTokens(
                    clientId = any(),
                    authCode = any(),
                    redirectUri = any(),
                    codeVerifier = any(),
                )
            } returns expectedResult

            val result = authUseCase.requestTokens(authCode, packageName, clientId)

            assertEquals(expectedResult, result)
        }
    }

    @Test
    fun `given that an access token was stored when it's requested then it's returned`() {
        val expectedToken = "accesstoken"
        every { authRepository.getAccessToken() } returns expectedToken

        val result: String? = authUseCase.getAccessToken()

        assertEquals(result, expectedToken)
    }

    @Test
    fun `given that an access token wasn't stored when it's requested then null returned`() {
        val expectedToken = null
        every { authRepository.getAccessToken() } returns expectedToken

        val result: String? = authUseCase.getAccessToken()

        assertEquals(result, expectedToken)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when a refresh is requested then a new token is returned`() = runTest {
        val expectedToken = "newtoken"
        val expectedResult = ApiResult.Success(expectedToken)
        val clientId = "client ID"

        coEvery { authRepository.refreshAccessToken(any()) } returns expectedResult

        val newToken = authUseCase.blockingRefreshToken(clientId)

        assertEquals(expectedResult, newToken)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when logout is requested then storage is cleaned up`() = runTest {
        coEvery { authRepository.clearData() } returns Unit

        authUseCase.logout()

        coVerify { authRepository.clearData() }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given revoke was requested when revoke succeeds then storage is cleaned up`() = runTest {
        coEvery { authRepository.revokeToken() } returns ApiResult.Success(Unit)
        coEvery { authRepository.clearData() } returns Unit

        authUseCase.revokeToken()

        coVerify { authRepository.revokeToken() }
        coVerify { authRepository.clearData() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given revoke was requested when revoke fails then storage is not cleaned up`() = runTest {
        val error: ApiResult.Error = mockk()
        coEvery { authRepository.revokeToken() } returns error
        coEvery { authRepository.clearData() } returns Unit

        authUseCase.revokeToken()

        coVerify { authRepository.revokeToken() }
        coVerify(inverse = true) { authRepository.clearData() }
    }
}
