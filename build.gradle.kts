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
    alias(libs.plugins.kover) apply false
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
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
extra["BASE_URL"] = getProperty("BASE_URL", "https://server-api.rokt.com")
extra["VIEW_NAME"] = getProperty("VIEW_NAME", "jetorderreview")
extra["ROKT_TAG_ID"] = getProperty("ROKT_TAG_ID", "3254379042274070528")
extra["ROKT_PUB_ID"] = getProperty("ROKT_PUB_ID", "rpub-fff50aac-d338-406d-ba71-83918e0419da")
extra["ROKT_SECRET"] = getProperty("ROKT_SECRET", "rsec-96c5f509-e159-4088-9606-9e0b6648742d")
extra["ROKT_CLIENT_UNIQUE_ID"] = getProperty("ROKT_CLIENT_UNIQUE_ID", "bd466933-91ff-401d-b583-0d8d3898a04")
