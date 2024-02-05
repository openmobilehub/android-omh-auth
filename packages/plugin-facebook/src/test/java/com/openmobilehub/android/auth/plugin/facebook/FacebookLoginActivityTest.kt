package com.openmobilehub.android.auth.plugin.facebook

import android.app.Activity
import android.content.Intent
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FacebookLoginActivityTest {
    @MockK(relaxed = true)
    lateinit var mockCallbackManager: CallbackManager

    @MockK
    lateinit var mockLoginManager: LoginManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(LoginManager::class)
        mockkObject(LoginManager.Companion)
        every { LoginManager.getInstance() } returns mockLoginManager

        mockkStatic(CallbackManager.Factory::class)
        every { CallbackManager.Factory.create() } returns mockCallbackManager
    }

    @After
    fun tearDown() {
        unmockkStatic(LoginManager::class)
        unmockkObject(LoginManager.Companion)
    }

    @Test
    fun shouldRegisterCallbackAndLogIn() {
        val scopes = arrayListOf("email", "public_profile")

        every { mockLoginManager.registerCallback(mockCallbackManager, any()) } returns Unit
        every { mockLoginManager.logIn(any<Activity>(), any<Collection<String>>()) } returns Unit

        val intent = Intent().putStringArrayListExtra("scopes", scopes)
        val activity =
            Robolectric.buildActivity(FacebookLoginActivity::class.java, intent).create().get()

        verify {
            mockLoginManager.registerCallback(mockCallbackManager, any())
            mockLoginManager.logIn(activity, scopes)
        }
    }
}