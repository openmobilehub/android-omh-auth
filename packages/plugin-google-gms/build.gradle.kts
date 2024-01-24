plugins {
    `android-base-lib`
}

setProperty("version", properties["authGoogleGmsVersion"])

android {
    namespace = "com.openmobilehub.android.auth.plugin.google.gms"
}

dependencies {
    api("com.openmobilehub.android.auth:core:1.0.1-beta")

    // KTX
    implementation(Libs.coreKtx)

    // Coroutines
    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)

    // Google Sign In
    implementation(Libs.googleSignIn)
    implementation(Libs.googleApiClientAndroid)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
    testImplementation(Libs.coroutineTesting)
}
