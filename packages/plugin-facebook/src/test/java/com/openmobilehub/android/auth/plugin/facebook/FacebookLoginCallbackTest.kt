package com.openmobilehub.android.auth.plugin.facebook

import android.app.Activity
import android.content.Intent
import com.facebook.AccessToken
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.openmobilehub.android.auth.core.models.OmhAuthException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import org.junit.Test

class FacebookLoginCallbackTest {
    @Test
    fun shouldHandleLoginSuccess() {
        val activity = mockk<Activity>()
        val mockResult = mockk<LoginResult>()

        val mockAccessToken = mockk<AccessToken>()
        val mockIntentResult = mockk<Intent>()

        mockkConstructor(Intent::class)
        every {
            anyConstructed<Intent>()
                .putExtra("accessToken", mockAccessToken)
        } returns mockIntentResult

        every { mockResult.accessToken } returns mockAccessToken
        every { activity.setResult(Activity.RESULT_OK, any()) } returns Unit
        every { activity.finish() } returns Unit

        val callback = FacebookLoginCallback(activity).getLoginCallback()

        callback.onSuccess(mockResult)

        verify {
            activity.setResult(
                Activity.RESULT_OK,
                any()
            )
            activity.finish()
            anyConstructed<Intent>()
                .putExtra("accessToken", mockAccessToken)
        }
    }

    @Test
    fun shouldHandleLoginCancel() {
        val errorMessage = OmhAuthException.LoginCanceledException().message
        val activity = mockk<Activity>()
        val mockIntentResult = mockk<Intent>()

        mockkConstructor(Intent::class)
        every {
            anyConstructed<Intent>()
                .putExtra("errorMessage", errorMessage)
        } returns mockIntentResult

        every { activity.setResult(Activity.RESULT_CANCELED, any()) } returns Unit
        every { activity.finish() } returns Unit

        val callback = FacebookLoginCallback(activity).getLoginCallback()

        callback.onCancel()

        verify {
            activity.setResult(Activity.RESULT_CANCELED, any())
            activity.finish()
            anyConstructed<Intent>()
                .putExtra("errorMessage", errorMessage)
        }
    }

    @Test
    fun shouldHandleLoginError() {
        val activity = mockk<Activity>()
        val exception = mockk<FacebookException>()
        val errorMessage = "Error message"
        val mockIntentResult = mockk<Intent>()

        every { exception.message } returns errorMessage
        mockkConstructor(Intent::class)
        every {
            anyConstructed<Intent>()
                .putExtra("errorMessage", errorMessage)
        } returns mockIntentResult
        every { activity.setResult(Activity.RESULT_CANCELED, any()) } returns Unit
        every { activity.finish() } returns Unit

        val callback = FacebookLoginCallback(activity).getLoginCallback()

        callback.onError(exception)

        verify {
            activity.setResult(Activity.RESULT_CANCELED, any())
            activity.finish()
            anyConstructed<Intent>()
                .putExtra("errorMessage", errorMessage)
        }
    }
}
