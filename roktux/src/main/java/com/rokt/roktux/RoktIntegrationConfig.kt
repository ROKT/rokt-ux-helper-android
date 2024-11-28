package com.rokt.roktux

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Configuration for the Rokt Integration.
 */
@Serializable
data class RoktIntegrationConfig(
    @SerialName("name")
    val name: String,
    @SerialName("version")
    val version: String,
    @SerialName("framework")
    val framework: String,
    @SerialName("platform")
    val platform: String,
    @SerialName("layoutSchemaVersion")
    val layoutSchemaVersion: String,
    @SerialName("packageVersion")
    val packageVersion: String,
    @SerialName("packageName")
    val packageName: String,
    @SerialName("operatingSystem")
    val operatingSystem: String,
    @SerialName("operatingSystemVersion")
    val operatingSystemVersion: String,
    @SerialName("deviceLocale")
    val deviceLocale: String,
    @SerialName("deviceType")
    val deviceType: String,
    @SerialName("deviceModel")
    val deviceModel: String,
    @SerialName("metadata")
    val metadata: Map<String, String>,
) {
    fun toJsonString(): String = Json.encodeToString(this)
}
