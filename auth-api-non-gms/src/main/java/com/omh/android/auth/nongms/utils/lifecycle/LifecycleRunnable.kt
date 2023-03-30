package com.omh.android.auth.nongms.utils.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

fun interface LifecycleRunnable {
    fun run(source: LifecycleOwner, event: Lifecycle.Event, observer: LifecycleEventObserver)
}
