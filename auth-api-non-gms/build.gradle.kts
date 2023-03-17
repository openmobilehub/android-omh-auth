plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.auth.nongms"

    viewBinding {
        enable = true
    }

    defaultConfig {
        buildConfigField(
            type = "String",
            name = "G_AUTH_URL",
            value = getPropertyOrFail("googleAuthUrl")
        )
    }
}

dependencies {
    api(project(":auth-api"))

    // KTX
    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleKtx)
    implementation(Libs.viewModelKtx)
    implementation(Libs.activityKtx)


    // Retrofit setup
    implementation(Libs.retrofit)
    implementation(Libs.retrofitJacksonConverter)
    implementation(Libs.okHttp)
    implementation(Libs.okHttpLoggingInterceptor)

    // Coroutines
    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)

    // Custom tabs
    implementation(Libs.customTabs)

    // Encrypted Shared Prefs and ID token resolution
    implementation(Libs.androidSecurity)
    implementation(Libs.googleApiClient) {
        exclude("org.apache.httpcomponents")
    }

    implementation(Libs.androidAppCompat)
    implementation(Libs.material)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
    testImplementation(Libs.coroutineTesting)
}