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

package com.openmobilehub.android.auth.plugin.google.gms

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.OmhAuthFactory

@Keep
internal object OmhAuthFactoryImpl : OmhAuthFactory {
    @Suppress("UNUSED_PARAMETER")
    override fun getAuthClient(
        context: Context,
        scopes: Collection<String>,
        clientId: String,
        webClientId: String?
    ): OmhAuthClient {
        val scopeList: MutableList<Scope> = scopes.map(::Scope).toMutableList()
        val gsoBuilder = GoogleSignInOptions.Builder()
        if (!webClientId.isNullOrEmpty()) {
            gsoBuilder.requestIdToken(webClientId)
        }
        scopeList.forEach(gsoBuilder::requestScopes)
        val client: GoogleSignInClient = GoogleSignIn.getClient(context, gsoBuilder.build())
        return OmhAuthClientImpl(client)
    }

}
