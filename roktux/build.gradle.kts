plugins {
    alias(libs.plugins.rokt.android.library)
    alias(libs.plugins.rokt.android.library.compose)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    alias(libs.plugins.rokt.android.library.publish)
    alias(libs.plugins.roborazzi)
}

val libGroupId = "com.rokt"
val libArtifactId = "roktux"
val formattedVersion by extra {
    val versionFromProperty = project.findProperty("VERSION")?.toString().takeIf { !it.isNullOrBlank() } ?: "0.0.0"
    val versionSuffix = project.findProperty("VERSION_SUFFIX")?.toString().takeIf { !it.isNullOrBlank() } ?: ""
    versionFromProperty + versionSuffix
}
val libDescription = "Rokt UX Helper Library"

roktMavenPublish {
    version.set(formattedVersion)
    groupId.set(libGroupId)
    artifactId.set(libArtifactId)
    description.set(libDescription)
}

android {
    namespace = "com.rokt.roktux"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "SDK_VERSION", "\"${formattedVersion}\"")
        buildConfigField("String", "SCHEMA_VERSION", "\"${libs.versions.dcui.get()}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.useJUnit {
                    // Run screenshot tests by using -PrunSnapshotTests
                    if (project.hasProperty("runSnapshotTests")) {
                        includeCategories("com.rokt.roktux.snapshot.SnapshotTest")
                    }
                }
            }
        }
    }
}

tasks.withType(Test::class.java) {
    systemProperty("robolectric.logging", "stdout")
}

dependencies {
    api(libs.dcui.schema)
    api(libs.kotlinx.serialization.json)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.svg)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.window.manager)
    debugApi(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.browser)
    testImplementation(platform(libs.okhttp.bom))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.bundles.mockk)
    testImplementation(libs.assertj.core)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.androidx.compose.ui.testManifest)
    testImplementation(libs.junit4)
    testImplementation(libs.junit.params)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.coil.test)
    debugImplementation(libs.rebugger) // Use only in debug builds
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    lintChecks(libs.compose.lint.checks)
    dokkaPlugin(libs.dokka.android)
}

// TODO: remove this once https://github.com/gradle/gradle/issues/23572 is fixed
fun Project.localGradleProperty(name: String): Provider<String> = provider {
    if (hasProperty(name)) property(name)?.toString() else null
}
