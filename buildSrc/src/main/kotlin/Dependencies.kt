object BuildPlugins {
    val android by lazy { "com.android.tools.build:gradle:${Versions.androidGradlePlugin}" }
    val kotlin by lazy { "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}" }
    val detekt by lazy { "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${Versions.detekt}" }
    val jacoco by lazy { "org.jacoco:org.jacoco.core:${Versions.jacoco}" }
}

object Libs {
    // KTX
    val coreKtx by lazy { "androidx.core:core-ktx:${Versions.coreKtx}" }
    val lifecycleKtx by lazy { "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleKtx}" }

    //Retrofit
    val retrofit by lazy { "com.squareup.retrofit2:retrofit:${Versions.retrofit}" }
    val retrofitJacksonConverter by lazy { "com.squareup.retrofit2:converter-jackson:${Versions.retrofit}" }
    val okHttp by lazy { "com.squareup.okhttp3:okhttp:${Versions.okhttp}" }
    val okHttpLoggingInterceptor by lazy { "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}" }

    // Custom Tabs
    val customTabs by lazy { "androidx.browser:browser:${Versions.customTabs}" }

    // Security
    val androidSecurity by lazy { "androidx.security:security-crypto:${Versions.androidSecurity}" }

    // Google Api Client
    val googleApiClient by lazy { "com.google.api-client:google-api-client:${Versions.googleApiClient}" }

    // Android
    val androidAppCompat by lazy { "androidx.appcompat:appcompat:${Versions.androidAppCompat}" }
    val material by lazy { "com.google.android.material:material:${Versions.material}" }

    // Testing
    val junit by lazy { "junit:junit:${Versions.junit}" }
    val androidJunit by lazy { "androidx.test.ext:junit:${Versions.androidJunit}" }
    val esspreso by lazy { "androidx.test.espresso:espresso-core:${Versions.espresso}" }
}
