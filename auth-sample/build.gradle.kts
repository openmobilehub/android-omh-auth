import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    `android-application`
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.44" apply true
}

android {
    namespace = "com.openmobilehub.auth.sample"

    defaultConfig {
        buildConfigField(
            type = "String",
            name = "CLIENT_ID",
            value = gradleLocalProperties(rootDir)["clientId"].toString()
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "google_services"
    productFlavors {
        create("ngms") {
            dimension = "google_services"
        }
        create("gms") {
            dimension = "google_services"
        }
    }

    viewBinding {
        enable = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    kapt {
        correctErrorTypes = true
    }
}

val gmsImplementation by configurations
val ngmsImplementation by configurations
dependencies {
    ngmsImplementation(project(":auth-api-non-gms"))
    //    implementation("com.openmobilehub:auth-non-gms:1.0-SNAPSHOT")
    gmsImplementation(project(":auth-api-gms"))

    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleKtx)
    implementation(Libs.androidAppCompat)
    implementation(Libs.material)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")

    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)
}