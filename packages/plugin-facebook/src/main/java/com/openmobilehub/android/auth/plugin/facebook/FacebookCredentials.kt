package com.openmobilehub.android.auth.plugin.facebook

import com.facebook.AccessToken
import com.facebook.FacebookException
import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.OmhTask
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FacebookCredentials : OmhCredentials {
    override fun refreshToken(): OmhTask<String?> {
        return OmhTask(::refreshAccessToken)
    }

    override val accessToken: String?
        get() = AccessToken.getCurrentAccessToken()?.token

    private suspend fun refreshAccessToken() = suspendCoroutine { continuation ->
        val callback = object : AccessToken.AccessTokenRefreshCallback {
            override fun OnTokenRefreshed(accessToken: AccessToken?) {
                continuation.resume(accessToken?.token)
            }

            override fun OnTokenRefreshFailed(exception: FacebookException?) {
                continuation.resumeWithException(exception!!)
            }
        }

        AccessToken.refreshCurrentAccessTokenAsync(callback)
    }
}
