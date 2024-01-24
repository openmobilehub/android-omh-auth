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

package com.openmobilehub.android.plugin.auth.nongms.repositories

import com.openmobilehub.android.auth.core.models.OmhUserProfile
import com.openmobilehub.android.auth.plugin.google.nongms.data.user.UserRepositoryImpl
import com.openmobilehub.android.auth.plugin.google.nongms.data.user.datasource.UserDataSource
import com.openmobilehub.android.auth.plugin.google.nongms.domain.user.UserRepository
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
        return com.openmobilehub.android.auth.plugin.google.nongms.data.user.UserRepositoryImpl(
            userDataSource,
            ioDispatcher
        )
    }
}
