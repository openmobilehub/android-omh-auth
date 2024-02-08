# Facebook plugin

## Set up your Facebook application

To access Facebook APIs, generate a unique `app_id` and `client_token` for your app in the Meta for Developers. Add the `app_id` and `client_token` to your app's code and complete the required Meta for Developers setup steps:

1.  [Go to the Meta for Developer](https://developers.facebook.com/apps).
2.  Click on "Create App" to start creating a new Facebook application.
3.  Go to the App Settings -> Basic.
4.  Set your application **Package Names** (Use "com.openmobilehub.android.auth.sample" if you are following the starter-code)
5.  Set your application **Class Name** (Use "com.openmobilehub.android.auth.sample.base.MainActivity" if you are following the starter-code)
6.  Provide the Development and Release Key Hashes for Your App
    To ensure the authenticity of the interactions between your app and Facebook, you need to supply us with the Android key hash for your development environment. If your app has already been published, you should add your release key hash too.

    ### Generating a Development Key Hash

    You'll have a unique development key hash for each Android development environment.

    #### Mac OS

    You will need the Key and Certificate Management Tool (keytool) from the Java Development Kit. To generate a development key hash, open a terminal window and run the following command:

    ```bash
    keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
    ```

    #### Windows

    You will need the following:

    - Key and Certificate Management Tool (keytool) from the Java Development Kit
    - openssl-for-windows openssl library for Windows from the Google Code Archive

    To generate a development key hash, run the following command in a command prompt in the Java SDK folder:

    ```bash
    keytool -exportcert -alias androiddebugkey -keystore "C:\Users\USERNAME\android\debug.keystore" | "PATH_TO_OPENSSL_LIBRARY\bin\openssl" sha1 -binary | "PATH_TO_OPENSSL_LIBRARY\bin\openssl" base64
    ```

    This command will generate a 28-character key hash unique to your development environment. Copy and paste it into the field below. You will need to provide a development key hash for the development environment of each person who works on your app.

    ### Generating a Release Key Hash

    Android apps must be digitally signed with a release key before you can upload them to the store. To generate a hash of your release key, run the following command on Mac or Windows substituting your release key alias and the path to your keystore:

    ```bash
    keytool -exportcert -alias YOUR_RELEASE_KEY_ALIAS -keystore YOUR_RELEASE_KEY_PATH | openssl sha1 -binary | openssl base64
    ```

    This will generate a 28-character string that you should copy and paste into the field below. Also, see the Android documentation for signing your apps.

## Edit your resources and manifest

1. Open the **/app/manifest/AndroidManifest.xml** file.
2. Add meta-data elements to the application element for your app ID and client token:

   ```XML
   <application android:label="@string/app_name" ...>
     ...
     <meta-data android:name="com.facebook.sdk.ApplicationId" android:value="@string/facebook_app_id"/>
     <meta-data android:name="com.facebook.sdk.ClientToken" android:value="@string/facebook_client_token"/>
     ...
   </application>
   ```

3. Add an activity for Facebook, and an activity and intent filter for Chrome Custom Tabs inside your application element:

   ```XML
   <activity android:name="com.facebook.FacebookActivity"
       android:configChanges=
           "keyboard|keyboardHidden|screenLayout|screenSize|orientation"
       android:label="OMH" />
   <activity
       android:name="com.facebook.CustomTabActivity"
       android:exported="true">
       <intent-filter>
           <action android:name="android.intent.action.VIEW" />
           <category android:name="android.intent.category.DEFAULT" />
           <category android:name="android.intent.category.BROWSABLE" />
           <data android:scheme="@string/fb_login_protocol_scheme" />
       </intent-filter>
   </activity>
   ```

4. Add a uses-permission element to the manifest after the application element:

   ```XML
   <uses-permission android:name="android.permission.INTERNET"/>
   ```

5. (Optional) To opt out of the Advertising ID Permission, add a uses-permission element to the manifest after the application element:

   ```XML
   <uses-permission android:name="com.google.android.gms.permission.AD_ID" tools:node="remove"/>
   ```

   > Note: You may directly set the auto-logging of App Events to “true” or “false” by setting the AutoLogAppEventsEnabled flag in the AndroidManifest.xml file.

## Add the App ID and App Secret to your app

You should not check your App ID or App Secret into your version control system, so it is recommended storing it in the `local.properties` file, which is located in the root directory of your project. For more information about the `local.properties` file, see [Gradle properties](https://developer.android.com/studio/build#properties-files) [files](https://developer.android.com/studio/build#properties-files).

1. Open the `local.properties` in your project level directory, and then add the following code.

- Replace `YOUR_FACEBOOK_APP_ID` with your App ID: `FACEBOOK_APP_ID=YOUR_FACEBOOK_APP_ID`.
- Replace `YOUR_FACEBOOK_CLIENT_TOKEN` with your App Secret: `FACEBOOK_CLIENT_TOKEN=YOUR_FACEBOOK_CLIENT_TOKEN`.

2. Save the fileand [sync your project with Gradle](https://developer.android.com/studio/build#sync-files).

## Gradle configuration

To incorporate Facebook plugin into your project, you have to directly include the Facebook plugin as a dependency.

<!-- TODO -->

In the `SingletonModule.kt` file in the `:auth-starter-sample` module add the following code to provide the OMH Auth Client.

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
