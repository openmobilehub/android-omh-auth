package com.openmobilehub.auth.api

import android.content.Context
import android.content.Intent
import com.openmobilehub.auth.api.models.OmhUserProfile

interface OmhAuthClient {

    interface Builder {

        fun build(): OmhAuthClient
    }

    fun getLoginIntent(context: Context): Intent

    fun getUser(context: Context): OmhUserProfile?

    fun getCredentials(context: Context): OmhCredentials

    /**
     * Logs out the user. This clears any stored data locally.
     */
    fun signOut(context: Context)
}
