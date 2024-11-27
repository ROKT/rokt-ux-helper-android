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
