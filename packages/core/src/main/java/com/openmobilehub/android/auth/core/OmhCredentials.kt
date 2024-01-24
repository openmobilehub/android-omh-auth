/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.auth.core

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
