package com.openmobilehub.android.auth.core

import android.os.Bundle

typealias OmhCallback = (Bundle) -> Unit

class OmhCallbackManager {
    private var successCallbacks: MutableMap<String, OmhCallback> = mutableMapOf()
    private var errorCallbacks: MutableMap<String, OmhCallback> = mutableMapOf()

    fun addSuccessCallback(key: String, successCallback: OmhCallback) {
        successCallbacks[key] = successCallback
    }

    fun addErrorCallback(key: String, errorCallback: OmhCallback) {
        errorCallbacks[key] = errorCallback
    }

    fun getSuccessCallback(key: String): OmhCallback? {
        return successCallbacks[key]
    }

    fun getErrorCallback(key: String): OmhCallback? {
        return errorCallbacks[key]
    }

    fun removeCallback(key: String) {
        successCallbacks.remove(key)
        errorCallbacks.remove(key)
    }

    companion object {
        // TODO: Investigate if this annotation is needed
        @JvmStatic
        val instance = OmhCallbackManager()
    }
}