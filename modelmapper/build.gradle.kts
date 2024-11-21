plugins {
    alias(libs.plugins.rokt.android.library)
    alias(libs.plugins.rokt.android.library.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    alias(libs.plugins.rokt.android.library.publish)
}

val libGroupId = "com.rokt"
val libArtifactId = "modelmapper"
val formattedVersion =
    "${libs.versions.roktUxHelper.get()}${System.getenv("VERSION_SUFFIX").takeIf { !it.isNullOrBlank() } ?: ""}"
val libDescription = "Rokt Model Mapper Library"

roktMavenPublish {
    version.set(formattedVersion)
    groupId.set(libGroupId)
    artifactId.set(libArtifactId)
    description.set(libDescription)
}

android {
    namespace = "com.rokt.modelmapper"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    api(libs.dcui.schema)
    api(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.kotlin.reflect)
    testApi(libs.bundles.mockk)
    testImplementation(libs.junit4)
    testImplementation(libs.junit.params)
    androidTestImplementation(libs.androidx.junit)
    lintChecks(libs.compose.lint.checks)
    dokkaPlugin(libs.dokka.android)
}

// TODO: remove this once https://github.com/gradle/gradle/issues/23572 is fixed
fun Project.localGradleProperty(name: String): Provider<String> = provider {
    if (hasProperty(name)) property(name)?.toString() else null
}
