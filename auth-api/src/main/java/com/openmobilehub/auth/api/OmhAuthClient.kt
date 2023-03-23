package com.openmobilehub.auth.api

import android.content.Context
import android.content.Intent
import com.openmobilehub.auth.api.models.OmhUserProfile

interface OmhAuthClient {

    interface Builder {

        fun build(context: Context): OmhAuthClient
    }

    fun getLoginIntent(): Intent

    fun getUser(context: Context): OmhUserProfile?

    fun getCredentials(): OmhCredentials

    /**
     * Logs out the user. This clears any stored data locally.
     */
    fun signOut()
}
