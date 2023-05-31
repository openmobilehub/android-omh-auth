import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    `android-application`
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.44" apply true
    id("com.openmobilehub.android.omh-core")
}

omhConfig {
    bundle("singleBuild") {
        auth {
            gmsService {
                dependency = "com.openmobilehub.android:auth-api-gms:1.0"
            }
            nonGmsService {
                dependency = "com.openmobilehub.android:auth-api-non-gms:1.0"
            }
        }
    }
    bundle("gms") {
        auth {
            gmsService {
                dependency = "com.openmobilehub.android:auth-api-gms:1.0"
            }
        }
    }
    bundle("nongms") {
        auth {
            nonGmsService {
                dependency = "com.openmobilehub.android:auth-api-non-gms:1.0"
            }
        }
    }
}

android {
    namespace = "com.omh.android.auth.sample"

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

    // Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")

    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)
}