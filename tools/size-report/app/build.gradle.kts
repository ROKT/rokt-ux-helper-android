plugins {
    alias(libs.plugins.rokt.android.application)
    alias(libs.plugins.rokt.android.application.compose)
}

android {
    namespace = "com.rokt.sizereport"

    defaultConfig {
        applicationId = "com.rokt.sizereport"
        versionCode = 1
        versionName = "1.0"
    }

    // Two flavors of one app, differing only in whether they depend on roktux.
    flavorDimensions += "sizevariant"
    productFlavors {
        create("baseline") { dimension = "sizevariant" }
        create("withHelper") { dimension = "sizevariant" }
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
    "withHelperImplementation"("com.rokt:roktux")
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
}
