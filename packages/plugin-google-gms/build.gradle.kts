plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.auth.plugin.google.gms"
}

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

dependencies {
    if (useLocalProjects) {
        api(project(":packages:core"))
    } else {
        api(Libs.omhAuthCore)
    }

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
