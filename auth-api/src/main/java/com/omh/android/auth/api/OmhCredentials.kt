package com.omh.android.auth.api

/**
 * An abstraction to provide access to the user's tokens and their functionalities. This
 * doesn't require the user to be logged in to be created, it will just return null values or
 * exceptions when trying to refresh tokens.
 */
interface OmhCredentials {

    /**
     * This is a blocking async call and should never be called from the main thread. This is designed
     * for use in an authenticator or interceptor for an OkHttp client, which is why the call blocks
     * the tread for simulating a sync call.
     *
     * @return the newly minted access token for ease of use. Do take into account that it's automatically
     * stored and accessible in the future through [accessToken]. In case of a failure, a null value
     * is returned.
     */
    fun blockingRefreshToken(): String?

    /**
     * Fetches the access token from the secure storage if possible. If no token is stored, null is
     * returned.
     */
    val accessToken: String?
}
