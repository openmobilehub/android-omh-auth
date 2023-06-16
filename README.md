[![GitHub license](https://img.shields.io/github/license/openmobilehub/omh-auth)](https://github.com/openmobilehub/omh-auth/blob/main/LICENSE)
![GitHub contributors](https://img.shields.io/github/contributors/openmobilehub/omh-auth)
[![API](https://img.shields.io/badge/API-21%2B-green.svg?style=flat)](https://developer.android.com/studio/releases/platforms#6.0)

# OMH Auth

The solution to seamlessly integrating auth services across GMS and non-GMS devices. Our open-source
Android SDK tackles the problem of device compatibility, allowing developers to use the same SDK
without worrying about specific device requirements.

With OMH Auth SDK, you can effortlessly incorporate the Google Auth implementation into your
applications, regardless of whether the device has Google Mobile Services or not.
Our SDK handles the complexities behind the scenes, providing a unified interface and common
components for consistent map functionality.

# Provider Implementations

We also believe in the power of community collaboration. That's why OMH Auth SDK is open-source,
inviting contributions and supporting plugins from other auth providers. Together, we can expand the
capabilities of the SDK and enhance the range of supported auth services.

# Sample App

This section describes how to setup an Android Studio project to use the OMH Auth SDK for Android.
For greater ease, a base code will be used within the repository.

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
   git clone --branch guide/starter-code https://github.com/openmobilehub/omh-auth.git
   ```

- Press "Enter" to create your local clone.

You can always check what the final result should be in the module `sample-app` in the main or
develop branches.

## Set up your Google Cloud project for applications with Google Services (Google Auth)

Complete the required Cloud Console setup steps by clicking through the following tabs:

### Steps

1. In the Google Cloud Console, on the project selector page, click **Create Project** to begin
   creating a new Cloud
   project, [Go to the project selector page](https://console.cloud.google.com/projectselector2/home/dashboard?utm_source=Docs_ProjectSelector&_gl=1*1ylhfe0*_ga*MTUwMDIzODY1Ni4xNjc1OTYyMDgw*_ga_NRWSTWS78N*MTY4MjA4ODIyNS44NS4xLjE2ODIwODgyMzcuMC4wLjA.)
   .
2. Go to the **Credentials**
   page, [Go to the Credentials page](https://console.cloud.google.com/apis/credentials)
   .
3. On the **Credentials** page, click **Create credentials > OAuth Client ID**. In the
   Application Type option select Android. Set the package name and you SHA-1 fingerprint and
   you're all set.

## Add the Client ID to your app

You should not check your Client ID into your version control system, so it is recommended
storing it in the `local.properties` file, which is located in the root directory of your project.
For more information about the `local.properties` file,
see [Gradle properties](https://developer.android.com/studio/build#properties-files)
[files](https://developer.android.com/studio/build#properties-files).

1. Open the `local.properties` in your project level directory, and then add the following code.
   Replace `YOUR_CLIENT_ID` with your API key.
   `CLIENT_ID=YOUR_CLIENT_ID`
2. Save the file.
3. To read the value from the `local.properties` you can
   use [Secrets Gradle plugin for Android](https://github.com/google/secrets-gradle-plugin). To
   install the plugin and store your API key:
    - Open your project level `build.gradle` file and add the following code:
   ```groovy
   buildscript {
       dependencies {
           classpath "com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1"
       }
   }
   ```

    - Open your application level `build.gradle` file and add the following code to
      the `plugins` element.

   ```groovy
   plugins {
      id 'com.google.android.libraries.mapsplatform.secrets-gradle-plugin'
   }
   ```

    - Save the file
      and [sync your project with Gradle](https://developer.android.com/studio/build#sync-files).

## Gradle dependencies

To integrate the OMH Auth SDK in your project is required to add some Gradle dependencies.

## Gradle configuration

To integrate the OMH Auth in your project is required to add some Gradle dependencies.

### Add OMH Core plugin

To add the core plugin dependency in a new project, follow the next steps:

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

To use the core plugin is required some minimum configuration, for more details
see [OMH Core Docs](https://github.com/openmobilehub/omh-core/tree/release/1.0).

1. In your "auth-starter-sample" module-level `build.gradle` file under the `buildFeatures` element
   add `buildConfig = true`. For more information
   see [BuildFeatures](https://developer.android.com/reference/tools/gradle-api/7.0/com/android/build/api/dsl/BuildFeatures)

   ```
   android {
      ...
      buildFeatures {
         ...
         buildConfig = true
      }
   }
   ```

2. In your "auth-starter-sample" module-level `build.gradle` file is required to configure
   the `omhConfig`. The `omhConfig` definition is used to extend the existing Android Studio
   variants in the core plugin.
   For more details `omhConfig`
   see [OMH Core](https://github.com/openmobilehub/omh-core/tree/release/1.0).

   #### Basic configuration
   Define the `Bundle` that represents the build variants names. In this example are `singleBuild`
   , `gms` and `nongms`.

   ##### Variant singleBuild
    - Define the `Service`. In this example is auth.
    - Define the `ServiceDetails`. In this example are `gmsService` and `nonGmsService`.
    - Define the dependency and the path. In this example
      are `com.openmobilehub.android:auth-api-gms:1.0"`
      and `com.openmobilehub.android:auth-api-non-gms:1.0`.
      **Note:** It's important to observe how a single build encompasses both GMS (Google Mobile
      Services) and Non-GMS configurations.

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

   In your "auth-starter-sample" module-level `build.gradle` file add the following code at the end
   of the file.

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

## Getting Started

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
    // A previously logged in user was found, redirect them to the appropriate screen.
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
            // navigate to logged in screen
        } catch (exception: OmhAuthException) {
            // There was an exception whilst logging in. In this case we'll show an error dialog.
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
    .addOnSuccess { /* navigate back to the login screen */ }
    .addOnFailure { /* show an error dialog */ }
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
    .addOnSuccess { /* navigate back to the login screen */ }
    .addOnFailure { /* show an error dialog */ }
    .execute()
cancellableCollector.addCancellable(cancellable)
```

## Documentation

See example and check the full documentation and add custom implementation at
our [Wiki](https://github.com/openmobilehub/omh-auth/wiki).

Additionally for more information about the OMH Auth
functions, [Docs](https://openmobilehub.github.io/omh-auth).

## Contributing

We'd be glad if you decide to contribute to this project.

All pull request is welcome, just make sure that every work is linked to an issue on this repository
so everyone can track it. For more information
check [CONTRIBUTING](https://github.com/openmobilehub/omh-auth/blob/main/CONTRIBUTING.md)

## License

Copyright 2023 Futurewei, Inc.
Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.
See the NOTICE file distributed with this work for additional information regarding copyright
ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License. You may obtain a copy of the
License at https://www.apache.org/licenses/LICENSE-2.0
