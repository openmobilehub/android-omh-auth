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

object BuildPlugins {
    val android by lazy { "com.android.tools.build:gradle:${Versions.androidGradlePlugin}" }
    val kotlin by lazy { "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}" }
    val detekt by lazy { "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${Versions.detekt}" }
    val jacoco by lazy { "org.jacoco:org.jacoco.core:${Versions.jacoco}" }
}

object Libs {
    // Kotlin
    val reflection by lazy { "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}" }

    // KTX
    val coreKtx by lazy { "androidx.core:core-ktx:${Versions.coreKtx}" }
    val lifecycleKtx by lazy { "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleKtx}" }
    val viewModelKtx by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleKtx}" }
    val activityKtx by lazy { "androidx.activity:activity-ktx:${Versions.activityKtx}" }

    // Retrofit
    val retrofit by lazy { "com.squareup.retrofit2:retrofit:${Versions.retrofit}" }
    val retrofitJacksonConverter by lazy { "com.squareup.retrofit2:converter-jackson:${Versions.retrofit}" }
    val okHttp by lazy { "com.squareup.okhttp3:okhttp:${Versions.okhttp}" }
    val okHttpLoggingInterceptor by lazy { "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}" }

    // Coroutines
    val coroutinesCore by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}" }
    val coroutinesAndroid by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}" }

    // Custom Tabs
    val customTabs by lazy { "androidx.browser:browser:${Versions.customTabs}" }

    // Security
    val androidSecurity by lazy { "androidx.security:security-crypto:${Versions.androidSecurity}" }

    // Google Api Client
    val googleApiClient by lazy { "com.google.api-client:google-api-client:${Versions.googleApiClient}" }
    val googleApiClientAndroid by lazy { "com.google.api-client:google-api-client-android:${Versions.googleApiClient}" }

    // Android
    val androidAppCompat by lazy { "androidx.appcompat:appcompat:${Versions.androidAppCompat}" }
    val material by lazy { "com.google.android.material:material:${Versions.material}" }

    // Google Sign In
    val googleSignIn by lazy { "com.google.android.gms:play-services-auth:${Versions.googleSignIn}" }
    val googlePlayBase by lazy { "com.google.android.gms:play-services-base:${Versions.googlePlayBase}" }

    // Facebook SDK
    val facebookSdk by lazy { "com.facebook.android:facebook-android-sdk:${Versions.facebookSdk}" }

    // Microsoft SDK
    val microsoftMsal by lazy { "com.microsoft.identity.client:msal:${Versions.microsoftMsal}" }

    // Dropbox SDK
    val dropboxCoreSdk by lazy { "com.dropbox.core:dropbox-core-sdk:${Versions.dropboxSdk}" }
    val dropboxAndroidSdk by lazy { "com.dropbox.core:dropbox-android-sdk:${Versions.dropboxSdk}" }

    // Testing
    val junit by lazy { "junit:junit:${Versions.junit}" }
    val androidJunit by lazy { "androidx.test.ext:junit:${Versions.androidJunit}" }
    val esspreso by lazy { "androidx.test.espresso:espresso-core:${Versions.espresso}" }
    val mockk by lazy { "io.mockk:mockk:${Versions.mockk}" }
    val robolectric by lazy { "org.robolectric:robolectric:${Versions.robolectric}" }
    val testJson by lazy { "org.json:json:${Versions.testJson}" }
    val coroutineTesting by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}" }
}
