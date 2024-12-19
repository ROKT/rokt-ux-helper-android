import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import java.util.Properties

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlinKapt) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.maven.publish) apply false
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}

val formattedVersion by extra {
    "${libs.versions.roktUxHelper.get()}${System.getenv("VERSION_SUFFIX").takeIf { !it.isNullOrBlank() } ?: ""}"
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

// Helper function to retrieve property with priority to environment variable
fun getProperty(key: String, defaultValue: String = ""): String =
    System.getenv(key) ?: localProperties.getProperty(key, defaultValue)

// Set extra properties that will be accessible in all modules
extra["BASE_URL"] = getProperty("BASE_URL", "https://default-url.com")
extra["VIEW_NAME"] = getProperty("VIEW_NAME", "defaultView")
extra["ROKT_TAG_ID"] = getProperty("ROKT_TAG_ID", "defaultTagId")
extra["ROKT_PUB_ID"] = getProperty("ROKT_PUB_ID", "defaultPubId")
extra["ROKT_SECRET"] = getProperty("ROKT_SECRET", "defaultSecretId")
extra["ROKT_CLIENT_UNIQUE_ID"] = getProperty("ROKT_CLIENT_UNIQUE_ID", "defaultClientId")

configure(subprojects) {
    pluginManager.withPlugin("com.vanniktech.maven.publish") {
        val libArtifactId = project.name
        val libGroupId = findProperty("libGroupId")?.toString()
        val libDescription = findProperty("libDescription")?.toString()

        extensions.configure<MavenPublishBaseExtension> {
            println("Configuring Maven publish for project ${project.name}")
            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
            coordinates(artifactId = libArtifactId, groupId = libGroupId, version = formattedVersion)
            signAllPublications()

            pom {
                name.set(libArtifactId)
                description.set(libDescription)
                url.set("https://docs.rokt.com")
                licenses {
                    license {
                        name.set("Copyright 2024 Rokt Pte Ltd")
                        url.set("https://rokt.com/sdk-license-2-0/")
                    }
                }
                developers {
                    developer {
                        organization {
                            name.set("Rokt Pte Ltd")
                            url.set("https://rokt.com")
                        }
                        name.set("Rokt")
                        email.set("nativeappsdev@rokt.com")
                    }
                }
                scm {
                    url.set("https://github.com/ROKT/rokt-ux-helper-android")
                    connection.set("scm:git:git://github.com/ROKT/rokt-ux-helper-android.git")
                    developerConnection.set("scm:git:https://github.com/ROKT/rokt-ux-helper-android.git")
                }
            }
        }
    }
}
