plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rokt.networkhelper"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "BASE_URL", "\"${rootProject.extra["BASE_URL"] as String}\"")
        buildConfigField("String", "ROKT_PUB_ID", "\"${rootProject.extra["ROKT_PUB_ID"] as String}\"")
        buildConfigField("String", "ROKT_SECRET", "\"${rootProject.extra["ROKT_SECRET"] as String}\"")
        buildConfigField("String", "ROKT_CLIENT_UNIQUE_ID", "\"${rootProject.extra["ROKT_CLIENT_UNIQUE_ID"] as String}\"")
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
}

dependencies {
    implementation(libs.bundles.retrofit)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp.core)
    lintChecks(libs.compose.lint.checks)
}
