# Google GMS and non-GMS plugin

## Set up your Google Cloud project for applications with Google Services (Google Auth)

To access Google APIs, generate a unique client_id for your app in the Google API Console. Add the client_id to your app's code and complete the required Cloud Console setup steps:

### Steps

1. [Go to the Google Cloud Console and open the project selector page](https://console.cloud.google.com/projectselector2).
2. Click on "Create Project" to start creating a new Cloud project.
3. [Go to the Credentials page](https://console.cloud.google.com/apis/credentials).
4. On the Credentials page, click on "Create credentials" and choose "OAuth Client ID".
5. In the "Application Type" option, select "Android".
6. Set your application package name (Use "com.openmobilehub.android.auth.sample" if you are following the starter-code)
7. Update the debug/release SHA-1 certificate fingerprint for Android's Client ID.
   Note: The debug build is automatically signed with the debug keystore. Obtain the certificate fingerprint from it by following the guidelines in the official Google Developers documentation: ["Using keytool on the certificate"](https://developers.google.com/android/guides/client-auth#using_keytool_on_the_certificate).
8. In the [OAuth consent screen](https://console.cloud.google.com/apis/credentials/consent) add the test users that you will be using for QA and development. Without this step you won't be able to access the application while it's in testing mode.
9. You're all set!

## Add the Client ID to your app

You should not check your Client ID into your version control system, so it is recommended
storing it in the `local.properties` file, which is located in the root directory of your project. For more information about the `local.properties` file, see [Gradle properties](https://developer.android.com/studio/build#properties-files) [files](https://developer.android.com/studio/build#properties-files).

1. Open the `local.properties` in your project level directory, and then add the following code. Replace `YOUR_GOOGLE_CLIENT_ID` with your API key. `GOOGLE_CLIENT_ID=YOUR_GOOGLE_CLIENT_ID`
2. Save the fileand [sync your project with Gradle](https://developer.android.com/studio/build#sync-files).

## Gradle configuration

To incorporate the Google GMS and non-GMS plugin into your project, you have two options: utilize the Android OMH Core Plugin or directly include the Android OMH Client libraries dependencies. This plugin simplifies the addition of Gradle dependencies, allowing you to effortlessly manage and include the necessary dependencies for seamless integration.

### Add Android OMH Core plugin

The subsequent instructions will outline the necessary steps for including the OMH Core Plugin as a Gradle dependency.

1. In your "auth-starter-sample" module-level `build.gradle` under the `plugins` element add the plugin id.

   ```
   plugins {
      ...
      id("com.openmobilehub.android.omh-core")
   }
   ```

2. Save the file and [sync Project with Gradle Files](https://developer.android.com/studio/build#sync-files).

### Configure the Android OMH Core plugin

In your `auth-starter-sample` module-level `build.gradle` file add the following code at the end of the file.

```
omhConfig {
   bundle("singleBuild") {
      auth {
         gmsService {
            dependency = "com.openmobilehub.android.auth:plugin-google-gms:2.0.0-beta"
         }
         nonGmsService {
            dependency = "com.openmobilehub.android.auth:plugin-google-non-gms:2.0.0-beta"
         }
      }
   }
   bundle("gms") {
      auth {
         gmsService {
            dependency = "com.openmobilehub.android.auth:plugin-google-gms:2.0.0-beta"
         }
      }
   }
   bundle("nongms") {
      auth {
         nonGmsService {
            dependency = "com.openmobilehub.android.auth:plugin-google-non-gms:2.0.0-beta"
         }
      }
   }
}
```

_**NOTE: This section covers concepts about the core plugin**_

In your "auth-starter-sample" module-level `build.gradle` file is required to configure the `omhConfig`. The `omhConfig` definition is used to extend the existing Android Studio variants in the core plugin. For more details `omhConfig` see [Android OMH Core](https://github.com/openmobilehub/android-omh-core).

#### Basic configuration

In this step, you will define the Android OMH Core Plugin bundles to generate multiple build variants with specific suffixes as their names. For example, if your project has `release` and `debug` variants with `singleBuild`, `gms`, and `nonGms` OMH bundles, the following build variants will be generated:

- `releaseSingleBuild`, `releaseGms`, and `releaseNonGms`
- `debugSingleBuild`, `debugGms`, and `debugNonGms`

##### Variant singleBuild

    - Define the `Service`. In this example is auth.
    - Define the `ServiceDetails`. In this example are `gmsService` and `nonGmsService`.
    - Define the dependency and the path. In this example
      are `com.openmobilehub.android.auth:plugin-google-gms:2.0.0-beta`
      and `com.openmobilehub.android.auth:plugin-google-non-gms:2.0.0-beta`.

**Note:** It's important to observe how a single build encompasses both GMS (Google Mobile Services) and Non-GMS configurations.

##### Variant gms

    - Define the `Service`. In this example is auth.
    - Define the `ServiceDetails` . In this example is `gmsService`.
    - Define the dependency and the path. In this example
      is `com.openmobilehub.android:auth-api-gms:1.0.1-beta"`.

**Note:** gms build covers only GMS (Google Mobile Services).

##### Variant nongms

    - Define the `Service`. In this example is auth.
    - Define the `ServiceDetails` . In this example is `nonGmsService`.
    - Define the dependency and the path. In this example
      is `com.openmobilehub.android:auth-api-non-gms:1.0.1-beta`.

**Note:** nongms build covers only Non-GMS configurations.

3. Save and [sync Project with Gradle Files](https://developer.android.com/studio/build#sync-files).
4. Rebuild the project to ensure the availability of `BuildConfig.AUTH_GMS_PATH` and `BuildConfig.AUTH_NON_GMS_PATH` variables.
5. Now you can select a build variant. To change the build variant Android Studio uses, do one of the following:
   - Select "Build" > "Select Build Variant..." in the menu.
   - Select "View" > "Tool Windows" > "Build Variants" in the menu.
   - Click the "Build Variants" tab on the tool window bar.
6. You can select any of the 3 variants for the `:auth-starter-sample`:
   - "singleBuild" variant builds for GMS (Google Mobile Services) and Non-GMS devices without changes to the code.(Recommended)
   - "gms" variant builds for devices that has GMS (Google Mobile Services).
   - "nongms" variant builds for devices that doesn't have GMS (Google Mobile Services).
7. In the `SingletonModule.kt` file in the `:auth-starter-sample` module add the following code to provide the OMH Auth Client.

   ```kotlin
   val omhAuthProvider = OmhAuthProvider.Builder()
       .addNonGmsPath(BuildConfig.AUTH_NON_GMS_PATH)
       .addGmsPath(BuildConfig.AUTH_GMS_PATH)
       .build()

   return omhAuthProvider.provideAuthClient(
       scopes = listOf("openid", "email", "profile"),
       clientId = BuildConfig.GOOGLE_CLIENT_ID,
       context = context
   )
   ```

_Note_: we'd recommend to store the client as a singleton with your preferred dependency injection library as this will be your only gateway to the OMH Auth SDK and it doesn't change in runtime at all.
