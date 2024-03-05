package com.openmobilehub.android.auth.plugin.microsoft

import android.app.Activity
import android.content.Intent
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.AuthenticationResult
import com.microsoft.identity.client.SignInParameters
import com.microsoft.identity.client.exception.MsalException
import com.openmobilehub.android.auth.core.models.OmhAuthException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

@RunWith(RobolectricTestRunner::class)
class MicrosoftLoginActivityTest {
    private val scopes = arrayListOf("User.Read")
    private val accessToken = "testAccessToken"
    private val intent = Intent().putStringArrayListExtra("scopes", scopes)
    private val callbackSlot = slot<AuthenticationCallback>()

    @Before
    fun setUp() {
        mockkObject(MicrosoftRepository.Companion)
        mockkObject(MicrosoftApplication.Companion)
        mockkStatic(SignInParameters::class)

        val paramsMock = mockk<SignInParameters>()

        every {
            MicrosoftRepository.getInstance(any()).token = accessToken
        } returns Unit
        every {
            SignInParameters
                .builder()
                .withActivity(any())
                .withScopes(scopes)
                .withCallback(capture(callbackSlot))
                .build()
        } returns paramsMock
        every {
            MicrosoftApplication.getInstance().getApplication().signIn(paramsMock)
        } returns Unit
    }

    @After
    fun tearDown() {
        unmockkObject(MicrosoftRepository.Companion)
        unmockkObject(MicrosoftApplication.Companion)
        mockkStatic(SignInParameters::class)
        callbackSlot.clear()
    }

    @Test
    fun shouldLogInSuccessfully() {
        val authResult = mockk<AuthenticationResult>()

        every { authResult.accessToken } returns accessToken

        val activity =
            Robolectric.buildActivity(MicrosoftLoginActivity::class.java, intent).create().get()

        callbackSlot.captured.onSuccess(authResult)

        val shadow = Shadows.shadowOf(activity)

        Assert.assertEquals(shadow.resultCode, Activity.RESULT_OK)
        Assert.assertEquals(shadow.resultIntent.getStringExtra("accessToken"), accessToken)
    }

    @Test
    fun shouldCancelLogIn() {
        val activity =
            Robolectric.buildActivity(MicrosoftLoginActivity::class.java, intent).create().get()

        callbackSlot.captured.onCancel()

        val shadow = Shadows.shadowOf(activity)

        Assert.assertEquals(shadow.resultCode, Activity.RESULT_CANCELED)
        Assert.assertEquals(
            shadow.resultIntent.getStringExtra("errorMessage"),
            OmhAuthException.LoginCanceledException().message
        )
    }

    @Test
    fun shouldFailLogIn() {
        val errorMessage = "testErrorMessage"
        val errorMock = mockk<MsalException>()

        every { errorMock.message } returns errorMessage

        val activity =
            Robolectric.buildActivity(MicrosoftLoginActivity::class.java, intent).create().get()

        callbackSlot.captured.onError(errorMock)

        val shadow = Shadows.shadowOf(activity)

        Assert.assertEquals(shadow.resultCode, Activity.RESULT_CANCELED)
        Assert.assertEquals(
            shadow.resultIntent.getStringExtra("errorMessage"),
            errorMessage
        )
    }
}
