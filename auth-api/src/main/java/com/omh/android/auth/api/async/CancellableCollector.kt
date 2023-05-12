package com.omh.android.auth.api.async

/**
 * A collection of [OmhCancellable] that allows to cancel them all in a single operation. This is a
 * util class for handling the [OmhTask] returns.
 */
class CancellableCollector {

    private val cancellables : MutableCollection<OmhCancellable> = mutableSetOf()

    /**
     * Adds a cancelable object to the collections. This cancellable can be a nullable for better
     * compatibility with the [OmhTask] class.
     */
    fun addCancellable(cancellable: OmhCancellable?) {
        if (cancellable == null) return
        cancellables.add(cancellable)
    }

    /**
     * Cancels each [OmhCancellable] and clears the collection.
     */
    fun clear() {
        cancellables.forEach(OmhCancellable::cancel)
        cancellables.clear()
    }
}
