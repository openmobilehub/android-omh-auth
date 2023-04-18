package com.omh.android.auth.nongms.utils

import android.os.Looper

internal object ThreadUtils {

    private val isOnMainThread: Boolean
        get() = Looper.myLooper() == Looper.getMainLooper()


    fun checkForMainThread() {
        if (isOnMainThread) {
            error("Running blocking function on main thread.")
        }
    }
}
