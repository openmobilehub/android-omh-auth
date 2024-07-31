@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.net.URLEncoder

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

plugins {
    `android-application`
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.44" apply true
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.openmobilehub.android.auth.sample"

    defaultConfig {
        applicationId = "com.openmobilehub.android.auth.sample"
        versionCode = 1
        versionName = "1.0"

        val facebookAppId = getValueFromProperties("FACEBOOK_APP_ID")
        val facebookClientToken = getValueFromProperties("FACEBOOK_CLIENT_TOKEN")
        val microsoftClientId = getValueFromProperties("MICROSOFT_CLIENT_ID")
        val microsoftSignatureHash = getValueFromProperties("MICROSOFT_SIGNATURE_HASH")
        val dropboxAppKey = getValueFromProperties("DROPBOX_APP_KEY")

        resValue("string", "facebook_app_id", facebookAppId)
        resValue("string", "facebook_client_token", facebookClientToken)
        resValue("string", "fb_login_protocol_scheme", "fb${facebookAppId}")
        resValue("string", "microsoft_path", "/${microsoftSignatureHash}")
        resValue("string", "dropbox_app_key", dropboxAppKey)
        resValue("string", "db_login_protocol_scheme", "db-${dropboxAppKey}")

        val rawDir = file("./src/main/res/raw")
        if (!rawDir.exists()) {
            rawDir.mkdirs()
        }

        val configJson = """
            {
              "client_id": "$microsoftClientId",
              "authorization_user_agent": "DEFAULT",
              "redirect_uri": "msauth://$applicationId/${URLEncoder.encode(microsoftSignatureHash, "UTF-8")}",
              "authorities": [{
                "type": "AAD",
                "audience": {
                  "type": "AzureADandPersonalMicrosoftAccount",
                  "tenant_id": "common"
                }
              }],
              "account_mode": "SINGLE"
          }
        """.trimIndent()

        file("./src/main/res/raw/ms_auth_config.json").writeText(configJson)
    }

    signingConfigs {
        // It creates a signing config for release builds if the required properties are set.
        // The if statement is necessary to avoid errors when the packages are built on CI.
        // The alternative would be to pass all the environment variables for signing apk to the packages workflows.
        create("release") {
            val storeFileName =
                getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_FILE_NAME") as? String
            val storePassword =
                getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_STORE_PASSWORD") as? String
            val keyAlias = getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_KEY_ALIAS") as? String
            val keyPassword =
                getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_KEY_PASSWORD") as? String

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
    implementation(Libs.googlePlayBase)

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")

    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)

    // Use local implementation instead of dependencies
    if (useLocalProjects) {
        implementation(project(":packages:plugin-google-gms"))
        implementation(project(":packages:plugin-google-non-gms"))
        implementation(project(":packages:plugin-facebook"))
        implementation(project(":packages:plugin-microsoft"))
        implementation(project(":packages:plugin-dropbox"))
    } else {
        implementation(Libs.omhGoogleGms)
        implementation(Libs.omhGoogleNonGms)
        implementation(Libs.omhFacebook)
        implementation(Libs.omhMicrosoft)
        implementation(Libs.omhDropbox)
    }
}

fun getValueFromEnvOrProperties(name: String): Any? {
    val localProperties = gradleLocalProperties(file("."))
    return System.getenv(name) ?: localProperties[name]
}

fun getValueFromProperties(name: String): String {
    val properties = gradleLocalProperties(rootDir)
    val property = properties[name] as? String
    return property
        ?: throw GradleException("Missing property $name, please add it to the local.properties file")
}


tasks.dokkaHtmlPartial {
    enabled = false
}
