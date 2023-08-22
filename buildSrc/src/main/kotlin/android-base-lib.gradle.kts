/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("com.android.library")
    id("io.gitlab.arturbosch.detekt")
    kotlin("android")
    id("jacoco")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka")
}

detekt {
    autoCorrect = properties.get("autoCorrect")?.toString()?.toBoolean() ?: false
}

android {
    compileSdk = ConfigData.compileSdkVersion
    defaultConfig {
        minSdk = ConfigData.minSdkVersion
        vectorDrawables {
            useSupportLibrary = true
        }
        consumerProguardFiles("consumer-rules.pro")
    }

    packagingOptions {
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
}

setupJacoco()

dependencies {
    detektPlugins(BuildPlugins.detekt)
}


// Publishing block

val androidSourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from("src/main/java")
    from("src/main/kotlin")
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn("dokkaJavadoc")
    archiveClassifier.set("javadoc")
    from("dokkaJavadoc.outputDirectory")
}

artifacts {
    add("archives", androidSourcesJar)
    add("archives", javadocJar)
}

val groupProperty = getPropertyOrFail("group")
val versionProperty = getPropertyOrFail("version")
val artifactId = getPropertyOrFail("artifactId")
val mDescription = getPropertyOrFail("description")

group = groupProperty
version = versionProperty

afterEvaluate {
    publishing {
        publications {
            register("release", MavenPublication::class.java) {
                setupPublication()
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        rootProject.ext["signingKeyId"].toString(),
        rootProject.ext["signingKey"].toString(),
        rootProject.ext["signingPassword"].toString(),
    )
    sign(publishing.publications)
}

fun MavenPublication.setupPublication() {
    groupId = groupProperty
    artifactId = artifactId
    version = versionProperty

    if (project.project.plugins.findPlugin("com.android.library") != null) {
        from(project.components["release"])
    } else {
        from(project.components["java"])
    }

    artifact(androidSourcesJar)
    artifact(javadocJar)

    pom {
        name.set(artifactId)
        description.set(mDescription)
        url.set("https://github.com/openmobilehub/omh-auth")
        licenses {
            license {
                name.set("Apache-2.0 License")
                url.set("https://github.com/openmobilehub/omh-auth/blob/main/LICENSE")
            }
        }

        developers {
            developer {
                id.set("Anwera64")
                name.set("Anton Soares")
            }
        }

        // Version control info - if you're using GitHub, follow the
        // format as seen here
        scm {
            connection.set("scm:git:github.com/openmobilehub/omh-auth.git")
            developerConnection.set("scm:git:ssh://github.com/openmobilehub/omh-auth.git")
            url.set("https://github.com/openmobilehub/omh-auth")
        }
    }
}