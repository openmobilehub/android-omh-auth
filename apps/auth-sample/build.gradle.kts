@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

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
    enableLocalProjects = useLocalProjects

    bundle("singleBuild") {
        auth {
            gmsService {
                if(!useLocalProjects) dependency = googleGmsDependency
                path = googleGmsPath
            }
            nonGmsService {
                if(!useLocalProjects) dependency = googleNongmsDependency
                path = googleNongmsPath
            }
        }
    }
    bundle("gms") {
        auth {
            gmsService {
                if(!useLocalProjects) dependency = googleGmsDependency
                path = googleGmsPath
            }
        }
    }
    bundle("nongms") {
        auth {
            nonGmsService {
                if(!useLocalProjects) dependency = googleNongmsDependency
                path = googleNongmsPath
            }
        }
    }
}

android {
    namespace = "com.openmobilehub.android.auth.sample"

    defaultConfig {
        versionCode = 1
        versionName = "1.0"

        val properties = gradleLocalProperties(rootDir)

        val facebookAppId = properties["FACEBOOK_APP_ID"] as String
        val facebookClientToken = properties["FACEBOOK_CLIENT_TOKEN"] as String

        resValue("string", "facebook_app_id", facebookAppId)
        resValue("string", "facebook_client_token", facebookClientToken)
        resValue("string", "fb_login_protocol_scheme", "fb${facebookAppId}")
    }

    signingConfigs {
        // It creates a signing config for release builds if the required properties are set.
        // The if statement is necessary to avoid errors when the packages are built on CI.
        // The alternative would be to pass all the environment variables for signing apk to the packages workflows.
        create("release") {
            val storeFileName = getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_FILE_NAME") as? String
            val storePassword = getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_STORE_PASSWORD") as? String
            val keyAlias = getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_KEY_ALIAS") as? String
            val keyPassword = getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_KEY_PASSWORD") as? String

            if (storeFileName != null && storePassword != null && keyAlias != null && keyPassword != null) {
                this.storeFile = file(storeFileName)
                this.storePassword = storePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // If the signing config is set, it will be used for release builds.
            if (signingConfigs["release"].storeFile != null) {
                signingConfig = signingConfigs.getByName("release")
            }
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
    implementation("com.squareup.picasso:picasso:2.8")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")

    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)

    // Use local implementation instead of dependencies
    if(useLocalProjects) {
        implementation(project(":packages:core"))
        implementation(project(":packages:plugin-google-gms"))
        implementation(project(":packages:plugin-google-non-gms"))
        implementation(project(":packages:plugin-facebook"))
    }
}

fun getValueFromEnvOrProperties(name: String): Any? {
    val localProperties = gradleLocalProperties(file("."))
    return System.getenv(name) ?: localProperties[name]
}
