plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.auth.core"
}

dependencies {

    implementation(Libs.androidAppCompat)
    implementation(Libs.material)
    implementation(Libs.reflection)

    implementation(Libs.googlePlayBase) // Provides a way to check for GMS availability
    implementation(Libs.androidSecurity)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)
}