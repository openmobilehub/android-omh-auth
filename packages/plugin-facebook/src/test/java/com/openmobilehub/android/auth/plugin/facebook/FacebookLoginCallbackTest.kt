package com.openmobilehub.android.auth.plugin.facebook

import android.app.Activity
import android.content.Intent
import com.facebook.AccessToken
import com.facebook.AuthenticationToken
import com.facebook.FacebookException
import com.facebook.login.LoginResult
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
        val mockAuthenticationToken = mockk<AuthenticationToken>()
        val mockIntentResult = mockk<Intent>()

        mockkConstructor(Intent::class)
        every {
            anyConstructed<Intent>()
                .putExtra("accessToken", mockAccessToken)
                .putExtra("authenticationToken", mockAuthenticationToken)
        } returns mockIntentResult

        every { mockResult.accessToken } returns mockAccessToken
        every { mockResult.authenticationToken } returns mockAuthenticationToken
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
                .putExtra("authenticationToken", mockAuthenticationToken)
        }
    }

    @Test
    fun shouldHandleLoginCancel() {
        val activity = mockk<Activity>()

        every { activity.setResult(Activity.RESULT_CANCELED) } returns Unit
        every { activity.finish() } returns Unit

        val callback = FacebookLoginCallback(activity).getLoginCallback()

        callback.onCancel()

        verify {
            activity.setResult(Activity.RESULT_CANCELED)
            activity.finish()
        }
    }

    @Test
    fun shouldHandleLoginError() {
        val activity = mockk<Activity>()
        val exception = mockk<FacebookException>()
        val exceptionCause = Throwable()
        val mockIntentResult = mockk<Intent>()

        every { exception.cause } returns exceptionCause
        mockkConstructor(Intent::class)
        every {
            anyConstructed<Intent>()
                .putExtra("error", exceptionCause)
        } returns mockIntentResult
        every { activity.setResult(Activity.RESULT_CANCELED, any()) } returns Unit
        every { activity.finish() } returns Unit

        val callback = FacebookLoginCallback(activity).getLoginCallback()

        callback.onError(exception)

        verify {
            activity.setResult(Activity.RESULT_CANCELED, any())
            activity.finish()
            anyConstructed<Intent>()
                .putExtra("error", exceptionCause)
        }
    }
}