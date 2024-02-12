package com.openmobilehub.android.auth.sample.di

import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.plugin.facebook.FacebookAuthClient
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthClientProvider @Inject constructor(
    private val googleAuthClient: OmhAuthClient,
    private val facebookAuthClient: FacebookAuthClient
) {
    suspend fun getClient() = suspendCoroutine { continuation ->
        googleAuthClient.getUser()
            .addOnSuccess {
                continuation.resume(googleAuthClient)
            }
            .addOnFailure {
                facebookAuthClient.getUser()
                    .addOnSuccess {
                        continuation.resume(facebookAuthClient)
                    }
                    .addOnFailure {
                        // FIXME:This will only throw Facebook exceptions
                        continuation.resumeWithException(it)
                    }
                    .execute()
            }
            .execute()
    }
}