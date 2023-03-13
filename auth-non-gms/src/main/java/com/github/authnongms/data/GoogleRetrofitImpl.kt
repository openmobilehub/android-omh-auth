package com.github.authnongms.data

import com.github.authnongms.data.login.GoogleAuthREST
import com.github.omhauthnongms.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class GoogleRetrofitImpl {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        )
        .build()

    private val retrofitClient = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BuildConfig.G_AUTH_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .build()

    val googleAuthREST: GoogleAuthREST = retrofitClient.create(GoogleAuthREST::class.java)

    companion object {
        val instance by lazy {
            GoogleRetrofitImpl()
        }
    }
}
