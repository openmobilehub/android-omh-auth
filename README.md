[![GitHub license](https://img.shields.io/github/license/openmobilehub/omh-auth)](https://github.com/openmobilehub/omh-auth/blob/main/LICENSE)
![GitHub contributors](https://img.shields.io/github/contributors/openmobilehub/omh-auth)
[![API](https://img.shields.io/badge/API-23%2B-green.svg?style=flat)](https://developer.android.com/studio/releases/platforms#6.0)

# OMH Auth Client Library Overview

OMH Auth is an Android client library that makes it easy to integrate auth providers on both Google
Mobile Services (GMS) and non-GMS devices. It eliminates the need for separate codebases for
different Android builds.

With the OMH Auth Client Library, you can easily add Google Sign in and other third-party
authentication providers to your applications, regardless of whether the device has GMS or not. The
library takes care of the technical details, providing a unified interface and components for a
consistent auth experience.

## A single codebase, running seamlessly on any device

For instance, the following screenshots showcase multiple devices with Android, both with GMS and
Non-GMS. The same app works without changing a single line of code, supporting multiple map provider
implementations (Google Maps and OpenStreetMap).
<div align="center">

| Non-GMS</br>Pixel 6 | GMS</br>Pixel 6 |
| ----------------------- | ------------ |
| <img src="https://github.com/openmobilehub/omh-maps/assets/32938335/633fd287-e1f6-440f-9f0e-5a274f0c8227"> | <img src="https://github.com/openmobilehub/omh-auth/assets/32938335/7d345ac3-b7b9-49b4-8044-6c115df992ad"> |

</div>

# Getting started

This section describes how to setup an Android Studio project to use the OMH Auth SDK for Android.
For greater ease, a base code will be used within the repository.

**Note: To quickly run a full-featured app with all OMH Auth functionality, refer to
the [`Sample App`](#sample-app) section and follow the provided steps.**

## Set up the development environment

1. Android Studio is required. If you haven't already done
   so, [download](https://developer.android.com/studio/index.html)
   and [install](https://developer.android.com/studio/install.html?pkg=studio) it.
2. Ensure that you are using
   the [Android Gradle plugin](https://developer.android.com/studio/releases/gradle-plugin) version
   7.0 or later in Android Studio.

## Clone the repository

Go to the branch "starter-code". The easiest way is cloning the repository.

- Open Terminal.
- Type git clone, and then paste the URL:

   ```
   git clone --branch code-starter https://github.com/openmobilehub/omh-auth.git
   ```

- Press "Enter" to create your local clone.

You can always check what the final result should be in the module `sample-app` in the main or
develop branches.

## Set up your Google Cloud project for applications with Google Services (Google Auth)

Complete the required Cloud Console setup steps by clicking through the following tabs:

### Steps

1. [Go to the Google Cloud Console and open the project selector page.](https://console.cloud.google.com/projectselector2/home/dashboard?utm_source=Docs_ProjectSelector&_gl=1*1ylhfe0*_ga*MTUwMDIzODY1Ni4xNjc1OTYyMDgw*_ga_NRWSTWS78N*MTY4MjA4ODIyNS44NS4xLjE2ODIwODgyMzcuMC4wLjA.)
2. Click on "Create Project" to start creating a new Cloud project.
3. [Go to the Credentials page](https://console.cloud.google.com/apis/credentials).
4. On the Credentials page, click on "Create credentials" and choose "OAuth Client ID".
5. In the "Application Type" option, select "Android".
6. Set your application package name (Use "com.omh.android.auth.sample" if you are following the
   starter-code)
7. Add your debug or release SHA-1 fingerprint:
    1. Create a new keystore and sign your APK/Bundle by following the instructions in the official
       Android
       documentation: [Generate a Keystore](https://developer.android.com/studio/publish/app-signing#generate-key)
       .
    2. Obtain the certificate fingerprint (SHA-1) from your keystore using the guidelines in the
       official Google Developers
       documentation: [Using keytool on the certificate](https://developers.google.com/android/guides/client-auth#using_keytool_on_the_certificate)
       .
9. In the [OAuth consent screen](https://console.cloud.google.com/apis/credentials/consent) add the
   test users that you will be using for QA and development. Without this step you won't be able to
   access the application while it's in testing mode.
10. You're all set!

## Add the Client ID to your app

You should not check your Client ID into your version control system, so it is recommended
storing it in the `local.properties` file, which is located in the root directory of your project.
For more information about the `local.properties` file,
see [Gradle properties](https://developer.android.com/studio/build#properties-files)
[files](https://developer.android.com/studio/build#properties-files).

1. Open the `local.properties` in your project level directory, and then add the following code.
   Replace `YOUR_CLIENT_ID` with your API key.
   `CLIENT_ID=YOUR_CLIENT_ID`
2. Save the
   fileand [sync your project with Gradle](https://developer.android.com/studio/build#sync-files).

## Gradle configuration

To incorporate OMH Auth into your project, you have two options: utilize the OMH Core Plugin or
directly include the OMH Client libraries dependencies. This plugin simplifies the addition of
Gradle dependencies, allowing you to effortlessly manage and include the necessary dependencies for
seamless integration.

### Add OMH Core plugin

The subsequent instructions will outline the necessary steps for including the OMH Core Plugin as a
Gradle dependency.

1. In your "auth-starter-sample" module-level `build.gradle` under the `plugins` element add the
   plugin id.

   ```
   plugins {
      ...
      id("com.openmobilehub.android.omh-core")
   }
   ```

2. Save the file
   and [sync Project with Gradle Files](https://developer.android.com/studio/build#sync-files).

### Configure the OMH Core plugin

In your `auth-starter-sample` module-level `build.gradle` file add the following code at the end of
the file.

   ```
   omhConfig {
      bundle("singleBuild") {
         auth {
            gmsService {
               dependency = "com.openmobilehub.android:auth-api-gms:1.0"
            }
            nonGmsService {
               dependency = "com.openmobilehub.android:auth-api-non-gms:1.0"
            }
         }
      }
      bundle("gms") {
         auth {
            gmsService {
               dependency = "com.openmobilehub.android:auth-api-gms:1.0"
            }
         }
      }
      bundle("nongms") {
         auth {
            nonGmsService {
               dependency = "com.openmobilehub.android:auth-api-non-gms:1.0"
            }
         }
      }
   }
   ```

_**NOTE: This section covers concepts about the core plugin**_

In your "auth-starter-sample" module-level `build.gradle` file is required to configure
the `omhConfig`. The `omhConfig` definition is used to extend the existing Android Studio
variants in the core plugin. For more details `omhConfig`
see [OMH Core](https://github.com/openmobilehub/omh-core/tree/release/1.0).

#### Basic configuration

In this step, you will define the OMH Core Plugin bundles to generate multiple build variants with specific suffixes as their names. For example, if your project has `release` and `debug` variants with `singleBuild`, `gms`, and `nonGms` OMH bundles, the following build variants will be generated:

- `releaseSingleBuild`, `releaseGms`, and `releaseNonGms`
- `debugSingleBuild`, `debugGms`, and `debugNonGms`

##### Variant singleBuild

    - Define the `Service`. In this example is auth.
    - Define the `ServiceDetails`. In this example are `gmsService` and `nonGmsService`.
    - Define the dependency and the path. In this example
      are `com.openmobilehub.android:auth-api-gms:1.0"`
      and `com.openmobilehub.android:auth-api-non-gms:1.0`.

**Note:** It's important to observe how a single build encompasses both GMS (Google MobileServices)
and Non-GMS configurations.

##### Variant gms

    - Define the `Service`. In this example is auth.
    - Define the `ServiceDetails` . In this example is `gmsService`.
    - Define the dependency and the path. In this example
      is `com.openmobilehub.android:auth-api-gms:1.0"`.

**Note:** gms build covers only GMS (Google Mobile Services).

##### Variant nongms

    - Define the `Service`. In this example is auth.
    - Define the `ServiceDetails` . In this example is `nonGmsService`.
    - Define the dependency and the path. In this example
      is `com.openmobilehub.android:auth-api-non-gms:1.0`.

**Note:** nongms build covers only Non-GMS configurations.

3. Save and [sync Project with Gradle Files](https://developer.android.com/studio/build#sync-files).
4. Now you can select a build variant. To change the build variant Android Studio uses, do one of
   the following:
    - Select "Build" > "Select Build Variant..." in the menu.
    - Select "View" > "Tool Windows" > "Build Variants" in the menu.
    - Click the "Build Variants" tab on the tool window bar.

5. You can select any of the 3 variants for the `:auth-starter-sample`:
    - "singleBuild" variant builds for GMS (Google Mobile Services) and Non-GMS devices without
      changes to the code.(Recommended)
    - "gms" variant builds for devices that has GMS (Google Mobile Services).
    - "nongms" variant builds for devices that doesn't have GMS (Google Mobile Services).
6. In the SingletonModule.kt file in the `:auth-starter-sample` module add the following code to
   provide the OMH Auth Client.

   ```kotlin
   val omhAuthProvider = OmhAuthProvider.Builder()
       .addNonGmsPath(BuildConfig.AUTH_GMS_PATH)
       .addGmsPath(BuildConfig.AUTH_NON_GMS_PATH)
       .build()
   return omhAuthProvider.provideAuthClient(
       scopes = listOf("openid", "email", "profile"),
       clientId = BuildConfig.CLIENT_ID,
       context = context
   )
   ```

*Note*: we'd recommend to store the client as a singleton with your preferred dependency injection
library as this will be your only gateway to the OMH Auth SDK and it doesn't change in runtime at
all.

## Adding Auth to your app.

First and foremost, the main interface that you'll be interacting with is called `OmhAuthClient`. In
contains all your basic authentication functionalities like login, sign out and check for a user
profile.

#### Check for an existing user

The snippet below shows how to check if there's a signed in user already in your application. If no
one has logged in yet, then it will return a null value. A successful fetch will return an object of
the class `OmhUserProfile`. In the LoginActivity.kt file in the `:auth-starter-sample` module add
the following code.

```kotlin
if (omhAuthClient.getUser() != null) {
    navigateToLoggedIn()
}
```

#### Login

If no user is found, then we should request a login intent which will redirect the user to the
provider's auth screen, be it the Google SignIn UI or a custom tab that redirects the user to
Google's Auth page. This should be used to start an activity for result ( The snippet below uses the
latest method of doing so, but it's the same as
using `startActivityForResult(Intent intent, int code)`. In the LoginActivity.kt file in
the `:auth-starter-sample` module add the following code.

```kotlin
private val loginLauncher: ActivityResultLauncher<Intent> =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            omhAuthClient.getAccountFromIntent(result.data)
            navigateToLoggedIn()
        } catch (exception: OmhAuthException) {
            // There was an exception whilst logging in. In this case we'll show an error dialog.
            exception.printStackTrace()
            AlertDialog.Builder(this)
                .setTitle("An error has occurred.")
                .setMessage(exception.message)
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

// This will trigger the login flow.
val loginIntent = omhAuthClient.getLoginIntent()
loginLauncher.launch(loginIntent)
```

If the returned `result` contains the account, then you can continue to the logged in activity of
your application.

#### Sign out

To sign-out the SDK provides a straightforward functionality that returns an `OmhTask`. This is the
interface to interact with async functionalities and subscribe to the success or error results. To
cancel a running OMH task a cancellable token is provided after the `execute()` function is called.
This can be stored in the `CancellableCollector` class similar to the `CompositeDisposable` in
RxJava. The sign-out action will removes any and all relevant data to the user from the application
storage. Add the following code in the LoggedInActivity.kt in the `:auth-starter-sample` module.

```kotlin
val cancellable = omhAuthClient.signOut()
    .addOnSuccess { navigateToLogin() }
    .addOnFailure(::showErrorDialog)
    .execute()
cancellableCollector.addCancellable(cancellable)
```

*Note:* you can cancel all emitted cancellables within the collector running

```kotlin
cancellableCollector.clear()
```

#### Revoke token

The SDK also provides a way to revoke the access token provided to the application. This works
similar to the sign-out functionality but on top of clearing all local data, this also makes a
request to the auth provider to revoke the token from the server. Add the following code in the
LoggedInActivity.kt in the `:auth-starter-sample` module.

```kotlin
val cancellable = omhAuthClient.revokeToken()
    .addOnFailure(::showErrorDialog)
    .addOnSuccess { navigateToLogin() }
    .execute()
cancellableCollector.addCancellable(cancellable)
```

# Sample App

This repository includes a [auth-sample](/auth-sample) that demonstrates the functionality of the
OMH Auth Client Library. By cloning the repo and executing the app, you can explore the various
features offered by the library. However, if you prefer a step-by-step approach to learn the SDK
from scratch, we recommend following the detailed Getting Started guide provided in this repository.
The guide will walk you through the implementation process and help you integrate the OMH Auth
Client Library into your projects effectively.

**Note: Before running the sample application, make sure to follow the steps in Setup your Google
Cloud project for application with Google Services to configure your Google Cloud project.**

# Documentation

See example and check the full documentation and add custom implementation at
our [Wiki](https://github.com/openmobilehub/omh-auth/wiki).

Additionally for more information about the OMH Auth
functions, [Docs](https://openmobilehub.github.io/omh-auth).

# Contributing

Please contribute! We will gladly review any pull requests. Make sure to read
the [CONTRIBUTING](https://github.com/openmobilehub/omh-auth/blob/main/CONTRIBUTING.md) page first
though.

# License

Copyright 2023 Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0
