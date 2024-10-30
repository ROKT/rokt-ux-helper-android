plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

// Retrieve API keys from environment variables
val roktPubId: String? = System.getenv("ROKT_PUB_ID")
val roktSecret: String? = System.getenv("ROKT_SECRET")
val roktClientUniqueId: String? = System.getenv("ROKT_CLIENT_UNIQUE_ID")

android {
    namespace = "com.rokt.networkhelper"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "ROKT_PUB_ID", "\"$roktPubId\"")
        buildConfigField("String", "ROKT_SECRET", "\"$roktSecret\"")
        buildConfigField("String", "ROKT_CLIENT_UNIQUE_ID", "\"$roktClientUniqueId\"")
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
