This plugin simplifies the addition of Gradle dependencies, allowing you to effortlessly manage and include the necessary dependencies for seamless integration. The subsequent instructions will outline the necessary steps for including the OMH Core Plugin as a Gradle dependency.

# Configure the Android OMH Core plugin

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

In your `auth-starter-sample` module-level `build.gradle` file add the following code at the end of
the file.

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

In your "auth-starter-sample" module-level `build.gradle` file is required to configure
the `omhConfig`. The `omhConfig` definition is used to extend the existing Android Studio variants
in the core plugin. For more details `omhConfig`
see [Android OMH Core](https://github.com/openmobilehub/android-omh-core).

## Basic configuration

In this step, you will define the Android OMH Core Plugin bundles to generate multiple build
variants with specific suffixes as their names. For example, if your project has `release`
and `debug` variants with `singleBuild`, `gms`, and `nonGms` OMH bundles, the following build
variants will be generated:

- `releaseSingleBuild`, `releaseGms`, and `releaseNonGms`
- `debugSingleBuild`, `debugGms`, and `debugNonGms`

### Variant singleBuild

    - Define the `Service`. In this example is auth.
    - Define the `ServiceDetails`. In this example are `gmsService` and `nonGmsService`.
    - Define the dependency and the path. In this example
      are `com.openmobilehub.android.auth:plugin-google-gms:2.0.0-beta`
      and `com.openmobilehub.android.auth:plugin-google-non-gms:2.0.0-beta`.

**Note:** It's important to observe how a single build encompasses both GMS (Google Mobile Services)
and Non-GMS configurations.

### Variant gms

    - Define the `Service`. In this example is auth.
    - Define the `ServiceDetails` . In this example is `gmsService`.
    - Define the dependency and the path. In this example
      is `com.openmobilehub.android:auth-api-gms:1.0.1-beta"`.

**Note:** gms build covers only GMS (Google Mobile Services).

### Variant nongms

    - Define the `Service`. In this example is auth.
    - Define the `ServiceDetails` . In this example is `nonGmsService`.
    - Define the dependency and the path. In this example
      is `com.openmobilehub.android:auth-api-non-gms:1.0.1-beta`.

**Note:** nongms build covers only Non-GMS configurations.

3. Save and [sync Project with Gradle Files](https://developer.android.com/studio/build#sync-files).
4. Rebuild the project to ensure the availability of `BuildConfig.AUTH_GMS_PATH`
   and `BuildConfig.AUTH_NON_GMS_PATH` variables.
5. Now you can select a build variant. To change the build variant Android Studio uses, do one of
   the following:
   - Select "Build" > "Select Build Variant..." in the menu.
   - Select "View" > "Tool Windows" > "Build Variants" in the menu.
   - Click the "Build Variants" tab on the tool window bar.
6. You can select any of the 3 variants for the `:auth-starter-sample`:
   - "singleBuild" variant builds for GMS (Google Mobile Services) and Non-GMS devices without
     changes to the code.(Recommended)
   - "gms" variant builds for devices that has GMS (Google Mobile Services).
   - "nongms" variant builds for devices that doesn't have GMS (Google Mobile Services).
