package com.omh.android.auth.nongms.utils.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

internal class OnLifecycleStateObserver(private val runnable: LifecycleRunnable) : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        runnable.run(source, event, this)
    }
}
