---
title: Omh Credentials
layout: default
parent: Advanced features
---

The interface OMH Credentials allows you to read the access token and refresh it in case it's
expired. It's designed to be used to refresh the expired token and retry the request if a newly
minted token is returned.

# Accessing the token

To access the minted token after the user has logged in use the variable `accessToken`. This will
return a `String `which will be null in case that no token had been stored previously. You can see a
snippet below representing that:

```kotlin
val credentials = omhAuthClient.getCredentials()
// Read the access token
val token: String = credentials.accessToken
if (token != null) {
    // Perform action with the token
} else {
    // No token data is stored on the device
}
```

# Refreshing the token

When your token expires, OMH Credentials exposes the function `refreshAccessToken` to try to refresh
the token. This method returns an `OmhTask` which is the interface to interact with async
functionalities and subscribe to the success or error results. A successful fetch will return a
new `accessToken`. This functionality is designed to be used in objects similar to Retrofit's
interceptor or authenticator. On a successful refresh action, the newly minted token will be
automatically stored locally and you'll be able to access it through the `accessToken` variable. You
can see a snippet below representing that:

```kotlin
authClient.getCredentials().refreshAccessToken()
    .addOnSuccess { token ->
        // Perform action with the new token
    }.addOnFailure {
        // Show an error dialog
    }
    .execute()
```
