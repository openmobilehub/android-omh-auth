package com.omh.android.auth.nongms.data

import com.omh.android.auth.nongms.BuildConfig
import com.omh.android.auth.nongms.data.login.GoogleAuthREST
import com.omh.android.auth.nongms.data.utils.retrofit.ApiResultCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

internal class GoogleRetrofitImpl {

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
        .addCallAdapterFactory(ApiResultCallAdapterFactory())
        .build()

    val googleAuthREST: GoogleAuthREST = retrofitClient.create(GoogleAuthREST::class.java)

    companion object {
        val instance by lazy {
            GoogleRetrofitImpl()
        }
    }
}
