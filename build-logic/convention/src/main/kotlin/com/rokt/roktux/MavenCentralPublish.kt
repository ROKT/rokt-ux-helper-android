package com.rokt.roktux

import com.rokt.roktux.publish.RoktMavenPublishExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal

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
}
