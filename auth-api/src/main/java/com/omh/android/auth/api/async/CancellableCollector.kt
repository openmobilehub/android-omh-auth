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

package com.omh.android.auth.api.async

/**
 * A collection of [OmhCancellable] that allows to cancel them all in a single operation. This is a
 * util class for handling the [OmhTask] returns.
 */
class CancellableCollector {

    private val cancellables : MutableCollection<OmhCancellable> = mutableSetOf()

    /**
     * Adds a cancelable object to the collections. This cancellable can be a nullable for better
     * compatibility with the [OmhTask] class.
     */
    fun addCancellable(cancellable: OmhCancellable?) {
        if (cancellable == null) return
        cancellables.add(cancellable)
    }

    /**
     * Cancels each [OmhCancellable] and clears the collection.
     */
    fun clear() {
        cancellables.forEach(OmhCancellable::cancel)
        cancellables.clear()
    }
}
