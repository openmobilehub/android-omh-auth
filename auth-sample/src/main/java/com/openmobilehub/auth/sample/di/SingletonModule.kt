package com.openmobilehub.auth.sample.di

import com.openmobilehub.auth.nongms.presentation.OmhAuthFactory
import com.openmobilehub.auth.api.OmhAuthClient
import com.openmobilehub.auth.sample.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    fun providesOmhAuthClient(): OmhAuthClient {
        return OmhAuthFactory.getAuthClient(
            clientId = BuildConfig.CLIENT_ID,
            scopes = "openid email profile",
        )
    }
}