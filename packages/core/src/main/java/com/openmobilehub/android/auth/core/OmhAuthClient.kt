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
import com.openmobilehub.android.auth.core.async.IOmhTask
import com.openmobilehub.android.auth.core.models.OmhUserProfile

interface OmhAuthClient {

    interface Builder {

        fun build(context: Context): OmhAuthClient
    }

    fun initialize(): IOmhTask<Unit>

    fun getLoginIntent(): Intent

    fun getUser(): IOmhTask<OmhUserProfile>

    fun getCredentials(): OmhCredentials

    fun signOut(): IOmhTask<Unit>

    fun revokeToken(): IOmhTask<Unit>

    /**
     * This method provides an escape hatch to access the provider native SDK. This allows developers
     * to use the underlying provider's API directly, should they need to access a feature of the
     * provider that is not supported by the OMH plugin. Refer to the plugin's advanced documentation
     * for type to which to cast the instance.
     *
     * @return Provider SDK instance that should be type casted to access underlying provider's API
     */
    fun getProviderSdk(): Any
}
