import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
}

val libGroupId = "com.rokt"
val libArtifactId = "roktux"
val formattedVersion: String by project
val libDescription = "Rokt UX Helper Library"

mavenPublishing {
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

android {
    namespace = "com.rokt.roktux"
    compileSdk = 34

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

tasks.withType(Test::class.java) {
    systemProperty("robolectric.logging", "stdout")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    val enableMetricsProvider = project.localGradleProperty("enableComposeCompilerMetrics")
    if (enableMetricsProvider.orNull == "true") {
        val metricsFolder = File(project.buildDir, "compose-metrics")
        compilerOptions.freeCompilerArgs.addAll(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + metricsFolder.absolutePath,
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    val enableReportsProvider = project.localGradleProperty("enableComposeCompilerReports")
    if (enableReportsProvider.orNull == "true") {
        val reportsFolder = File(project.buildDir, "compose-reports")
        compilerOptions.freeCompilerArgs.addAll(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + reportsFolder.absolutePath,
        )
    }
}

dependencies {

    api(projects.uxHelper.modelmapper)
    testDebugImplementation(projects.uxHelper.testutils)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.svg)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material.iconsExtended)
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
    testImplementation(projects.uxHelper.modelmapper)
    testImplementation(libs.junit4)
    testImplementation(libs.junit.params)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.kotlinx.serialization.json)
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
