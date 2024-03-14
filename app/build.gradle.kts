@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.net.URLEncoder

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.openmobilehub.android.omh-core")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

var googleGmsDependency = "com.openmobilehub.android.auth:plugin-google-gms:2.0.0-beta"
var googleNongmsDependency = "com.openmobilehub.android.auth:plugin-google-non-gms:2.0.0-beta"

var googleGmsPath = "com.openmobilehub.android.auth.plugin.google.gms.OmhAuthFactoryImpl"
var googleNongmsPath =
    "com.openmobilehub.android.auth.plugin.google.nongms.presentation.OmhAuthFactoryImpl"

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
    namespace = "com.openmobilehub.android.auth.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.openmobilehub.android.auth.sample"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setupSecrets(this)
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

    packaging {
        resources.excludes.add("META-INF/*")
        resources.excludes.add("**/LICENSE.txt")
        resources.excludes.add("**/README.txt")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }

    viewBinding {
        enable = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation("com.google.api-client:google-api-client-android:1.33.0")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-auth-base:18.0.8")

    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("com.squareup.picasso:picasso:2.8")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.openmobilehub.android.auth:plugin-facebook:2.0.0-beta")
    implementation("com.openmobilehub.android.auth:plugin-microsoft:2.0.0-beta")
    implementation("com.openmobilehub.android.auth:plugin-dropbox:2.0.0-beta")
}

fun setupSecrets(defaultConfig: ApplicationDefaultConfig) {
    val properties = gradleLocalProperties(rootDir)

    val facebookAppId = properties["FACEBOOK_APP_ID"] as String
    val facebookClientToken = properties["FACEBOOK_CLIENT_TOKEN"] as String
    val microsoftClientId = properties["MICROSOFT_CLIENT_ID"] as String
    val microsoftSignatureHash = properties["MICROSOFT_SIGNATURE_HASH"] as String
    val dropboxAppKey = properties["DROPBOX_APP_KEY"] as String

    defaultConfig.resValue("string", "facebook_app_id", facebookAppId)
    defaultConfig.resValue("string", "facebook_client_token", facebookClientToken)
    defaultConfig.resValue("string", "fb_login_protocol_scheme", "fb${facebookAppId}")
    defaultConfig.resValue("string", "microsoft_path", "/${microsoftSignatureHash}")
    defaultConfig.resValue("string", "dropbox_app_key", dropboxAppKey)
    defaultConfig.resValue("string", "db_login_protocol_scheme", "db-${dropboxAppKey}")

    file("./src/main/res/raw/ms_auth_config.json").writeText(
        """
{
  "client_id": "$microsoftClientId",
  "authorization_user_agent": "DEFAULT",
  "redirect_uri": "msauth://com.openmobilehub.android.auth.sample.base.DemoApp/${
            URLEncoder.encode(
                microsoftSignatureHash,
                "UTF-8"
            )
        }",
  "authorities": [
    {
      "type": "AAD",
      "audience": {
        "type": "AzureADandPersonalMicrosoftAccount",
        "tenant_id": "common"
      }
    }
  ],
  "account_mode": "SINGLE"
}
            """.trimIndent()
    )
}
