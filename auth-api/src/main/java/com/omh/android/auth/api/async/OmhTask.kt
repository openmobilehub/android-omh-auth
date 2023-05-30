package com.omh.android.auth.api.async

/**
 * A wrapper class for the async library that's used in a specific OMH implementation. This creates
 * a layer of abstraction between the developer and the library that doesn't force the preferred async
 * library on them.
 *
 * This will work akin to the Rx libraries which give you a disposable object to cancel any async
 * functionalities that are in execution, thus giving you a bit of control over the async operations.
 * It also allows you to add [onSuccess] and [onFailure] listeners in a modular way to react to the
 * outcomes.
 *
 * The [execute] function is important to obtain the [OmhCancellable] that allows you to cancel any
 * currently running async operation. Because not all libraries have a way to cancel async operations,
 * the [OmhCancellable] is returned as nullable.
 */
abstract class OmhTask<T> {

    protected var onSuccess: ((T) -> Unit)? = null
    protected var onFailure: ((Exception) -> Unit)? = null

    fun addOnSuccess(successListener: OmhSuccessListener<T>): OmhTask<T> {
        this.onSuccess = successListener::onSuccess
        return this
    }

    fun addOnFailure(errorListener: OmhErrorListener): OmhTask<T> {
        this.onFailure = errorListener::onError
        return this
    }

    /**
     * Executes the async operation and returns a way to cancel the operation if possible. Do take in
     * mind that not all async libraries have "cold" tasks. Some operation may already be in motion
     * when added to the wrapper. In this case, execute will only add the [onSuccess] and [onFailure]
     * listeners.
     *
     * @return an optional [OmhCancellable] in case the async operation can be cancelled.
     */
    abstract fun execute(): OmhCancellable?
}
