package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCatalogItemGroup(
    @SerialName("groupId") val groupId: String,
    @SerialName("catalogItemIds") val catalogItemIds: List<String> = emptyList(),
    @SerialName("attributes") val attributes: List<NetworkCatalogItemGroupAttribute> = emptyList(),
    @SerialName("metadata") val metadata: Map<String, String> = emptyMap(),
)

@Serializable
data class NetworkCatalogItemGroupAttribute(
    @SerialName("attributeId") val attributeId: String,
    @SerialName("label") val label: String? = null,
    @SerialName("options") val options: List<NetworkCatalogItemGroupOption> = emptyList(),
    @SerialName("metadata") val metadata: Map<String, String> = emptyMap(),
)

@Serializable
data class NetworkCatalogItemGroupOption(
    @SerialName("label") val label: String? = null,
    @SerialName("catalogItemIds") val catalogItemIds: List<String> = emptyList(),
    @SerialName("metadata") val metadata: Map<String, String> = emptyMap(),
)
