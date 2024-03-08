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

package com.openmobilehub.android.auth.sample.di

import android.content.Context
import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.auth.core.OmhAuthProvider
import com.openmobilehub.android.auth.plugin.dropbox.DropboxAuthClient
import com.openmobilehub.android.auth.plugin.facebook.FacebookAuthClient
import com.openmobilehub.android.auth.plugin.microsoft.MicrosoftAuthClient
import com.openmobilehub.android.auth.sample.BuildConfig
import com.openmobilehub.android.auth.sample.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
    @Provides
    fun providesGoogleAuthClient(@ApplicationContext context: Context): OmhAuthClient {
        val omhAuthProvider = OmhAuthProvider.Builder()
            .addNonGmsPath(BuildConfig.AUTH_NON_GMS_PATH)
            .addGmsPath(BuildConfig.AUTH_GMS_PATH)
            .build()
        return omhAuthProvider.provideAuthClient(
            scopes = listOf("openid", "email", "profile"),
            clientId = BuildConfig.GOOGLE_CLIENT_ID,
            context = context
        )
    }

    @Provides
    fun providesFacebookAuthClient(@ApplicationContext context: Context): FacebookAuthClient {
        return FacebookAuthClient(
            scopes = arrayListOf("public_profile", "email"),
            context = context,
        )
    }

    @Provides
    fun providesMicrosoftAuthClient(@ApplicationContext context: Context): MicrosoftAuthClient {
        return MicrosoftAuthClient(
            configFileResourceId = R.raw.ms_auth_config,
            context = context,
            scopes = arrayListOf("User.Read"),
        )
    }

    @Provides
    fun providesDropboxAuthClient(@ApplicationContext context: Context): DropboxAuthClient {
        return DropboxAuthClient(
            scopes = arrayListOf("account_info.read"),
            context = context,
            appId = BuildConfig.DROPBOX_APP_KEY,
        )
    }
}