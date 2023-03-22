package com.openmobilehub.auth.sample.di

import android.content.Context
import com.openmobilehub.auth.api.OmhAuthClient
import com.openmobilehub.auth.gms.OmhAuthFactory
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
        return OmhAuthFactory.getAuthClient(
            scopes = listOf("openid", "email", "profile"),
            context = context
        )
    }
}