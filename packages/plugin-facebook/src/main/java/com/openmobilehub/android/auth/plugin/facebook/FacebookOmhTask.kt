package com.openmobilehub.android.auth.plugin.facebook

import com.openmobilehub.android.auth.core.async.OmhCancellable
import com.openmobilehub.android.auth.core.async.OmhTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FacebookOmhTask<T>(private val task: suspend () -> T) : OmhTask<T>() {
    private val coroutineContext = Dispatchers.Main + SupervisorJob()
    private val customScope: CoroutineScope = CoroutineScope(context = coroutineContext)

    @SuppressWarnings("TooGenericExceptionCaught")
    private suspend fun executeScopedTask() {
        try {
            executeSuccess()
        } catch (e: Exception) {
            executeFailure(e)
        }
    }

    private suspend fun executeSuccess() {
        val result = task.invoke()

        withContext(Dispatchers.Main) {
            onSuccess?.invoke(result)
        }
    }

    private suspend fun executeFailure(e: Exception) = withContext(Dispatchers.Main) {
        withContext(Dispatchers.Main) {
            onFailure?.invoke(e)
        }
    }

    override fun execute(): OmhCancellable {
        customScope.launch {
            executeScopedTask()
        }
        return OmhCancellable { coroutineContext.cancelChildren() }
    }
}
