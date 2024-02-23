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

package com.openmobilehub.android.auth.core.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * A wrapper class for the async library that's used in a specific OMH implementation. This creates
 * a layer of abstraction between the developer and the library that doesn't force the preferred async
 * library on them.
 *
 * This will work akin to the Rx libraries which give you a disposable object to cancel any async
 * functionalities that are in execution, thus giving you a bit of control over the async operations.
 * It also allows you to add [onSuccess] and [onFailure] listeners in a modular way to react to the
 * outcomes.
 *
 * The [execute] function is important to obtain the [OmhCancellable] that allows you to cancel any
 * currently running async operation. Because not all libraries have a way to cancel async operations,
 * the [OmhCancellable] is returned as nullable.
 */
open class OmhTask<T>(
    private val task: (suspend () -> T)?,
    private val coroutineContext: CoroutineContext? = Dispatchers.IO + SupervisorJob()
) {
    protected var onSuccess: ((T) -> Unit)? = null
    protected var onFailure: ((Exception) -> Unit)? = null

    private val customScope = CoroutineScope(context = coroutineContext!!)

    fun addOnSuccess(successListener: OmhSuccessListener<T>): OmhTask<T> {
        this.onSuccess = successListener::onSuccess
        return this
    }

    fun addOnFailure(errorListener: OmhErrorListener): OmhTask<T> {
        this.onFailure = errorListener::onError
        return this
    }

    /**
     * Executes the async operation and returns a way to cancel the operation if possible. Do take in
     * mind that not all async libraries have "cold" tasks. Some operation may already be in motion
     * when added to the wrapper. In this case, execute will only add the [onSuccess] and [onFailure]
     * listeners.
     *
     * @return an optional [OmhCancellable] in case the async operation can be cancelled.
     */
    @Suppress("TooGenericExceptionCaught")
    open fun execute(): OmhCancellable? {
        customScope.launch {
            try {
                val result = task!!.invoke()

                withContext(Dispatchers.Main) {
                    onSuccess?.invoke(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure?.invoke(e)
                }
            }
        }

        return OmhCancellable { coroutineContext!!.cancelChildren() }
    }
}
