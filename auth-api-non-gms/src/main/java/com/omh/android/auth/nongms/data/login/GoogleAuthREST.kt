package com.omh.android.auth.nongms.data.login

import com.omh.android.auth.nongms.data.login.models.AuthTokenResponse
import com.omh.android.auth.nongms.data.utils.retrofit.ApiResultCall
import com.omh.android.auth.nongms.domain.models.ApiResult
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

internal interface GoogleAuthREST {

    @POST("/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code_verifier") codeVerifier: String,
        @Field("grant_type") grantType: String = "authorization_code",
    ): ApiResult<AuthTokenResponse>

    @POST("/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): ApiResult<AuthTokenResponse>

    @POST("/revoke")
    @FormUrlEncoded
    suspend fun revokeToken(
        @Field("token") token: String
    ): ApiResult<Unit>
}
