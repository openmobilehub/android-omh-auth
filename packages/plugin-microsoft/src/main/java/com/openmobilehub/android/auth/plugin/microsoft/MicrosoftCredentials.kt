package com.openmobilehub.android.auth.plugin.microsoft

import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.exception.MsalException
import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MicrosoftCredentials(
    private val microsoftRepository: MicrosoftRepository,
    private val microsoftApplication: MicrosoftApplication
) : OmhCredentials {
    override val accessToken: String?
        get() = microsoftRepository.token

    override fun refreshToken(): OmhTask<String?> {
        return OmhTask(::requestAccessToken)
    }

    private suspend fun requestAccessToken() = suspendCoroutine { continuation ->
        val params =
            AcquireTokenParameters.Builder().withCallback(object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    microsoftRepository.token = authenticationResult.accessToken
                    continuation.resume(authenticationResult.accessToken)
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
            }).build()

        microsoftApplication.getApplication().acquireToken(params)
    }
}
