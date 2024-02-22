package com.openmobilehub.android.auth.plugin.microsoft

import android.content.Context
import android.content.Intent
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MicrosoftAuthClient(val configFileResourceId: Int, val context: Context) : OmhAuthClient {
    private val microsoftApplication = MicrosoftApplication.getInstance()
    private val microsoftRepository = MicrosoftRepository.getInstance(context)

    override fun initialize(): OmhTask<Unit> {
        return OmhTask {
            @Suppress("SwallowedException")
            try {
                microsoftApplication.getApplication()
            } catch (e: OmhAuthException.NotInitializedException) {
                microsoftApplication.initialize(context, configFileResourceId)
            }
        }
    }

    override fun getLoginIntent(): Intent {
        return Intent(
            context, MicrosoftLoginActivity::class.java
        ).putExtra("configFileResourceId", configFileResourceId)
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
        return MicrosoftCredentials(microsoftRepository, microsoftApplication)
    }

    override fun revokeToken(): OmhTask<Unit> {
        TODO("Not yet implemented")
    }

    override fun signOut(): OmhTask<Unit> {
        TODO("Not yet implemented")
    }

    internal suspend fun getUserRequest(): OmhUserProfile = suspendCoroutine { continuation ->
        val queue: RequestQueue = Volley.newRequestQueue(context)

        val stringRequest = object : JsonObjectRequest(
            "https://graph.microsoft.com/v1.0/me",
            Response.Listener { jsonObject ->
                continuation.resume(
                    OmhUserProfile(
                        jsonObject.getString("givenName"),
                        jsonObject.getString("surname"),
                        jsonObject.getString("mail"),
                        null
                    )
                )
            },
            Response.ErrorListener(continuation::resumeWithException)
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                headers["Authorization"] =
                    "Bearer " + microsoftRepository.token

                return headers
            }
        }

        queue.add(stringRequest)
    }

}
