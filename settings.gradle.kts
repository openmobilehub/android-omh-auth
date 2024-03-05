pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id("org.jetbrains.dokka") version "1.9.10"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OMH Auth"
include(":packages:core")
include(":apps:auth-sample")
include(":packages:plugin-google-non-gms")
include(":packages:plugin-google-gms")
include(":packages:plugin-facebook")
include(":packages:plugin-microsoft")
include(":packages:plugin-dropbox")