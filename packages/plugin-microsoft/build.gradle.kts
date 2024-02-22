plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.auth.plugin.microsoft"
}

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

dependencies {
    if (useLocalProjects) {
        api(project(":packages:core"))
    } else {
        api("com.openmobilehub.android.auth:core:2.0.0-beta")
    }

    // Microsoft SDK
    api(Libs.microsoftMsal) {
        // TODO: Investigate this a little bit more
        exclude(group = "com.microsoft.device.display")
    }

    api("com.android.volley:volley:1.2.1")

    // Test dependencies
    testImplementation(Libs.junit)
    testImplementation(Libs.mockk)
    testImplementation(Libs.coroutineTesting)
    testImplementation(Libs.robolectric)
    testImplementation(Libs.testJson)
}
