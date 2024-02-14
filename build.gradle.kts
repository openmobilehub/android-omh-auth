import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import java.net.URL
import java.util.Properties

val properties = Properties()
val localPropertiesFile = project.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}
val useMavenLocal = getBooleanFromProperties("useMavenLocal")
val useLocalProjects = getBooleanFromProperties("useLocalProjects")

if (useLocalProjects) {
    println("OMH Auth project running with useLocalProjects enabled ")
}

if (useMavenLocal) {
    println("OMH Auth project running with useMavenLocal enabled${if (useLocalProjects) ", but only publishing will be altered since dependencies are overriden by useLocalProjects" else ""} ")
}

project.extra.set("useLocalProjects", useLocalProjects)
project.extra.set("useMavenLocal", useMavenLocal)

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("org.jetbrains.dokka")
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")

    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets.configureEach {
            documentedVisibilities.set(
                setOf(
                    Visibility.PUBLIC,
                    Visibility.PROTECTED
                )
            )

            sourceLink {
                val exampleDir = "https://github.com/openmobilehub/android-omh-auth/tree/main"

                localDirectory.set(rootProject.projectDir)
                remoteUrl.set(URL(exampleDir))
                remoteLineSuffix.set("#L")
            }
        }
    }

    if (useMavenLocal) {
        repositories {
            mavenLocal()
            gradlePluginPortal()
            google()
        }
    } else {
        repositories {
            mavenCentral()
            google()
            maven("https://s01.oss.sonatype.org/content/groups/staging/")
        }
    }
}

tasks.dokkaHtmlMultiModule {
    moduleName.set("OMH Auth")
    outputDirectory.set(rootProject.projectDir.resolve("docs/generated"))
}

tasks.register("installPrePushHook", Copy::class) {
    from("tools/scripts/pre-push")
    into(".git/hooks")
    fileMode = 0b000_111_111_111
}

tasks.register("installPreCommitHook", Copy::class) {
    from("tools/scripts/pre-commit")
    into(".git/hooks")
    fileMode = 0b000_111_111_111
}

tasks {
    val installPrePushHook by existing
    val installPreCommitHook by existing
    getByName("prepareKotlinBuildScriptModel").dependsOn(installPrePushHook)
    getByName("prepareKotlinBuildScriptModel").dependsOn(installPreCommitHook)
}

val ossrhUsername by extra(getValueFromEnvOrProperties("OSSRH_USERNAME"))
val ossrhPassword by extra(getValueFromEnvOrProperties("OSSRH_PASSWORD"))
val mStagingProfileId by extra(getValueFromEnvOrProperties("SONATYPE_STAGING_PROFILE_ID"))
val signingKeyId by extra(getValueFromEnvOrProperties("SIGNING_KEY_ID"))
val signingPassword by extra(getValueFromEnvOrProperties("SIGNING_PASSWORD"))
val signingKey by extra(getValueFromEnvOrProperties("SIGNING_KEY"))

// Set up Sonatype repository
nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(mStagingProfileId.toString())
            username.set(ossrhUsername.toString())
            password.set(ossrhPassword.toString())
            // Add these lines if using new Sonatype infra
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

fun getValueFromEnvOrProperties(name: String): Any? {
    val localProperties = gradleLocalProperties(rootDir)
    return System.getenv(name) ?: localProperties[name]
}

fun getBooleanFromProperties(name: String): Boolean {
    val localProperties = gradleLocalProperties(rootDir)
    return (project.ext.has(name) && project.ext.get(name) == "true") || localProperties[name] == "true"
}
