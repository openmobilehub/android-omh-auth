The interface OMH Credentials allows you to read the access token and refresh it in a blocking manner in case it's expired. It's designed to be used in a non main thread for your REST requests to refresh the expired token and retry the request if a newly minted token is returned.

# Accessing the token

To access the minted token after the user has logged in use the variable `accessToken`. This will return a `String `which will be null in case that no token had been stored previously. You can see a snippet below representing that:

```kotlin
val credentials = omhAuthClient.getCredentials()
// Read the access token
val token: String? = credentials.accessToken
if (token != null) {
   // Perform action with the token
} else {
   // No token data is stored on the device
}
```

# Refreshing the token

When your token expires, OMH Credentials exposes the function `blockingRefreshToken()` to try to refresh the token. This will return a `String` which will be null if the refresh wasn't successful. Take into account that this should never be called from the main thread as it's a blocking operation and will freeze that thread until a response is returned. This functionality is designed to be used in objects similar to Retrofit's interceptor or authenticator. On a successful refresh action, the newly minted token will be automatically stored locally and you'll be able to access it through the `accessToken` variable. You can see a snippet below representing that:

```kotlin
val credentials = omhAuthClient.getCredentials()
// A REST operation with the credentials.accessToken returned HTTP exception 401
val refreshedToken = credentials.blockingRefreshToken()
if (refreshedToken != null) {
   // retry REST operation
} else {
   // You should logout your user and request to login again.
}
```
