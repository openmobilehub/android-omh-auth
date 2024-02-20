package com.openmobilehub.android.auth.plugin.microsoft

import android.content.Context
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.openmobilehub.android.auth.core.models.OmhAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class MicrosoftApplication {
    private var application: ISingleAccountPublicClientApplication? = null

    fun getApplication(): ISingleAccountPublicClientApplication {
        if (application == null) {
            throw OmhAuthException.NotInitializedException()
        }

        return application!!
    }

    suspend fun initialize(context: Context, configFileResourceId: Int) {
        withContext(Dispatchers.IO) {
            application = PublicClientApplication.createSingleAccountPublicClientApplication(
                context,
                configFileResourceId,
            )
        }
    }

    companion object {
        private var instance: MicrosoftApplication? = null

        fun getInstance(): MicrosoftApplication {
            if (instance == null) {
                instance = MicrosoftApplication()
            }

            return instance!!
        }
    }
}