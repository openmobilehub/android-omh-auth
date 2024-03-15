---
title: Plugins
layout: default
parent: Advanced features
---

For creating a custom implementation of our Android OMH Auth interfaces you'll need to get our
Android OMH Auth Core dependency:

```groovy
implementation("com.openmobilehub.android.auth:core:$version")
```

Once you've downloaded the dependencies, it's time to extend each interface. Below you can find a
step by step guide on how to do it.

# Developing your implementation

## Implementing the OMH Auth Client

As explained in
the [OMH Auth Client page](OMH-Auth-Client.md#obtaining-the-users-profile), this will
be the main interactor for the library. You will need to provide an implementation for each of the
functionalities so that the developers can interact with the Auth provider you'll be implementing.

### Signing in

Initialization of auth provider should be done in the `initialize` function. If you don't need to
do so, you can just return `IOmhTask` with noop function.

```kotlin
fun initialize(): IOmhTask<Unit>
```

You'll have to implement `getLoginIntent()` function that returns an intent for you auth
provider login screen.
Successful result should contain `ActivityResult` with `resultCode` set to `Activity.RESULT_OK`.
Failure or cancellation should contain `resultCode` set to `Activity.RESULT_CANCELED` and
an `"errorMessage"` extra.

```kotlin
fun getLoginIntent(): Intent
```

### Obtaining the user

Here you only need to provide an implementation for this function:

```kotlin
fun getUser(): OmhTask<OmhUserProfile>
```

The function should returns an `OmhTask`. This is the interface to interact with async
functionalities and subscribe to the success or error results. A successful fetch will return an
object of the class `OmhUserProfile`. This will help the developers identify if there was a previous
login or not.

### Signing out

Here you'll have to provide two implementations:

```kotlin
fun signOut(): OmhTask<Unit>

fun revokeToken(): OmhTask<Unit>
```

The functions `signOut()` and `revokeToken()` perform two variations of the sign out functionality
and you should connect them to your auth provider's respective features.

### Credentials

The only function you have to implement here is:

```kotlin
fun getCredentials(): OmhCredentials
```

More information about OmhCredentials can be found [here](OMH-Credentials.md)

## Implementing the OMH Auth Factory

This will be the most important part of your implementation as this will be how we reflect your
library with the provider. Save the path to this class, as we'll be using it further down the line.
This class will be responsible of instantiating your OMH Auth Client implementation and returning it
to the user as the interface `OmhAuthClient`. The only function to implement here is:

```kotlin
fun getAuthClient(context: Context, scopes: Collection<String>, clientId: String): OmhAuthClient
```

You'll have access to the application context in case you need it and the OAuth scopes that the user
will be requesting.

**Note: here are some examples of the path that you should be
storing: `com.example.app.factories.MyOwnFactoryImplementation`**

## Implementing the OMH Credentials interface

Here you'll need to provide a function and a parameter:

```kotlin
fun refreshAccessToken(): OmhTask<String?>

val accessToken: String?
```

The `accessToken` variable should return the stored token received in the login use case.

## Implementing the OMH Base Task abstract class

This is an abstraction for the async layer of your library. The idea is to avoid forcing the user to
use a specific async library and give the more flexibility with your OMH Auth implementation. You
can read more about it [here](OMH-Task.md). Here the only function you need to
implement is:

```kotlin
abstract override fun execute(): OmhCancellable
```

This should execute your async code and return a way to cancel the operation with
the `OmhCancellable` interface if possible. The cancellable interface can be represented as a lambda
for convenience.

# Using your implementation with the Android OMH Core Plugin

To use your newly created implementation with out plugin you just need to pass the reflection path
and the dependency string in the `Service` section like this:

```groovy
omhConfig {
    bundle("gms") {
        auth {
            gmsService {
                dependency = "com.example.app:custom-implementation:1.0"
                path = "com.example.app.factories.MyOwnFactoryImplementation"
            }
        }
    }
}
```

If you are not using the core plugin then you can always pass the path manually to the provider like
this:

```kotlin
val omhAuthProvider = OmhAuthProvider.Builder()
    .addGmsPath(com.example.app.factories.MyOwnFactoryImplementation)
    .build()
```

Just don't forget to add your custom implementation as a dependency to the project.

# Resources

You can always look into our own implementations of the Android OMH Auth
Core ([GMS](/packages/plugin-google-gms/) and [non GMS](/packages/plugin-google-non-gms/)) as a
reference to help you develop your own implementation.
