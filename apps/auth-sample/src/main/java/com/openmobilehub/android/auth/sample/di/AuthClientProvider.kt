package com.openmobilehub.android.auth.sample.di

import android.content.Context
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.plugin.facebook.FacebookAuthClient
import com.openmobilehub.android.auth.plugin.microsoft.MicrosoftAuthClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthClientProvider @Inject constructor(
    private val googleAuthClient: OmhAuthClient,
    private val facebookAuthClient: FacebookAuthClient,
    private val microsoftAuthClient: MicrosoftAuthClient
) {
    suspend fun getClient(context: Context): OmhAuthClient = withContext(Dispatchers.IO) {
        when (LoginState(context).getLoggedInProvider().firstOrNull()) {
            "google" -> googleAuthClient
            "facebook" -> facebookAuthClient
            "microsoft" -> microsoftAuthClient
            else -> throw Exception("No login provider found")
        }
    }
}