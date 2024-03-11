# Microsoft plugin

## Set up your Microsoft application

To access Microsoft APIs, generate a unique **Client ID** and **Keystore Hash** for your app in the Microsoft Azure. Add the **Application ID** and **Keystore Hash** to your app's code and complete the required Microsoft Azure setup steps:

1.  [Go to the Microsoft Azure](https://portal.azure.com/#view/Microsoft_AAD_RegisteredApps/ApplicationsListBlade).
2.  Click on "New registration" to start creating a new Microsoft Azure application. Make sure that the "accounts in any organizational directory (Any Microsoft Entra ID tenant - Multitenant) and personal Microsoft accounts (e.g. Skype, Xbox)" option is chosen under **Supported account types**.
3.  Once created the application, go to the Authentication and add a new Android platform.
4.  Set your app package name (Use "com.openmobilehub.android.auth.sample.base.DemoApp" if you are following the starter-code).
5.  Generate and set your Signature Hash:

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

    ### Setting the Signature Hash

    1. Once you generated the debug or the release key hash, add it under **Signature Hash** input and save your changes.

    2. Next, click on **View** and copy the **MSAL Configuration** contents into a new file under `/app/res/raw/ms_auth_config.json`.

    3. In the newly created JSON **MSAL Configuration**, add a new key: `account_mode` and set it's value to: `"SINGLE"`.

## Edit Your Resources and Manifest

1. Configure an intent filter in the Android Manifest, using your redirect URI:

```XML
<activity
  android:name="com.microsoft.identity.client.BrowserTabActivity"
  android:exported="true">
  <intent-filter>
      <action android:name="android.intent.action.VIEW" />

      <category android:name="android.intent.category.DEFAULT" />
      <category android:name="android.intent.category.BROWSABLE" />

      <data
        android:host="<YOUR_PACKAGE_NAME>"
          android:path="/<YOUR_SIGNATURE_HASH>"
          android:scheme="msauth" />
  </intent-filter>
</activity>
```

2. Add a uses-permission element to the manifest after the application element:

```XML
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

> You can find more information on how to integrate the Microsoft SDK by following the [official documentation](https://github.com/AzureAD/microsoft-authentication-library-for-android).

## Gradle configuration

To incorporate Microsoft plugin into your project, you have to directly include the Microsoft plugin as a dependency. In the `build.gradle.kts`, add the following implementation statement to the `dependencies{}` section:

```groovy
implementation("com.openmobilehub.android.auth:plugin-microsoft:2.0.0-beta")
```

Save the file and [sync your project with Gradle](https://developer.android.com/studio/build#sync-files).

## Provide the Microsoft OMH Auth Client

In the `SingletonModule.kt` file in the `:auth-starter-sample` module add the following code to provide the Microsoft OMH Auth Client.

```kotlin
@Provides
fun providesMicrosoftAuthClient(@ApplicationContext context: Context): MicrosoftAuthClient {
    return MicrosoftAuthClient(
        configFileResourceId = R.raw.ms_auth_config,
        context = context,
        scopes = arrayListOf("User.Read"),
    )
}
```

> We'd recommend to store the client as a singleton with your preferred dependency injection library as this will be your only gateway to the OMH Auth SDK and it doesn't change in runtime at all.
