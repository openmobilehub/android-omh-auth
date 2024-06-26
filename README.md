# Android OMH Auth Client Library

[![GitHub license](https://img.shields.io/github/license/openmobilehub/omh-auth)](https://github.com/openmobilehub/omh-auth/blob/main/LICENSE)
![GitHub contributors](https://img.shields.io/github/contributors/openmobilehub/omh-auth)
[![API](https://img.shields.io/badge/API-23%2B-green.svg?style=flat)](https://developer.android.com/studio/releases/platforms#6.0)

## Overview

The Android OMH Auth Client Library simplifies authentication integration for Android developers
across devices, supporting both Google Mobile Services (GMS) and non-GMS configurations. With a
unified interface, it enables easy incorporation of Google Sign-in and other third-party
authentication providers without maintaining separate codebases.

This README serves as a valuable learning resource, providing step-by-step instructions for setting
up an Android Studio project and effectively implementing the Android OMH Auth Client Library.
Whether you are new to Android development or an experienced programmer, this guide equips you with
the knowledge to seamlessly integrate authentication features into your applications. For a broader
understanding of OMH's philosophy and comprehensive capabilities, visit the official website
at https://openmobilehub.org.

## A single codebase, running seamlessly on any device

For instance, the following screenshots showcase multiple devices with Android, both with GMS and
Non-GMS. The same app works without changing a single line of code, supporting multiple auth
provider implementations.

<div align="center">

| GMS Device                                                                           | Non GMS Device                                                                           |
|--------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| <img src="https://github.com/openmobilehub/android-omh-auth/blob/main/assets/auth_gms.gif?raw=true" width=250 /> | <img src="https://github.com/openmobilehub/android-omh-auth/blob/main/assets/auth_non_gms.gif?raw=true" width=250 /> |

</div>

<details>
  <summary>Show more</summary>

| Facebook login                                                                            | Microsoft login                                                                     | Dropbox login                                                                            |
|-------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| <img src="https://github.com/openmobilehub/android-omh-auth/blob/main/assets/auth_facebook.gif?raw=true" width=250 /> | <img src="https://github.com/openmobilehub/android-omh-auth/blob/main/assets/auth_ms.gif?raw=true" width=250  /> | <img src="https://github.com/openmobilehub/android-omh-auth/blob/main/assets/auth_dropbox.gif?raw=true" width=250 /> |
<div align="center">

</div>
</details>

## Getting started

This section describes how to setup an Android Studio project to use the Android OMH Auth SDK for
Android. For greater ease, a base code will be used within the repository.

**Note: To quickly run a full-featured app with all Android OMH Auth functionality, refer to
the [`Sample App`](#sample-app) section and follow the provided steps.**

### Prerequisites

1. Android Studio is required. If you haven't already done
   so, [download](https://developer.android.com/studio/index.html)
   and [install](https://developer.android.com/studio/install.html?pkg=studio) it.
2. Ensure that you are using
   the [Android Gradle plugin](https://developer.android.com/studio/releases/gradle-plugin) version
   7.0 or later in Android Studio.

### Clone the repository

The easiest way is cloning the repository from the `starter-code` branch. Run this CLI command in
your terminal:

```
git clone --branch code-starter https://github.com/openmobilehub/android-omh-auth.git
```

You can always check what the final result should be in the module `sample-app` in the `main`
branch.

**Note: Before running the starter code application, make sure to follow
the [starter code app setup](https://github.com/openmobilehub/android-omh-auth/blob/code-starter/README.md) instructions.**

### Provider specific setup

There are different setup requirements based on the provider you will be including into your app.
Please find the specific setup instruction for the providers below:

- [Google GMS and non-GMS](https://openmobilehub.github.io/android-omh-auth/advanced-docs/plugin-google-gms/README)
- [Facebook](https://openmobilehub.github.io/android-omh-auth/advanced-docs/plugin-facebook/README)
- [Microsoft](https://openmobilehub.github.io/android-omh-auth/advanced-docs/plugin-microsoft/README)
- [Dropbox](https://openmobilehub.github.io/android-omh-auth/advanced-docs/plugin-dropbox/README)

### Adding Auth to your app.

First and foremost, the main interface that you'll be interacting with is called `OmhAuthClient`. It
contains all your basic authentication functionalities like login, getting the user profile, sign
out, revoking a token, etc.

#### Initialize client

In order to be able to interact with our OMH Auth client, we need to initialize it first.

The snippet below shows how to initialize the OMH Auth client. The `initialize` method returns an `OmhTask`. This is the interface to interact with async functionalities and subscribe to the success or error results. In the `MainActivity.kt`, add the following code to the `onViewCreated()` function:

```kotlin
lifecycleScope.launch(Dispatchers.IO) {
    authClientProvider.getClient(requireContext()).initialize()
        .addOnSuccess {
            setupUI()
        }
        .execute()
}
```

#### Get user

The snippet below shows how to check if there's a signed in user already in your application.
The `getUser` method returns an `OmhTask`. This is the interface to interact with async
functionalities and subscribe to the success or error results. A successful fetch will return an
object of the class `OmhUserProfile`. In the `MainActivity.kt`, add the following code to
the `setupGraph()` function:

```kotlin
val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

lifecycleScope.launch(Dispatchers.IO) {
    omhAuthClient.getUser()
        .addOnSuccess {
            navGraph.setStartDestination(R.id.logged_in_fragment)
            navController.graph = navGraph
        }
        .addOnFailure {
            navGraph.setStartDestination(R.id.login_fragment)
            navController.graph = navGraph
        }
        .execute()
}
```

#### Login

If no user is found, then we should request a login intent which will redirect the user to the
provider's auth screen, be it the Google SignIn UI or a custom tab that redirects the user to
provider specific auth page. In the `LoginFragment.kt`, add the following code to the `startLogin`
function:

```kotlin
// This will trigger the login flow.
val loginIntent = omhAuthClient.getLoginIntent()
loginLauncher.launch(loginIntent)
```

This should be used to start an activity for result. In the `LoginFragment.kt`, add the following
code to the `handleLoginResult(result: ActivityResult)`:

```kotlin
if (result.resultCode == Activity.RESULT_OK) {
    navigateToLoggedIn()
} else {
    val errorMessage = result.data?.getStringExtra("errorMessage")
    handleException(Exception(errorMessage))
}
```

If the returned `result` contains the account, then you can continue to the logged in activity of
your application.

#### Sign out

To sign-out, the SDK provides a straightforward functionality that returns an `OmhTask`. in addition
to subscribing to the success or error results, the `OmhTask` also provides a way to cancel it. A
cancellable token is provided after the `execute()` function is called. This can be stored in
the `CancellableCollector` class similar to the `CompositeDisposable` in RxJava. The sign-out action
will remove any and all relevant data of the user from the application storage. In
the `LoggedInFragment.kt`, add the following code to the `logout` function:

```kotlin
val cancellable = omhAuthClient.signOut()
    .addOnSuccess { navigateToLogin() }
    .addOnFailure(::showErrorDialog)
    .execute()

cancellableCollector.addCancellable(cancellable)
```

_Note:_ you can cancel all emitted cancellables within the collector running.

```kotlin
cancellableCollector.clear()
```

#### Revoke token

The SDK also provides a way to revoke the access token provided to the application. This works
similar to the sign-out functionality but on top of clearing all local data, this also makes a
request to the auth provider to revoke the token from the server. In the `LoggedInFragment.kt`, add
the following code to the `revokeToken` function:

```kotlin
val cancellable = omhAuthClient.revokeToken()
    .addOnSuccess { navigateToLogin() }
    .addOnFailure(::showErrorDialog)
    .execute()

cancellableCollector.addCancellable(cancellable)
```

> Some providers like in the case of Microsoft, doesn't provide a way to revoke the authentication token. For this reason, the default behavior for Microsoft when revoking a token is to sign out the user. This is identical to calling the `omhAuthClient.signOut()` function.

## Sample App

This repository includes a [auth-sample](/apps/auth-sample) that demonstrates the functionality of
the OMH Auth Client Library. By cloning the repo and executing the app, you can explore the various
features offered by the library. However, if you prefer a step-by-step approach to learn the SDK
from scratch, we recommend following the detailed Getting Started guide provided in this repository.
The guide will walk you through the implementation process and help you integrate the OMH Auth
Client Library into your projects effectively.

**Note: Before running the sample application, make sure to follow
the [specific setup](#provider-specific-setup) instructions for each provider.**

## Documentation

[Full documentation](https://openmobilehub.github.io/android-omh-auth/advanced-docs)

[Reference API](https://openmobilehub.github.io/android-omh-auth/api-docs)

## Provider Implementations / Plugins

OMH Auth SDK is open-source, promoting community collaboration and plugin support from other auth
providers to enhance capabilities and expand supported auth services. You can find more details in
the "[creating a custom implementation](https://openmobilehub.github.io/android-omh-auth/advanced-docs/core/advanced/Plugins)" section.

## Contributing

Please contribute! We will gladly review any pull requests. Make sure to read
the [CONTRIBUTING](/CONTRIBUTING.md) page first though.

## License

```
Copyright 2023 Open Mobile Hub

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
