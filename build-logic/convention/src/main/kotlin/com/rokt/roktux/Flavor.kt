package com.rokt.roktux

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import java.util.*

enum class FlavorDimension {
    RoktEnvironment,
    ;

    override fun toString(): String = super.toString().toLowerCase(Locale.ENGLISH)
}

enum class Flavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    Mock(FlavorDimension.RoktEnvironment),
    Dev(FlavorDimension.RoktEnvironment, ".prod"),
    ;

    override fun toString(): String = super.toString().toLowerCase(Locale.ENGLISH)
}

data class BuildConfigs(val versionName: String, val dcuiVersion: String)

fun Project.configureFlavors(commonExtension: CommonExtension<*, *, *, *, *, *>, buildConfigs: BuildConfigs? = null) {
    commonExtension.apply {
        flavorDimensions += FlavorDimension.RoktEnvironment.toString()
        productFlavors {
            Flavor.values().forEach {
                create(it.toString()) {
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (it.applicationIdSuffix != null) {
                            this.applicationIdSuffix = it.applicationIdSuffix
                        }
                    }
                    if (buildConfigs != null) {
                        buildConfigField("String", "VERSION_NAME", "\"${buildConfigs.versionName}\"")
                        buildConfigField("String", "DCUI_VERSION", "\"${buildConfigs.dcuiVersion}\"")
                    }
                }
            }
        }
    }
}
