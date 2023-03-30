package com.omh.android.auth.nongms.utils.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

internal object LifecycleUtil {

    /**
     * Allows you to run a lambda on the next step of the lifecycle of a Fragment or an Activity
     * once. After it's executed, the observer is removed.
     *
     * @param lifecycle -> lifecycle to which the observer is added.
     * @param owner -> the lifecycle owner who will be running the code
     * @param action -> the action to execute
     */
    fun runOnResume(lifecycle: Lifecycle, owner: LifecycleOwner, action: () -> Unit) {
        val observer: LifecycleEventObserver = getObserver(action, owner)
        lifecycle.addObserver(observer)
    }

    private fun getObserver(action: () -> Unit, owner: LifecycleOwner): LifecycleEventObserver {
        return OnLifecycleStateObserver { source, event, observer ->
            runOnState(event, source, owner, observer, action)
        }
    }

    private fun runOnState(
        event: Lifecycle.Event,
        source: LifecycleOwner,
        owner: LifecycleOwner,
        observer: LifecycleEventObserver,
        action: () -> Unit
    ) {
        if (event == Lifecycle.Event.ON_RESUME && source == owner) {
            action()
            source.lifecycle.removeObserver(observer)
        }
    }
}
