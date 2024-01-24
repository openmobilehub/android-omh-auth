/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.plugin.auth.nongms.usecases

import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.plugin.google.nongms.domain.user.ProfileUseCase
import com.openmobilehub.android.auth.plugin.google.nongms.domain.user.UserRepository
import com.openmobilehub.android.auth.core.models.OmhUserProfile
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
    fun `given idToken and clientId then the ID token is resolved`() = runTest {
        val idToken = "idToken"
        val clientId = "clientId"
        coEvery { userRepository.handleIdToken(any(), any()) } returns Unit

        useCase.resolveIdToken(idToken, clientId)

        coVerify { userRepository.handleIdToken(idToken, clientId) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = OmhAuthException.UnrecoverableLoginException::class)
    fun `given idToken and clientId when they are empty then an exception is thrown`() = runTest {
        val idToken = " "
        val clientId = " "
        coEvery { userRepository.handleIdToken(any(), any()) } returns Unit

        useCase.resolveIdToken(idToken, clientId)

        coVerify(inverse = true) { userRepository.handleIdToken(idToken, clientId) }
    }

    @Test
    fun `when no profile data is available then null is returned`() {
        every { userRepository.getProfileData() } returns null

        val result = useCase.getProfileData()

        assertNull(result)
    }

    @Test
    fun `when profile data is available then an OmhUserProfile is returned`() {
        val profileData: OmhUserProfile = mockk()
        every { userRepository.getProfileData() } returns profileData

        val result = useCase.getProfileData()

        assertEquals(result, profileData)
    }
}
