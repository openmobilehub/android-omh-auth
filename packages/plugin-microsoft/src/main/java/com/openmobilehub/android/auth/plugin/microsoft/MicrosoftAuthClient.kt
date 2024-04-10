package com.openmobilehub.android.auth.plugin.microsoft

import android.content.Context
import android.content.Intent
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile
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

    override fun getLoginIntent(): Intent {
        return Intent(
            context, MicrosoftLoginActivity::class.java
        )
            .putExtra("configFileResourceId", configFileResourceId)
            .putStringArrayListExtra("scopes", scopes)
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
                    continuation.resumeWithException(
                        Exception(
                            response.errorBody()?.string()
                        )
                    )
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
