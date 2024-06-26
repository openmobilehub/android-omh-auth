---
title: OMH Task
layout: default
parent: Advanced features
---

The OMH Task is a utility class that allows to abstract the async logic in libraries that are using
the specific implementations of the OMH SDKs. It also depends on other OMH classes and interfaces
that will be detailed upon here.

# OMH Task listeners

The OMH Task itself implements the `BaseOmhTask` which is an abstract class that defines two
functions to setup listeners for the two possible results: `Success` and `Failure`. This allows for
a modular build of the async task's behavior. The snippet below shows how to add this listeners to
the OMH Task:

```kotlin
val task: OmhTask<Unit> = OmhAuthClient.logout()
    .addOnSuccess {
        // Perform the on success task
    }
    .addOnFailure {
        // Perform the on failure task
    }
```

# OMH Task execution and cancellation

The OMH Task is designed as a cold task, meaning that you need to call the function `execute()` to
start the operation. In reality, not all async libraries are cold tasks, which mean that even before
they're wrapped into an OMH Task, the execution will had begun. To handle this, the `execute()`
function also is responsible for attaching the listeners to the wrapped task.

The wrapper also exposes a way to cancel the task, if the underlying library allows for it. Once you
call the function `execute()`, an `OmhCancellable` object will be returned. Add this to an instance
of the `CancellableCollector `to cancel multiple tasks at once or just use the `cancel()` function
manually whenever you need. The snippet below will show this in code:

```kotlin
val cancellableCollector = CancellableCollector()

fun logout() {
    val cancellable: OmhCancellable = OmhAuthClient.logout()
        .addOnSuccess {
            // Perform the on success task
        }
        .addOnFailure {
            // Perform the on failure task
        }
        .execute()
    // Add the cancellable to the collector for easy cancellation of multiple tasks.
    cancellableCollector.addCancellable(cancellable)
    // Alternatively you can cancel manually
    cancellable.cancel()
}

override fun onDestroy() {
    super.onDestroy()
    cancellableCollector.clear()
}
```
