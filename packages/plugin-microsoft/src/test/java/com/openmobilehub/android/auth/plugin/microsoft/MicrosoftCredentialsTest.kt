package com.openmobilehub.android.auth.plugin.microsoft

import com.microsoft.identity.client.AcquireTokenSilentParameters
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.SilentAuthenticationCallback
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MicrosoftCredentialsTest {
    private val scopes = arrayListOf("User.Read")

    @Test
    fun shouldGetAccessToken() = runTest {
        val accessToken = "accessToken"
        val repoMock = mockk<MicrosoftRepository>()
        val appMock = mockk<MicrosoftApplication>()
        val currentAccountMock = mockk<IAccount>()
        val paramsMock = mockk<AcquireTokenSilentParameters>()
        val authCallbackSlot = slot<SilentAuthenticationCallback>()
        val resultMock = mockk<IAuthenticationResult>()
        val credentials = MicrosoftCredentials(
            microsoftRepository = repoMock,
            microsoftApplication = appMock,
            scopes = scopes
        )
        every { resultMock.accessToken } returns accessToken
        every { appMock.getApplication().currentAccount.currentAccount } returns currentAccountMock
        every { currentAccountMock.authority } returns "authorityMock"
        mockkStatic(AcquireTokenSilentParameters.Builder::class)
        mockkConstructor(AcquireTokenSilentParameters.Builder::class)
        every {
            anyConstructed<AcquireTokenSilentParameters.Builder>()
                .forAccount(any())
                .fromAuthority(any())
                .forceRefresh(true)
                .withScopes(scopes)
                .withCallback(capture(authCallbackSlot))
                .build()
        } answers {
            authCallbackSlot.captured.onSuccess(resultMock)
            paramsMock
        }
        every { appMock.getApplication().acquireTokenSilentAsync(paramsMock) } returns Unit
        every { repoMock.token = accessToken } returns Unit

        val token = credentials.requestAccessToken()


        verify {
            appMock.getApplication().acquireTokenSilentAsync(paramsMock)
            repoMock.token = accessToken

            assertEquals(token, accessToken)
        }
    }
}
