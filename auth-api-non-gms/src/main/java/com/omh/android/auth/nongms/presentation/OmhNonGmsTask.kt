package com.omh.android.auth.nongms.presentation

import com.omh.android.auth.api.async.OmhCancellable
import com.omh.android.auth.api.async.OmhTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OmhNonGmsTask<T>(private val task: suspend () -> T) : OmhTask<T>() {

    private val coroutineContext = Dispatchers.Main + SupervisorJob()
    private val customScope: CoroutineScope = CoroutineScope(context = coroutineContext)


    override fun execute(): OmhCancellable {
        customScope.launch {
            executeScopedTask()
        }
        return OmhCancellable { coroutineContext.cancelChildren() }
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    private suspend fun executeScopedTask() {
        try {
            executeSuccess()
        } catch (e: Exception) {
            executeFailure(e)
        }
    }

    private suspend fun executeFailure(e: Exception) = withContext(Dispatchers.Main) {
        onFailure?.invoke(e)
    }

    private suspend fun executeSuccess() {
        val result = task()
        withContext(Dispatchers.Main) {
            onSuccess?.invoke(result)
        }
    }
}
