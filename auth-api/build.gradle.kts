plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.auth.api"
}

dependencies {

    implementation(Libs.androidAppCompat)
    implementation(Libs.material)
    implementation(Libs.reflection)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)
}