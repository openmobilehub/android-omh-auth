package com.github.omhauthdemo.di

import com.github.authnongms.presentation.OmhAuthClientFactory
import com.github.openmobilehub.auth.OmhAuthClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    fun providesOmhAuthClient(): OmhAuthClient {
        return OmhAuthClientFactory.getAuthClient(
            clientId = "299050426753-r8qf4odfugftdlmadmu5elpkc1vd3kdh.apps.googleusercontent.com",
            scopes = "openid email profile",
        )
    }
}