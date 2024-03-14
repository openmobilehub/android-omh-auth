plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.auth.plugin.facebook"
}

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

dependencies {
    if (useLocalProjects) {
        api(project(":packages:core"))
    } else {
        api(Libs.omhAuthCore)
    }

    // Facebook SDK
    api(Libs.facebookSdk)

    // Test dependencies
    testImplementation(Libs.junit)
    testImplementation(Libs.mockk)
    testImplementation(Libs.coroutineTesting)
    testImplementation(Libs.robolectric)
    testImplementation(Libs.testJson)
}
