package com.openmobilehub.android.auth.plugin.microsoft

import android.content.Context
import android.content.Intent
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile

class MicrosoftAuthClient(val configFileResourceId: Int, val context: Context) : OmhAuthClient {
    fun initialize(): OmhTask<Unit> {
        return MicrosoftOmhTask(::initializeClient)
    }

    private suspend fun initializeClient() {
        return MicrosoftApplication.getInstance().initialize(context, configFileResourceId)
    }

    override fun getLoginIntent(): Intent {
        return Intent(
            context, MicrosoftLoginActivity::class.java
        ).putExtra("configFileResourceId", configFileResourceId)
    }

    override fun handleLoginIntentResponse(data: Intent?) {
        if (data == null || !data.hasExtra(("accessToken"))) {
            throw OmhAuthException.LoginCanceledException()
        }

        if (data.hasExtra("error")) {
            throw OmhAuthException.UnrecoverableLoginException(
                data.getSerializableExtra("error") as Throwable
            )
        }
    }

    override fun getUser(): OmhTask<OmhUserProfile> {
        TODO("Not yet implemented")
    }

    override fun getCredentials(): Any? {
        TODO("Not yet implemented")
    }

    override fun revokeToken(): OmhTask<Unit> {
        TODO("Not yet implemented")
    }

    override fun signOut(): OmhTask<Unit> {
        TODO("Not yet implemented")
    }
}
