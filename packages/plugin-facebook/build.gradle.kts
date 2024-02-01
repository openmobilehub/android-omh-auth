plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.auth.plugin.facebook"
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

    // Coroutines
    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)

    // Facebook SDK
    api(Libs.facebookSdk)

    // Encrypted Shared Prefs
    implementation(Libs.androidSecurity)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
    testImplementation(Libs.coroutineTesting)
}
