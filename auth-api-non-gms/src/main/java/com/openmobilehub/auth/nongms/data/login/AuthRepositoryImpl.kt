package com.openmobilehub.auth.nongms.data.login

import android.content.Context
import android.content.SharedPreferences
import com.openmobilehub.auth.nongms.data.GoogleRetrofitImpl
import com.openmobilehub.auth.nongms.data.login.datasource.AuthDataSource
import com.openmobilehub.auth.nongms.data.login.datasource.GoogleAuthDataSource
import com.openmobilehub.auth.nongms.data.login.models.AuthTokenResponse
import com.openmobilehub.auth.nongms.data.utils.getEncryptedSharedPrefs
import com.openmobilehub.auth.nongms.domain.auth.AuthRepository
import com.openmobilehub.auth.nongms.domain.models.ApiResult
import com.openmobilehub.auth.nongms.domain.models.OAuthTokens
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

internal class AuthRepositoryImpl(
    private val googleAuthDataSource: AuthDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override suspend fun requestTokens(
        clientId: String,
        authCode: String,
        redirectUri: String,
        codeVerifier: String
    ): ApiResult<OAuthTokens> = withContext(ioDispatcher) {
        val response: Response<AuthTokenResponse> = googleAuthDataSource.getToken(
            clientId = clientId,
            authCode = authCode,
            redirectUri = redirectUri,
            codeVerifier = codeVerifier
        )
        val body: AuthTokenResponse = response.body()
            ?: return@withContext ApiResult.Error("Null body")

        return@withContext if (response.isSuccessful) {
            googleAuthDataSource.storeToken(
                tokenType = AuthDataSource.ACCESS_TOKEN,
                token = checkNotNull(body.accessToken)
            )
            googleAuthDataSource.storeToken(
                tokenType = AuthDataSource.REFRESH_TOKEN,
                token = checkNotNull(body.refreshToken)
            )
            val data = OAuthTokens(
                accessToken = body.accessToken,
                refreshToken = checkNotNull(body.refreshToken),
                idToken = body.idToken
            )
            ApiResult.Success(data)
        } else {
            val exception = response.errorBody()?.string()
            response.errorBody()?.close()
            ApiResult.Error(exception.orEmpty())
        }
    }

    override fun buildLoginUrl(
        scopes: String,
        clientId: String,
        codeChallenge: String,
        redirectUri: String
    ): String {
        return googleAuthDataSource.buildLoginUrl(
            scopes = scopes,
            clientId = clientId,
            codeChallenge = codeChallenge,
            redirectUri = redirectUri
        ).toString()
    }

    override fun getAccessToken(): String? {
        return googleAuthDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
    }

    override suspend fun refreshAccessToken(
        clientId: String
    ): ApiResult<String> = withContext(ioDispatcher) {
        val response: Response<AuthTokenResponse> =
            googleAuthDataSource.refreshAccessToken(clientId)
        val body: AuthTokenResponse = response.body()
            ?: return@withContext ApiResult.Error("Null body")

        return@withContext if (response.isSuccessful) {
            googleAuthDataSource.storeToken(AuthDataSource.ACCESS_TOKEN, body.accessToken)
            ApiResult.Success(body.accessToken)
        } else {
            val exception = response.errorBody()?.string()
            response.errorBody()?.close()
            ApiResult.Error(exception.orEmpty())
        }
    }

    override suspend fun revokeToken(): ApiResult<Unit> = withContext(ioDispatcher) {
        val accessToken: String = googleAuthDataSource.getToken(AuthDataSource.ACCESS_TOKEN)
            ?: return@withContext ApiResult.Success(Unit)

        val response: Response<Nothing> = googleAuthDataSource.revokeToken(accessToken)

        return@withContext if (response.isSuccessful) {
            ApiResult.Success(Unit)
        } else {
            val exception = response.errorBody()?.string()
            response.errorBody()?.close()
            ApiResult.Error(exception.orEmpty())
        }
    }

    override fun clearData() {
        googleAuthDataSource.clearData()
    }

    companion object {

        private var authRepository: AuthRepository? = null

        fun getAuthRepository(
            context: Context,
            ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        ): AuthRepository {
            if (authRepository == null) {
                val authService: GoogleAuthREST = GoogleRetrofitImpl.instance.googleAuthREST
                val sharedPreferences: SharedPreferences = getEncryptedSharedPrefs(context)
                val googleAuthDataSource: AuthDataSource = GoogleAuthDataSource(
                    authService = authService,
                    sharedPreferences = sharedPreferences
                )
                authRepository = AuthRepositoryImpl(googleAuthDataSource, ioDispatcher)
            }

            return authRepository!!
        }
    }
}
