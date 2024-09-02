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

/**
 * A Factory to provide any interfaces of the OMH Auth module. This isn't designed to be used directly
 * from the client side, instead use the [OmhAuthProvider]
 */
interface OmhAuthFactory {

    /**
     * Provides the [OmhAuthClient] that is the main interactor with the Auth module.
     */
    fun getAuthClient(
        context: Context,
        scopes: Collection<String>,
        clientId: String,
        webClientId: String?
    ): OmhAuthClient
}
