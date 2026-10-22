import com.rokt.roktux.configureMavenPublishing
import com.rokt.roktux.publish.RoktMavenPublishExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidLibraryMavenCentralPublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.vanniktech.maven.publish")
            }
            val publishExtension = extensions.create("roktMavenPublish", RoktMavenPublishExtension::class.java)
            configureMavenPublishing(publishExtension)
            configureConsumerKotlinCompatibility()
        }
    }

    // Keeps published metadata and the kotlin-stdlib dependency readable by consumers on older Kotlin compilers.
    private fun Project.configureConsumerKotlinCompatibility() {
        extensions.configure<KotlinAndroidProjectExtension> {
            coreLibrariesVersion = CONSUMER_KOTLIN_STDLIB_VERSION
        }
        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                languageVersion.set(CONSUMER_KOTLIN_VERSION)
                apiVersion.set(CONSUMER_KOTLIN_VERSION)
            }
        }
    }

    private companion object {
        const val CONSUMER_KOTLIN_STDLIB_VERSION = "2.1.20"
        val CONSUMER_KOTLIN_VERSION = KotlinVersion.KOTLIN_2_1
    }
}
