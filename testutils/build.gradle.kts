plugins {
    alias(libs.plugins.rokt.android.library)
    alias(libs.plugins.rokt.android.library.compose)
    alias(libs.plugins.rokt.android.library.publish)
}

val libGroupId = "com.rokt.core"
val libArtifactId = "testutils"
val formattedVersion by extra {
    val versionFromProperty = project.findProperty("VERSION")?.toString().takeIf { !it.isNullOrBlank() } ?: "0.0.0"
    val versionSuffix = project.findProperty("VERSION_SUFFIX")?.toString().takeIf { !it.isNullOrBlank() } ?: ""
    versionFromProperty + versionSuffix
}
val libDescription = "Rokt Core Test Utilities"

roktMavenPublish {
    version.set(formattedVersion)
    groupId.set(libGroupId)
    artifactId.set(libArtifactId)
    description.set(libDescription)
}

android {
    namespace = "com.rokt.core.testutils"
}

dependencies {
    implementation(platform(libs.okhttp.bom))
    implementation(platform(libs.androidx.compose.bom))
    api(libs.kotlinx.coroutines.test)
    api(libs.bundles.mockk)
    api(libs.junit4)
    api(libs.assertj.core)
    api(libs.okhttp.mockwebserver)
    api(libs.androidx.compose.ui.test)
    api(libs.androidx.compose.ui.testManifest)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.robolectric)
    lintChecks(libs.compose.lint.checks)
}
