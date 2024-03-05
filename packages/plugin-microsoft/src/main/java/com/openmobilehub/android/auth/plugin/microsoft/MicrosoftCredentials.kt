package com.openmobilehub.android.auth.plugin.microsoft

import com.microsoft.identity.client.AcquireTokenSilentParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.exception.MsalException
import com.openmobilehub.android.auth.core.OmhCredentials
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhAuthStatusCodes
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MicrosoftCredentials(
    private val microsoftRepository: MicrosoftRepository,
    private val microsoftApplication: MicrosoftApplication,
    private val scopes: ArrayList<String>
) : OmhCredentials {
    override val accessToken: String?
        get() = microsoftRepository.token

    override fun refreshAccessToken(): OmhTask<String?> {
        return OmhTask(::requestAccessToken)
    }

    internal suspend fun requestAccessToken() = suspendCoroutine { continuation ->
        val currentAccount = microsoftApplication.getApplication().currentAccount.currentAccount
        val params =
            AcquireTokenSilentParameters.Builder()
                .forAccount(currentAccount)
                .fromAuthority(currentAccount.authority)
                .forceRefresh(true)
                .withScopes(scopes)
                .withCallback(object : AuthenticationCallback {
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        microsoftRepository.token = authenticationResult.accessToken
                        continuation.resume(authenticationResult.accessToken)
                    }

                    override fun onError(exception: MsalException?) {
                        continuation.resumeWithException(
                            OmhAuthException.ApiException(
                                OmhAuthStatusCodes.PROVIDER_ERROR,
                                exception
                            )
                        )
                    }

                    override fun onCancel() {
                        continuation.resumeWithException(
                            OmhAuthException.ApiException(
                                OmhAuthStatusCodes.CANCELED
                            )
                        )
                    }
                }).build()

        microsoftApplication.getApplication().acquireTokenSilentAsync(params)
    }
}
