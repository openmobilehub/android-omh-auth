plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.auth.plugin.dropbox"
}

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

dependencies {
    if (useLocalProjects) {
        api(project(":packages:core"))
    } else {
        api(Libs.omhAuthCore)
    }

    // Dropbox SDK
    api(Libs.dropboxCoreSdk)
    api(Libs.dropboxAndroidSdk)

    // Coroutines
    implementation(Libs.coroutinesCore)

    implementation(Libs.androidSecurity)

    // Test dependencies
    testImplementation(Libs.junit)
    testImplementation(Libs.mockk)
    testImplementation(Libs.coroutineTesting)
    testImplementation(Libs.robolectric)
    testImplementation(Libs.testJson)
}
