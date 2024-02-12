/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.auth.plugin.google.gms

import com.google.android.gms.tasks.Task
import com.openmobilehub.android.auth.core.async.OmhCancellable
import com.openmobilehub.android.auth.core.async.OmhTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OmhGmsTask<T>(private val task: Task<T>?) : OmhTask<T>() {
    private var suspendedTask: (suspend () -> T)? = null
    private val coroutineContext = Dispatchers.Main + SupervisorJob()
    private val customScope: CoroutineScope = CoroutineScope(context = coroutineContext)

    fun addSuspendedTask(callback: suspend () -> T) {
        suspendedTask = callback
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    private suspend fun executeScopedTask() {
        try {
            executeSuccess()
        } catch (e: Exception) {
            executeFailure(e)
        }
    }

    private suspend fun executeSuccess() {
        val result = suspendedTask!!.invoke()

        withContext(Dispatchers.Main) {
            onSuccess?.invoke(result)
        }
    }

    private suspend fun executeFailure(e: Exception) = withContext(Dispatchers.Main) {
        withContext(Dispatchers.Main) {
            onFailure?.invoke(e)
        }
    }

    override fun execute(): OmhCancellable? {
        if (task != null) {
            task.addOnSuccessListener { result -> onSuccess?.invoke(result) }
                .addOnFailureListener { e -> onFailure?.invoke(e) }

            return null
        }

        customScope.launch {
            executeScopedTask()
        }

        return OmhCancellable { coroutineContext.cancelChildren() }
    }
}
