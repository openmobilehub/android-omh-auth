package com.openmobilehub.android.auth.plugin.facebook

import com.openmobilehub.android.auth.core.async.OmhTask
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FacebookOmhTaskTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun shouldExecuteTaskSuccessfully() = runTest {
        val expectedResult = "Test Result"
        val taskFn: suspend () -> String = { expectedResult }
        val onSuccessMock = mockk<(String) -> Unit>(relaxed = true)

        val task = OmhTask(taskFn)

        task.addOnSuccess(onSuccessMock).execute()

        verify {
            onSuccessMock.invoke(expectedResult)
        }
    }

    @Test
    fun shouldExecuteTaskFailure() = runTest {
        val testException = Exception("Test Exception")
        val taskFn: suspend () -> String = { throw testException }
        val onFailureMock = mockk<(Exception) -> Unit>(relaxed = true)

        val task = OmhTask(taskFn)

        task.addOnFailure(onFailureMock).execute()

        verify {
            onFailureMock.invoke(testException)
        }
    }
}
