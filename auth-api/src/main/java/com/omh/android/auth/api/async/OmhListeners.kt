package com.omh.android.auth.api.async

fun interface OmhSuccessListener<T> {
    fun onSuccess(result: T)
}

fun interface OmhErrorListener {
    fun onError(omhError: Exception)
}
