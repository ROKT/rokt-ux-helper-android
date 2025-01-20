package com.rokt.roktux.publish

import org.gradle.api.provider.Property

interface RoktMavenPublishExtension {

    /**
     * Library version
     */
    val version: Property<String>

    /**
     * Group Id of the library
     */
    val groupId: Property<String>

    /**
     * Artifact Id of the library
     */
    val artifactId: Property<String>

    /**
     * Suffix string to be added at the end of mock variant
     */
    val mockSuffix: Property<String>

    /**
     * Set to true to include docs from `./doc` path
     */
    val includeDocs: Property<Boolean>

    /**
     * description of the library
     */
    val description: Property<String>
}
