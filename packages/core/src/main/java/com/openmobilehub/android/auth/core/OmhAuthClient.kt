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

import android.content.Context
import android.content.Intent
import com.openmobilehub.android.auth.core.async.OmhTask
import com.openmobilehub.android.auth.core.models.OmhAuthException
import com.openmobilehub.android.auth.core.models.OmhUserProfile

interface OmhAuthClient {

    interface Builder {

        fun build(context: Context): OmhAuthClient
    }

    fun getLoginIntent(): Intent

    fun getUser(): OmhUserProfile?

    /**
     * This method is designed for internal OMH libraries first and foremost. In case you need to
     * access the account credentials yourself, do it with care, as you will have to cast it into
     * the appropriate class. Read below to see what classes the returned object can be cast safely.
     *
     * @return the credential object associated with the specific implementation of the API.
     *
     * For GMS this returns a GoogleAccountCredential object to allow other libraries, like Drive, to
     * authenticate their requests.
     *
     * For non GMS this returns the a [OmhCredentials] object that allows you to access and refresh
     * the access token using REST operations.
     */
    fun getCredentials(): Any?

    /**
     * Logs out the user. This clears any stored data locally.
     */

    fun signOut(): OmhTask<Unit>

    @Throws(OmhAuthException::class)
    fun getAccountFromIntent(data: Intent?): OmhUserProfile

    fun revokeToken(): OmhTask<Unit>
}
