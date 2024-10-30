package com.rokt.networkhelper.model

import kotlinx.serialization.Serializable

data class ExperienceRequest(
    val pageIdentifier: String,
    val attributes: Map<String, String>,
    val integrationInfo: String, // Integration Info available from RoktUXHelper library
    val sessionId: String? = null,
)

@Serializable
internal data class NetworkExperienceRequest(
    val pageIdentifier: String,
    val attributes: Map<String, String>,
    val integration: IntegrationInfo?, // Integration Info available from RoktUXHelper library
    val sessionId: String? = null,
)

@Serializable
internal data class IntegrationInfo(
    val name: String,
    val version: String,
    val framework: String,
    val platform: String,
    val layoutSchemaVersion: String,
    val packageVersion: String,
    val packageName: String,
    val operatingSystem: String,
    val operatingSystemVersion: String,
    val deviceLocale: String,
    val deviceType: String,
    val deviceModel: String,
    val metadata: Map<String, String>,
)
