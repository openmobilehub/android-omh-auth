package com.openmobilehub.android.auth.plugin.microsoft

import android.content.Context
import android.content.Intent
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile

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
        TODO("Not yet implemented")
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
}
