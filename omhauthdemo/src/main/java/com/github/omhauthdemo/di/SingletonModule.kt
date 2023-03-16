package com.github.omhauthdemo.di

import com.github.authnongms.presentation.OmhAuthFactory
import com.github.omhauthdemo.BuildConfig
import com.github.openmobilehub.auth.api.OmhAuthClient
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