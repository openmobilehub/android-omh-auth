package com.openmobilehub.android.auth.plugin.microsoft

import android.content.Context
import android.content.SharedPreferences
import com.openmobilehub.android.auth.core.utils.EncryptedSharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class MicrosoftRepositoryTest {
    @Test
    fun shouldPersistToken() {
        val testToken = "testToken"
        val sharedPreferencesMock = mockk<SharedPreferences>()
        val contextMock = mockk<Context>()

        mockkObject(EncryptedSharedPreferences)

        every {
            EncryptedSharedPreferences.getEncryptedSharedPrefs(
                contextMock,
                "microsoft"
            )
        } returns sharedPreferencesMock

        val repository = MicrosoftRepository.getInstance(contextMock)

        every { sharedPreferencesMock.edit().putString("token", testToken).apply() } returns Unit

        repository.token = testToken

        every { sharedPreferencesMock.getString("token", null) } returns testToken

        assertEquals(repository.token, testToken)

        verify {
            sharedPreferencesMock.edit().putString("token", testToken).apply()
            sharedPreferencesMock.getString("token", null)
        }
    }
}
