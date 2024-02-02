package com.openmobilehub.android.auth.plugin.facebook

import com.openmobilehub.android.auth.core.async.OmhCancellable
import com.openmobilehub.android.auth.core.async.OmhTask

class FacebookTask : OmhTask<Unit>() {
    private var onExecute: (() -> Unit)? = null

    fun addOnExecute(callback: () -> Unit) {
        onExecute = callback
    }

    @Suppress("TooGenericExceptionCaught")
    override fun execute(): OmhCancellable? {
        try {
            onExecute?.invoke()
            super.onSuccess?.invoke(Unit)

        } catch (e: Exception) {
            super.onFailure?.invoke(e)
        }

        return null
    }
}
