plugins {
    alias(libs.plugins.rokt.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rokt.networkhelper"

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "BASE_URL", "\"${rootProject.extra["BASE_URL"] as String}\"")
        buildConfigField("String", "ROKT_PUB_ID", "\"${rootProject.extra["ROKT_PUB_ID"] as String}\"")
        buildConfigField("String", "ROKT_SECRET", "\"${rootProject.extra["ROKT_SECRET"] as String}\"")
        buildConfigField(
            "String",
            "ROKT_CLIENT_UNIQUE_ID",
            "\"${rootProject.extra["ROKT_CLIENT_UNIQUE_ID"] as String}\"",
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
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
