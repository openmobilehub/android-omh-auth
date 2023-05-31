package com.omh.android.auth.sample.di

import android.content.Context
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.OmhAuthProvider
import com.omh.android.auth.sample.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
    @Provides
    fun providesOmhAuthClient(@ApplicationContext context: Context): OmhAuthClient {
        val omhAuthProvider = OmhAuthProvider.Builder()
            .addNonGmsPath(BuildConfig.AUTH_GMS_PATH)
            .addGmsPath(BuildConfig.AUTH_NON_GMS_PATH)
            .build()
        return omhAuthProvider.provideAuthClient(
            scopes = listOf("openid", "email", "profile"),
            clientId = BuildConfig.CLIENT_ID,
            context = context
        )
    }
}