package com.github.authnongms.presentation

import com.github.openmobilehub.auth.api.OmhAuthClient

object OmhAuthClientFactory {

    /**
     * Creates an auth client for the user of the non GMS type and returns it as the abstraction.
     * This should be used by the core plugin only.
     */
    fun getAuthClient(clientId: String, scopes: String): OmhAuthClient {
        val builder = OmhAuthClientImpl.Builder(clientId, scopes)
        return builder.build()
    }
}
