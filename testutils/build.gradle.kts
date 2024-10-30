plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.rokt.core.testutils"

    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
