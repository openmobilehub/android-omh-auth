package com.omh.android.auth.nongms.repositories

import com.omh.android.auth.api.models.OmhUserProfile
import com.omh.android.auth.nongms.data.user.UserRepositoryImpl
import com.omh.android.auth.nongms.data.user.datasource.UserDataSource
import com.omh.android.auth.nongms.domain.user.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class UserRepositoryTest {

    private val userDataSource: UserDataSource = mockk()

    @Test
    fun `GIVEN idToken and clientId WHEN handle ID token requested THEN ID token handled`() =
        runTest {
            val repository = createUserRepository()
            val idToken = "ID token"
            val clientId = "Client ID"

            coEvery { userDataSource.handleIdToken(any(), any()) } returns Unit

            repository.handleIdToken(idToken, clientId)

            coVerify { userDataSource.handleIdToken(idToken, clientId) }
        }

    @Test
    fun `GIVEN profile was stored WHEN profile requested THEN profile returned`() = runTest {
        val repository = createUserRepository()
        val omhUserProfile: OmhUserProfile = mockk()

        every { userDataSource.getProfileData() } returns omhUserProfile

        val result: OmhUserProfile? = repository.getProfileData()

        assertEquals(result, omhUserProfile)
    }

    @Test
    fun `GIVEN profile wasn't stored WHEN profile requested THEN null returned`() = runTest {
        val repository = createUserRepository()

        every { userDataSource.getProfileData() } returns null

        val result: OmhUserProfile? = repository.getProfileData()

        assertEquals(result, null)
    }

    private fun TestScope.createUserRepository(): UserRepository {
        val ioDispatcher = UnconfinedTestDispatcher(testScheduler)
        return UserRepositoryImpl(userDataSource, ioDispatcher)
    }
}
