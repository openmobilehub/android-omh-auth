plugins {
    `android-base-lib`
}

setProperty("version", properties["authGoogleNongmsVersion"])

android {
    namespace = "com.openmobilehub.android.auth.plugin.google.nongms"

    viewBinding {
        enable = true
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField(
            type = "String",
            name = "G_AUTH_URL",
            value = getPropertyOrFail("googleAuthUrl")
        )
    }
}

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

dependencies {
    if(useLocalProjects) {
        api(project(":packages:core"))
    } else {
        api("com.openmobilehub.android.auth:core:2.0.0-beta")
    }

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
