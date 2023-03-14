package com.github.authnongms.utils

import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object ThreadUtils {

    private val isOnMainThread: Boolean
        get() = Looper.myLooper() == Looper.getMainLooper()


    fun checkForMainThread() {
        if (isOnMainThread) {
            error("Running blocking function on main thread.")
        }
    }
}
