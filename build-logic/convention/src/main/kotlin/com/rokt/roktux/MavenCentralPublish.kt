package com.rokt.roktux

import com.rokt.roktux.publish.RoktMavenPublishExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal
import org.gradle.api.tasks.bundling.Jar
import java.io.File

fun Project.configureMavenPublishing(roktMavenPublish: RoktMavenPublishExtension) {
    val versionFromProperty = project.findProperty("VERSION")?.toString().takeIf { !it.isNullOrBlank() } ?: "0.0.0"
    val versionSuffix = project.findProperty("VERSION_SUFFIX")?.toString().takeIf { !it.isNullOrBlank() } ?: ""
    val shouldSign = !project.findProperty("signingInMemoryKey")?.toString().isNullOrBlank()
    val formattedVersion = versionFromProperty + versionSuffix

    extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
        afterEvaluate {
            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
            coordinates(
                artifactId = roktMavenPublish.artifactId.get(),
                groupId = roktMavenPublish.groupId.get(),
                version = formattedVersion,
            )
            if (shouldSign) {
                signAllPublications()
            } else {
                println("Skip signAllPublications")
            }
            pom {
                name.set(roktMavenPublish.artifactId.get())
                description.set(roktMavenPublish.description.get())
                url.set("https://docs.rokt.com")
                licenses {
                    license {
                        name.set("Copyright 2024 Rokt Pte Ltd")
                        url.set("https://rokt.com/sdk-license-2-0/")
                    }
                }
                developers {
                    developer {
                        organization {
                            name.set("Rokt Pte Ltd")
                            url.set("https://rokt.com")
                        }
                        name.set("Rokt")
                        email.set("nativeappsdev@rokt.com")
                    }
                }
                scm {
                    url.set("https://github.com/ROKT/rokt-ux-helper-android")
                    connection.set("scm:git:git://github.com/ROKT/rokt-ux-helper-android.git")
                    developerConnection.set("scm:git:https://github.com/ROKT/rokt-ux-helper-android.git")
                }
            }
        }

        val publicationName = "maven"
        val generatePomTaskName =
            "generatePomFileFor${publicationName.replaceFirstChar { it.uppercaseChar() }}Publication"
        val validateTaskName =
            "validatePomFor${publicationName.replaceFirstChar { it.uppercaseChar() }}Publication"

        tasks.register(validateTaskName, ValidatePomTask::class.java) {
            description = "Validates the generated POM file for the '$publicationName' publication."
            group = "verification"
            pomFile.set(project.layout.buildDirectory.file("publications/$publicationName/pom-default.xml"))
            dependsOn(generatePomTaskName)
        }

        tasks.withType(PublishToMavenLocal::class.java).configureEach {
            if (name.contains(publicationName, ignoreCase = true)) {
                dependsOn(validateTaskName)
            }
        }
    }

    project.afterEvaluate {
        if (roktMavenPublish.includeDocs.getOrElse(false)) {
            val docDir = File(project.projectDir, "doc")
            if (!docDir.exists() || !docDir.isDirectory) return@afterEvaluate

            val namespace = project.extensions.findByName("android")?.let {
                it::class.java.getMethod("getNamespace").invoke(it)?.toString()
            } ?: "com.rokt.roktux"
            val packagePath = namespace.replace('.', '/')

            val sourceJarTaskNames = listOf(
                "sourceDevReleaseJar",
                "sourcesJar",
                "devReleaseSourcesJar",
                "sourceReleaseJar",
                "releaseSourcesJar",
            )

            val sourceJarTask = sourceJarTaskNames
                .firstNotNullOfOrNull { taskName -> project.tasks.findByName(taskName) } ?: return@afterEvaluate

            sourceJarTask.enabled = false

            val variantName = if (sourceJarTask.name.contains("Dev")) "devRelease" else "release"
            val intermediateDir = File(project.buildDir, "intermediates/source_jar/$variantName")
            val outputDir = File(project.buildDir, "libs")
            val outputFileName = "${project.name}-$formattedVersion-sources.jar"

            val customTaskName = "doc${sourceJarTask.name.replaceFirstChar { it.uppercase() }}"
            val docTask = project.tasks.create(customTaskName, Jar::class.java) {
                archiveClassifier.set("sources")
                from(docDir) { into(packagePath) }
                destinationDirectory.set(outputDir)
                archiveFileName.set(outputFileName)
            }

            sourceJarTask.dependsOn(docTask)

            sourceJarTask.doLast {
                project.copy {
                    from(docTask.archiveFile)
                    into(docTask.destinationDirectory)
                    rename { docTask.archiveFileName.get() }
                }
            }

            val createDirTask = project.tasks.create("createIntermediateSourceJarDir") {
                doLast {
                    intermediateDir.mkdirs()
                    project.copy {
                        from(docTask.archiveFile)
                        into(intermediateDir)
                        rename { "$variantName-sources.jar" }
                    }
                }
            }

            docTask.finalizedBy(createDirTask)
        }
    }
}
