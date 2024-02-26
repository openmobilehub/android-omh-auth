package com.openmobilehub.android.auth.core.async

abstract class BaseOmhTask<T> : IOmhTask<T> {
    protected var onSuccess: ((T) -> Unit)? = null
    protected var onFailure: ((Exception) -> Unit)? = null

    abstract override fun execute(): OmhCancellable

    override fun addOnSuccess(successListener: OmhSuccessListener<T>): BaseOmhTask<T> {
        this.onSuccess = successListener::onSuccess
        return this
    }

    override fun addOnFailure(errorListener: OmhErrorListener): BaseOmhTask<T> {
        this.onFailure = errorListener::onError
        return this
    }
}
