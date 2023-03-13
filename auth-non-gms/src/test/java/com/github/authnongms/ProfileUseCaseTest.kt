package com.github.authnongms

import com.github.authnongms.domain.user.ProfileUseCase
import com.github.authnongms.domain.user.UserRepository
import com.github.openmobilehub.auth.models.OmhUserProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class ProfileUseCaseTest {

    private val userRepository: UserRepository = mockk()
    private val useCase = ProfileUseCase(userRepository)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when idToken and clientId are provided the token is handled`() = runTest {
        val idToken = "idToken"
        val clientId = "clientId"
        coEvery { userRepository.handleIdToken(any(), any()) } returns Unit

        useCase.resolveIdToken(idToken, clientId)

        coVerify { userRepository.handleIdToken(idToken, clientId) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when idToken or clientId are empty the token is not handled`() = runTest {
        val idToken = " "
        val clientId = " "

        useCase.resolveIdToken(idToken, clientId)

        coVerify(inverse = true) { userRepository.handleIdToken(idToken, clientId) }
    }

    @Test
    fun `when no profile data is available a null is returned`() {
        every { userRepository.getProfileData() } returns null

        val result = useCase.getProfileData()

        assertNull(result)
    }

    @Test
    fun `when profile data is available an object is returned`() {
        val profileData: OmhUserProfile = mockk()
        every { userRepository.getProfileData() } returns profileData

        val result = useCase.getProfileData()

        assertEquals(result, profileData)
    }
}
