import com.facebook.AccessToken
import com.facebook.FacebookException
import com.openmobilehub.android.auth.core.OmhCredentials
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class FacebookCredentials : OmhCredentials {
    override fun blockingRefreshToken(): String {
        ThreadUtils.checkForMainThread()
        return runBlocking {
            refreshAccessToken()?.token!!
        }
    }

    override val accessToken: String?
        get() = AccessToken.getCurrentAccessToken()?.token

    private suspend fun refreshAccessToken(): AccessToken? {
        return suspendCoroutine { continuation ->
            val callback = object : AccessToken.AccessTokenRefreshCallback {
                override fun OnTokenRefreshed(accessToken: AccessToken?) {
                    continuation.resume(accessToken)
                }

                override fun OnTokenRefreshFailed(exception: FacebookException?) {
                    continuation.resumeWithException(exception!!)
                }
            }

            AccessToken.refreshCurrentAccessTokenAsync(callback)
        }
    }
}
