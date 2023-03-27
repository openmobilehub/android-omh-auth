package com.omh.android.auth.api

import android.content.Context

/**
 * A Factory to provide any interfaces of the OMH Auth module. This isn't designed to be used directly
 * from the client side, instead use the [OmhAuthProvider]
 */
interface OmhAuthFactory {

    /**
     * Provides the [OmhAuthClient] that is the main interactor with the Auth module.
     */
    fun getAuthClient(context: Context, scopes: Collection<String>, clientId: String): OmhAuthClient
}
