package com.openmobilehub.android.auth.core.async

interface IOmhTask<T> {
    fun addOnSuccess(successListener: OmhSuccessListener<T>): IOmhTask<T>
    fun addOnFailure(errorListener: OmhErrorListener): IOmhTask<T>
    fun execute(): OmhCancellable
}
