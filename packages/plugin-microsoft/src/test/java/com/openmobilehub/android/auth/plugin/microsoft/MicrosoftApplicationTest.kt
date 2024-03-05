package com.openmobilehub.android.auth.plugin.microsoft

import android.content.Context
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.openmobilehub.android.auth.core.models.OmhAuthException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class MicrosoftApplicationTest {
    @Test
    fun shouldThrowNotInitializedError() {
        assertThrows(OmhAuthException.NotInitializedException::class.java) {
            MicrosoftApplication.getInstance().getApplication()
        }
    }

    @Test
    fun shouldInitializeApplication() = runTest {
        val applicationMock = mockk<ISingleAccountPublicClientApplication>()
        val contextMock = mockk<Context>()
        val configResourceId = 0

        mockkStatic(PublicClientApplication::class)

        every {
            PublicClientApplication.createSingleAccountPublicClientApplication(
                contextMock,
                configResourceId
            )
        } returns applicationMock

        MicrosoftApplication.getInstance().initialize(contextMock, configResourceId)

        assertEquals(MicrosoftApplication.getInstance().getApplication(), applicationMock)
    }
}
