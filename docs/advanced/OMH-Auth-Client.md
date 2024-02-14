The Android OMH Auth Client acts as the facade of the library serving as the only interface that you'll ever interact with. It provides you with a host of functionalities that range from logging in to logging out.

# Obtaining the client

To obtain the client in whatever configuration you're running your application you need to use the `OmhAuthProvider`. Depending on if you're using the OMH Core plugin or not, there are two ways of going about it:

## With the core plugin

In case you're using our OMH Core plugin, then configuring the provider is very straightforward, just paste the following code into your project:

```kotlin
val omhAuthProvider = OmhAuthProvider.Builder()
  .addNonGmsPath(BuildConfig.AUTH_GMS_PATH)
  .addGmsPath(BuildConfig.AUTH_NON_GMS_PATH)
  .build()
```

The BuildConfig fields will be generated automatically once you finish setting up the core plugin.

## Without the core plugin

1. Without the core plugin, we would recommend creating distinct flavors for your GMS and non GMS versions. If you are using a custom implementation of the Android OMH Auth SDK, save the reflection path of the `OmhAuthFactory` in a variable.
2. Create sourceSets for your classes that will provide the `OmhAuthProvider`, be it static methods or dependency injection modules.
3. For each source set, configure the `Builder` to represent the configuration expected in which the application will run. For example, the configuration for the GMS version would look like this:

```kotlin
val omhAuthProvider = OmhAuthProvider.Builder()
  .addGmsPath(BuildConfig.AUTH_NON_GMS_PATH)
  .build()
```

Once you have the provider setup, you can obtain the Auth client with the following function:

```kotlin
return omhAuthProvider.provideAuthClient(
   scopes = listOf("openid", "email", "profile"),
   clientId = BuildConfig.GOOGLE_CLIENT_ID,
   context = context
)
```

The scopes vary according to your needs, but do take into account that dynamic requests for more scopes isn't possible with the Android OMH Auth SDK. _Note that the `GOOGLE_CLIENT_ID` should be of the Android type if you're trying to login with the Google Provider._

We'd recommend using the client as a singleton instance as once instantiated, the configuration won't change in runtime.

# Using the client

As explained in
the [Getting Started Guide](/README.md), to perform the login you need to access the `Intent` from the Android OMH Auth Client using the `getLoginIntent()` function and launch it expecting a result. To read the result and determine if the login action was successful and obtain the [user profile](#obtaining-the-users-profile), use the `omhAuthClient.handleLoginIntentResponse(result.data)` function and wrap it into a try/catch statement. An example of this can be seen in the following gist:

```kotlin
private val loginLauncher: ActivityResultLauncher<Intent> =
  registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    try {
      omhAuthClient.handleLoginIntentResponse(result.data)
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

# Logging out

To logout using the Android OMH Auth SDK the client provides the `omhAuthClient.signOut()` function. This returns an OmhTask which represents an async functionality. If you wish to read more about this interface, you can do so [here](/docs/advanced/OMH-Task.md). Take into account that this functionality only clears the local user data stored in the application but doesn't revoke the tokens emitted. Here's a snippet on how you could use the sign out functionality:

```kotlin
val cancellable = omhAuthClient.signOut()
  .addOnSuccess { /* navigate back to the login screen */ }
  .addOnFailure { /* show an error dialog */ }
  .execute()
cancellableCollector.addCancellable(cancellable)
```

# Revoking a token

The revoke token serves a similar functionality as the logout, but on top of clearing the local user data this also makes a REST call to the provider to revoke the tokens emitted for the user.

```kotlin
val cancellable = omhAuthClient.revokeToken()
  .addOnSuccess { /* navigate back to the login screen */ }
  .addOnFailure { /* show an error dialog */ }
  .execute()
cancellableCollector.addCancellable(cancellable)
```

# Obtaining the user's profile

To obtain the user profile use the function `omhAuthClient.getUser()`. This returns an `OmhTask` which represents an async functionality. A successful fetch will return an object of the class `OmhUserProfile`. The content of the user profile has the most basic information that's bundled in the ID token returned and the ID token itself in case you wish to validate it yourself or send it to your backend servers. Here's the `OmhUserProfile` for more detail:

```kotlin
// OmhUserProfile
class OmhUserProfile(
  val name: String?,
  val surname: String?,
  val email: String?,
  val profileImage: String?,
  val idToken: String?
)

omhAuthClient.getUser()
  .addOnSuccess()
  .addOnFailure()
  .execute()
```

# Working with the credentials

If you wish to obtain the given credentials you can use the function `getCredentials()`. Take into mind that this will return an `Any` object as this credentials may vary depending on the implementation. For the GMS version of the Android OMH Auth SDK a `GoogleAccountCredential` will be returned. You can read more about it [here](https://cloud.google.com/java/docs/reference/google-api-client/latest/com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential). In the case of the non GMS version, an interface is provided called `OmhCredentials`. You can read more about it [here](/docs/advanced/OMH-Credentials.md)