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

package com.omh.android.auth.api

import android.content.Context
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes
import com.omh.android.auth.api.utils.OmhAuthUtils
import kotlin.reflect.KClass

/**
 * Object that providers the correct implementation of the client for GMS or non GMS builds.
 */
class OmhAuthProvider private constructor(
    private val gmsPath: String?,
    private val nonGmsPath: String?
) {

    private val isSingleBuild = gmsPath != null && nonGmsPath != null


    /**
     * Provides an auth client interface to interact with the OMH Auth library. This uses reflection
     * to obtain the correct implementation for GMS or non GMS devices depending on what dependency
     * you have.
     *
     * @param context -> ideally your application context, but an activity context will also work.
     * @param scopes -> your oauth scopes in a collection. Do take into account that non GMS devices
     * won't be able to request more scopes after the first authorization.
     * @param clientId -> your client ID for the Android Application.
     * @param ownReflectionPath -> provide your own reflection path in case you are implementing
     * your own OMH module.
     *
     * @return an [OmhAuthClient] to interact with the Auth module.
     * @throws [OmhAuthException.ApiException] when reflection fails for any of the implementations
     * of the [OmhAuthFactory]. If this happens, look if you have configured correctly the gradle
     * plugin or if your obfuscation method hasn't tampered with the library files.
     */
    fun provideAuthClient(
        context: Context,
        scopes: Collection<String>,
        clientId: String,
    ): OmhAuthClient {
        val omhAuthFactory: OmhAuthFactory = try {
            getOmhAuthFactory(context)
        } catch (exception: ClassNotFoundException) {
            throw OmhAuthException.ApiException(
                statusCode = OmhAuthStatusCodes.DEVELOPER_ERROR,
                cause = exception
            )
        }
        return omhAuthFactory.getAuthClient(context, scopes, clientId)
    }

    private fun getOmhAuthFactory(context: Context) = when {
        isSingleBuild -> reflectSingleBuild(context)
        gmsPath != null -> getFactoryImplementation(gmsPath)
        nonGmsPath != null -> getFactoryImplementation(nonGmsPath)
        else -> throw OmhAuthException.ApiException(
            statusCode = OmhAuthStatusCodes.DEVELOPER_ERROR,
            cause = IllegalStateException("NO PATHS PROVIDED")
        )
    }

    private fun reflectSingleBuild(context: Context): OmhAuthFactory {
        return if (OmhAuthUtils.isGmsDevice(context)) {
            getFactoryImplementation(gmsPath!!)
        } else {
            getFactoryImplementation(nonGmsPath!!)
        }
    }

    private fun getFactoryImplementation(path: String): OmhAuthFactory {
        val clazz: KClass<out Any> = Class.forName(path).kotlin
        return clazz.objectInstance as OmhAuthFactory
    }

    class Builder {

        companion object {
            private const val NGMS_ADDRESS =
                "com.omh.android.auth.nongms.presentation.OmhAuthFactoryImpl"
            private const val GMS_ADDRESS = "com.omh.android.auth.gms.OmhAuthFactoryImpl"
        }

        private var gmsPath: String? = null
        private var nonGmsPath: String? = null

        @JvmOverloads
        fun addGmsPath(path: String? = GMS_ADDRESS): Builder {
            gmsPath = path
            return this
        }

        @JvmOverloads
        fun addNonGmsPath(path: String? = NGMS_ADDRESS): Builder {
            nonGmsPath = path
            return this
        }

        fun build(): OmhAuthProvider = OmhAuthProvider(gmsPath, nonGmsPath)
    }
}
