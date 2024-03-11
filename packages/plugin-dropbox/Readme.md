# Dropbox plugin

## Set up your Dropbox application

To access Dropbox APIs, generate a unique **App Key** for your app in the Dropbox Console. Add the *
*App Key** to your app's code and complete the required Dropbox Console setup steps:

1. [Go to the Dropbox Console](https://www.dropbox.com/developers/apps).
2. Click on "Create app" to start creating a new Dropbox application.

## Edit Your Resources and Manifest

Create strings for your Dropbox App Key. Also, add `AuthActivity` to your Android manifest.

1. Open your `/app/res/values/strings.xml` file.

2. Add a new string element with the name **db_login_protocol_scheme** and set the value to your App
   ID. For example, if your app ID is 1234, your code looks like the following:

```XML

<string name="db_login_protocol_scheme">db-1234</string>
```

3. Open the `/app/manifest/AndroidManifest.xml` file.

4. Configure an intent filter in the Android Manifest, using your redirect URI:

```XML

<activity android:name="com.dropbox.core.android.AuthActivity"
    android:configChanges="orientation|keyboard" android:exported="true"
    android:launchMode="singleTask">
    <intent-filter>
        <data android:scheme="@string/db_login_protocol_scheme" />

        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.BROWSABLE" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>

    <!-- Additional intent-filter required as a workaround for Apps using targetSdk=33 until the fix in the Dropbox app is available to all users. -->
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

5. Add Dropbox package to queries:

```XML

<queries>
    <package android:name="com.dropbox.android" />
</queries>
```

> You can find more information on how to integrate the Dropbox SDK by following
> the [official documentation](https://github.com/dropbox/dropbox-sdk-java).

## Add the App Key to your app

You should not check your App Key into your version control system, so it is recommended storing it
in the `local.properties` file, which is located in the root directory of your project. For more
information about the `local.properties` file,
see [Gradle properties](https://developer.android.com/studio/build#properties-files) [files](https://developer.android.com/studio/build#properties-files).

Open the `local.properties` in your project level directory, and do the following:

- Replace `YOUR_DROPBOX_APP_KEY` with your **APP ID**: `DROPBOX_APP_KEY=YOUR_DROPBOX_APP_KEY`.

## Gradle configuration

To incorporate Dropbox plugin into your project, you have to directly include the Dropbox plugin as
a dependency. In the `build.gradle.kts`, add the following implementation statement to
the `dependencies{}` section:

```groovy
implementation("com.openmobilehub.android.auth:plugin-dropbox:2.0.0-beta")
```

Save the file
and [sync your project with Gradle](https://developer.android.com/studio/build#sync-files).

## Provide the Dropbox OMH Auth Client

In the `SingletonModule.kt` file in the `:auth-starter-sample` module add the following code to
provide the Dropbox OMH Auth Client.

```kotlin
@Provides
fun providesDropboxAuthClient(@ApplicationContext context: Context): DropboxAuthClient {
    return DropboxAuthClient(
        scopes = arrayListOf("account_info.read"),
        context = context,
        appId = BuildConfig.DROPBOX_APP_KEY,
    )
}
```

> We'd recommend to store the client as a singleton with your preferred dependency injection library
> as this will be your only gateway to the OMH Auth SDK and it doesn't change in runtime at all.
