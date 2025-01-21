import com.rokt.roktux.configureMavenPublishing
import com.rokt.roktux.publish.RoktMavenPublishExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryMavenCentralPublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.vanniktech.maven.publish")
            }
            val publishExtension = extensions.create("roktMavenPublish", RoktMavenPublishExtension::class.java)
            configureMavenPublishing(publishExtension)
        }
    }
}
