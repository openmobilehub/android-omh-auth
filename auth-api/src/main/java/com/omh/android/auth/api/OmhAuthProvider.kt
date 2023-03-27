package com.omh.android.auth.api

import android.content.Context
import kotlin.reflect.KClass

/**
 * Object that providers the correct implementation of the client for GMS or non GMS builds.
 */
object OmhAuthProvider {

    private const val NGMS_ADDRESS = "com.omh.android.auth.nongms.presentation.OmhAuthFactoryImpl"
    private const val GMS_ADDRESS = "com.omh.android.auth.gms.OmhAuthFactoryImpl"

    /**
     * Provides an auth client interface to interact with the OMH Auth library. This uses reflection
     * to obtain the correct implementation for GMS or non GMS devices depending on what dependency
     * you have.
     *
     * @param context -> ideally your application context, but an activity context will also work.
     * @param scopes -> your oauth scopes in a collection. Do take into account that non GMS devices
     * won't be able to request more scopes after the first authorization.
     * @param clientId -> your client ID for the Android Application.
     *
     * @return an [OmhAuthClient] to interact with the Auth module.
     */
    @SuppressWarnings("SwallowedException")
    fun provideAuthClient(
        context: Context,
        scopes: Collection<String>,
        clientId: String
    ): OmhAuthClient {
        val omhAuthFactory = try {
            val clazz: KClass<out Any> = Class.forName(GMS_ADDRESS).kotlin
            clazz.objectInstance as OmhAuthFactory
        } catch (e: ClassNotFoundException) {
            val clazz: KClass<out Any> = Class.forName(NGMS_ADDRESS).kotlin
            clazz.objectInstance as OmhAuthFactory
        }
        return omhAuthFactory.getAuthClient(context, scopes, clientId)
    }
}
