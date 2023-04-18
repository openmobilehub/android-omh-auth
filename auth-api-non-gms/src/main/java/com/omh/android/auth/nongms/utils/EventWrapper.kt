package com.omh.android.auth.nongms.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Handy event wrapper for events with Observables (like LiveData) to avoid reacting once it has been
 * handled.
 */
internal class EventWrapper<T>(private val content: T) {
    var hasBeenHandled: Boolean = false

    /**
     * Extracts the content if not handled and marks it as handled.
     *
     * @return null when content has been handled.
     */
    fun getContentIfHandled(): T? {
        if (hasBeenHandled) {
            return null
        }
        hasBeenHandled = true
        return content
    }

    /**
     * Allows you to pick the content without setting it as handled.
     */
    fun peekContent(): T {
        return content
    }
}

@OptIn(ExperimentalContracts::class)
internal fun EventWrapper<*>?.nullOrHandled(): Boolean {
    contract {
        returns(false) implies (this@nullOrHandled != null)
    }
    return this == null || hasBeenHandled
}
