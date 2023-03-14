package com.github.openmobilehub.auth

/**
 * An abstraction to provide access to the user's tokens and their refresh functionalities. This
 * doesn't require the user to be logged in to be created, it will just return null values or
 * exceptions when trying to refresh tokens.
 */
interface OmhCredentials {

    /**
     * This is a blocking async call and should never be called from the main thread. Use the
     * functional interfaces to handle failure and success scenarios of the refresh functionality.
     *
     * @param onRefreshFailure -> functional interface to handle the refresh token failure scenario.
     * The user should be logged out and a new login request should be extended.
     * @return the newly minted access token for ease of use. Do take into account that it's automatically
     * stored and accessible in the future through [accessToken]. In case of a failure, a null value
     * is returned.
     */
    fun refreshAccessToken(onRefreshFailure: OnRefreshFailure): String?

    /**
     * Fetches the access token from the secure storage if possible. If no token is stored, null is
     * returned.
     */
    val accessToken: String?

    /**
     * Helper SAM interface for Java interoperability. Lambdas don't translate well to Java.
     */
    fun interface OnRefreshFailure {
        fun onFailure(exception: Exception)
    }
}
