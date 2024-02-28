package com.openmobilehub.android.auth.core

import com.openmobilehub.android.auth.core.async.IOmhTask
import com.openmobilehub.android.auth.core.async.OmhCancellable
import com.openmobilehub.android.auth.core.async.OmhErrorListener
import com.openmobilehub.android.auth.core.async.OmhSuccessListener
import com.openmobilehub.android.auth.core.models.OmhAuthException

class OmhLoginTask(private val callbackId: String, private val task: () -> Unit) : IOmhTask<Unit> {
    override fun addOnSuccess(successListener: OmhSuccessListener<Unit>): IOmhTask<Unit> {
        OmhCallbackManager.instance.addSuccessCallback(callbackId) {
            successListener.onSuccess(Unit)
        }
        return this
    }

    override fun addOnFailure(errorListener: OmhErrorListener): IOmhTask<Unit> {
        OmhCallbackManager.instance.addErrorCallback(callbackId) { result ->
            val error = result.getSerializable("error") as Throwable
            errorListener.onError(OmhAuthException.UnrecoverableLoginException(error))
        }
        return this
    }

    override fun execute(): OmhCancellable {
        task()

        return OmhCancellable { }
    }
}