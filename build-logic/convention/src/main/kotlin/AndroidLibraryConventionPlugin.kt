import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import com.rokt.roktux.BuildConfigs
import com.rokt.roktux.configureFlavors
import com.rokt.roktux.configureKotlinAndroid
import com.rokt.roktux.configurePrintApksTask
import com.rokt.roktux.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }
            var versionParam = "sdkVersionName"
            if (rootProject.name.contains(
                    "UxHelper",
                    true,
                )
            ) {
                versionParam = "roktUxHelper"
            }
            if (target.path.contains(":ux-helper:")) {
                versionParam = "roktUxHelper"
            }
            val sdkVersionName = libs.findVersion(versionParam).get().toString()
            val sdkVersionCode = libs.findVersion("sdkVersionCode").get().toString().toInt()
            val dcuiVersion = libs.findVersion("dcui").get().toString()
            val buildConfigs = BuildConfigs(
                versionName = sdkVersionName,
                dcuiVersion = formatDcuiVersion(dcuiVersion).orEmpty(),
            )
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 33
                defaultConfig.versionName = sdkVersionName
                defaultConfig.versionCode = sdkVersionCode
                resourcePrefix = "rokt_" // Prefix for all resources
                if (target.findProperty("useProductFlavours") as? String == "true") {
                    configureFlavors(this, buildConfigs)
                }
            }
            extensions.configure<LibraryAndroidComponentsExtension> {
                configurePrintApksTask(this)
            }
            dependencies {
                add("androidTestImplementation", kotlin("test"))
                add("testImplementation", kotlin("test"))
            }
        }
    }

    // Format the version into {major}.{minor} format
    private fun formatDcuiVersion(version: String): String? = "[0-9]+\\.[0-9]+".toRegex().find(version)?.value
}
