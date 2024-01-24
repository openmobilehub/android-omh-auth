plugins {
    `android-base-lib`
}

setProperty("version", properties["authCoreVersion"])

android {
    namespace = "com.openmobilehub.android.auth.core"
}

dependencies {

    implementation(Libs.androidAppCompat)
    implementation(Libs.material)
    implementation(Libs.reflection)

    implementation(Libs.googlePlayBase) // Provides a way to check for GMS availability

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)
}