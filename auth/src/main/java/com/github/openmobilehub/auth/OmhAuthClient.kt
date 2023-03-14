package com.github.openmobilehub.auth

import android.content.Context
import android.content.Intent
import com.github.openmobilehub.auth.models.OmhUserProfile

interface OmhAuthClient {

    interface Builder {

        fun build(): OmhAuthClient
    }

    fun getLoginIntent(context: Context): Intent

    fun getUser(context: Context): OmhUserProfile?

    fun getCredentials(context: Context): OmhCredentials
}
