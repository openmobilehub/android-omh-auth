plugins {
    `android-base-lib`
}

android {
    namespace = "com.omh.android.auth.gms"
}

dependencies {
    api("com.openmobilehub.android:auth-api:1.0")

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