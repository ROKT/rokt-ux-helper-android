plugins {
    alias(libs.plugins.rokt.android.application)
    alias(libs.plugins.rokt.android.application.compose)
}

android {
    namespace = "com.rokt.sizereport.baseline"

    defaultConfig {
        applicationId = "com.rokt.sizereport.baseline"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    lint {
        checkReleaseBuilds = false
    }
}

dependencies {

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
}
