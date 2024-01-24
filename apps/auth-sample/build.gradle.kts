@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    `android-application`
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.44" apply true
    id("com.openmobilehub.android.omh-core")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

var googleGmsDependency = "com.openmobilehub.android.auth:plugin-google-gms:2.0.0-beta"
var googleNongmsDependency = "com.openmobilehub.android.auth:plugin-google-non-gms:2.0.0-beta"

var googleGmsPath = "com.openmobilehub.android.auth.plugin.google.gms.OmhAuthFactoryImpl"
var googleNongmsPath = "com.openmobilehub.android.auth.plugin.google.nongms.presentation.OmhAuthFactoryImpl"

omhConfig {
    bundle("singleBuild") {
        auth {
            gmsService {
                dependency = googleGmsDependency
                path = googleGmsPath
            }
            nonGmsService {
                dependency = googleNongmsDependency
                path = googleNongmsPath
            }
        }
    }
    bundle("gms") {
        auth {
            gmsService {
                dependency = googleGmsDependency
                path = googleGmsPath
            }
        }
    }
    bundle("nongms") {
        auth {
            nonGmsService {
                dependency = googleNongmsDependency
                path = googleNongmsPath
            }
        }
    }
}

android {
    signingConfigs {
        create("release") {
            val localProperties = gradleLocalProperties(rootDir)
            storeFile = file(localProperties["keypath"].toString())
            storePassword = localProperties["keypass"].toString()
            keyAlias = localProperties["keyalias"].toString()
            keyPassword = localProperties["keypassword"].toString()
        }
    }
    namespace = "com.openmobilehub.android.auth.sample"

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
dependencies {
    implementation(Libs.googleApiClientAndroid)

    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleKtx)
    implementation(Libs.androidAppCompat)
    implementation(Libs.material)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-auth-base:18.0.8")

    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")

    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)
}
