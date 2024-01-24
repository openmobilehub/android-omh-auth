/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.auth.plugin.google.nongms.data.login

import com.openmobilehub.android.auth.plugin.google.nongms.data.login.models.AuthTokenResponse
import com.openmobilehub.android.auth.plugin.google.nongms.domain.models.ApiResult
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
    ): ApiResult<com.openmobilehub.android.auth.plugin.google.nongms.data.login.models.AuthTokenResponse>

    @POST("/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): ApiResult<com.openmobilehub.android.auth.plugin.google.nongms.data.login.models.AuthTokenResponse>

    @POST("/revoke")
    @FormUrlEncoded
    suspend fun revokeToken(
        @Field("token") token: String
    ): ApiResult<Unit>
}
