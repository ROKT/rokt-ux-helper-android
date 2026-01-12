package com.rokt.roktux

import com.rokt.roktux.publish.RoktMavenPublishExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Project

fun Project.configureMavenPublishing(roktMavenPublish: RoktMavenPublishExtension) {
    val shouldSkipSign = project.findProperty("SKIP_SIGN")?.toString() == "true"

    extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
        afterEvaluate {
            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
            coordinates(
                artifactId = roktMavenPublish.artifactId.get(),
                groupId = roktMavenPublish.groupId.get(),
                version = roktMavenPublish.version.get(),
            )

            if (!shouldSkipSign) {
                //   signAllPublications()
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
    }
}
