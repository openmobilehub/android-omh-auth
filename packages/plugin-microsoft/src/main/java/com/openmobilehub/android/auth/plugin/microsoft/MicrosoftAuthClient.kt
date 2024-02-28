package com.openmobilehub.android.auth.plugin.microsoft

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.SignInParameters
import com.microsoft.identity.client.exception.MsalException
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MicrosoftAuthClient(
    val configFileResourceId: Int,
    val scopes: ArrayList<String>,
    val context: Context
) : OmhAuthClient {
    private val microsoftApplication = MicrosoftApplication.getInstance()
    private val microsoftRepository = MicrosoftRepository.getInstance(context)
    private val microsoftApiService = MicrosoftApiService.service

    override fun initialize(): OmhTask<Unit> {
        return OmhTask({
            @Suppress("SwallowedException")
            try {
                microsoftApplication.getApplication()
            } catch (e: OmhAuthException.NotInitializedException) {
                microsoftApplication.initialize(context, configFileResourceId)
            }
        })
    }

    fun signIn(activity: Activity): OmhTask<Unit> {
        return OmhTask({
            signInRequest(activity)
        }, Dispatchers.Main + SupervisorJob())
    }

    private suspend fun signInRequest(activity: Activity) = suspendCoroutine { continuation ->
        val params =
            SignInParameters
                .builder()
                .withActivity(activity)
                .withScopes(scopes)
                .withCallback(object : AuthenticationCallback {
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        MicrosoftRepository.getInstance(context).token =
                            authenticationResult.accessToken
                        continuation.resume(Unit)
                    }

                    override fun onError(exception: MsalException?) {
                        continuation.resumeWithException(
                            OmhAuthException.UnrecoverableLoginException(
                                exception
                            )
                        )
                    }

                    override fun onCancel() {
                        continuation.resumeWithException(OmhAuthException.LoginCanceledException())
                    }
                })
                .build()

        MicrosoftApplication.getInstance().getApplication()
            .signIn(params)
    }

    override fun getLoginIntent(): Intent {
        return Intent(
            context, MicrosoftLoginActivity::class.java
        )
            .putExtra("configFileResourceId", configFileResourceId)
            .putStringArrayListExtra("scopes", scopes)
    }

    override fun handleLoginIntentResponse(data: Intent?) {
        if (data != null && data.hasExtra("error")) {
            throw OmhAuthException.UnrecoverableLoginException(
                data.getSerializableExtra("error") as Throwable
            )
        }

        if (data == null || !data.hasExtra(("accessToken"))) {
            throw OmhAuthException.LoginCanceledException()
        }
    }

    override fun getUser(): OmhTask<OmhUserProfile> {
        return OmhTask(::getUserRequest)
    }

    override fun getCredentials(): MicrosoftCredentials {
        return MicrosoftCredentials(microsoftRepository, microsoftApplication, scopes)
    }

    override fun revokeToken(): OmhTask<Unit> {
        return OmhTask(::signOutRequest)
    }

    override fun signOut(): OmhTask<Unit> {
        return OmhTask(::signOutRequest)
    }

    internal suspend fun getUserRequest(): OmhUserProfile = suspendCoroutine { continuation ->
        val call =
            microsoftApiService.getUserProfile("Bearer " + microsoftRepository.token)

        call.enqueue(object : Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                if (response.isSuccessful) {
                    val user = response.body()

                    continuation.resume(
                        OmhUserProfile(
                            user?.givenName,
                            user?.surname,
                            user?.mail,
                            null
                        )
                    )
                } else {
                    continuation.resumeWithException(Exception(response.message()))
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }

    internal suspend fun signOutRequest() = suspendCoroutine { continuation ->
        val success = microsoftApplication.getApplication().signOut()
        microsoftRepository.token = null

        if (!success) {
            continuation.resumeWithException(Exception("Failed to sign out"))
        } else {
            continuation.resume(Unit)
        }
    }
}
