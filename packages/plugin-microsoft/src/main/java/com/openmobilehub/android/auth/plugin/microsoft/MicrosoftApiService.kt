package com.openmobilehub.android.auth.plugin.microsoft

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

internal object MicrosoftApiService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://graph.microsoft.com/v1.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(ApiService::class.java)
}

internal interface ApiService {
    @GET("me")
    fun getUserProfile(
        @Header("Authorization") token: String
    ): Call<User>
}

internal data class User(
    val givenName: String,
    val surname: String,
    val mail: String
)
