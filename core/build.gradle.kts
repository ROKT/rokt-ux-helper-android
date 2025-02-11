plugins {
    alias(libs.plugins.rokt.android.library)
    alias(libs.plugins.rokt.android.library.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    alias(libs.plugins.rokt.android.library.publish)
}

val libGroupId = "com.rokt"
val libArtifactId = "core"
val formattedVersion by extra {
    val versionFromProperty = project.findProperty("VERSION")?.toString().takeIf { !it.isNullOrBlank() } ?: "0.0.0"
    val versionSuffix = project.findProperty("VERSION_SUFFIX")?.toString().takeIf { !it.isNullOrBlank() } ?: ""
    versionFromProperty + versionSuffix
}
val libDescription = "Rokt Core Library"

roktMavenPublish {
    version.set(formattedVersion)
    groupId.set(libGroupId)
    artifactId.set(libArtifactId)
    description.set(libDescription)
}

android {
    namespace = "com.rokt.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.viewModelCompose)
    testImplementation(libs.junit4)
    lintChecks(libs.compose.lint.checks)
    dokkaPlugin(libs.dokka.android)
}

// TODO: remove this once https://github.com/gradle/gradle/issues/23572 is fixed
fun Project.localGradleProperty(name: String): Provider<String> = provider {
    if (hasProperty(name)) property(name)?.toString() else null
}
