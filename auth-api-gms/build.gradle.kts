plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.auth.gms"
}

dependencies {
    api(project(":auth-api"))

    // KTX
    implementation(Libs.coreKtx)

    // Coroutines
    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)

    // Google Sign In
    implementation(Libs.googleSignIn)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
    testImplementation(Libs.coroutineTesting)
}