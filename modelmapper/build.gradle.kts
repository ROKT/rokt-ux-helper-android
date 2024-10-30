import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
}

val libGroupId = "com.rokt"
val libArtifactId = "modelmapper"
val formattedVersion: String by project
val libDescription = "Rokt Model Mapper Library"

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    buildFeatures {
        compose = true
    }
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
