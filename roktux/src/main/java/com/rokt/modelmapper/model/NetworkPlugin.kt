package com.rokt.modelmapper.model

import com.rokt.network.model.LayoutDisplayPreset
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.LayoutSettings
import com.rokt.network.model.RootSchemaModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkPluginContainer(@SerialName("plugin") val plugin: NetworkPlugin)

@Serializable
data class NetworkPlugin(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("config") val config: NetworkPluginConfig,
    @SerialName("targetElementSelector") val targetElementSelector: String,
)

@Serializable
data class NetworkPluginConfig(
    @SerialName("instanceGuid") val instanceGuid: String,
    @SerialName("token") val token: String,
    @SerialName("outerLayoutSchema")
    @Serializable(with = RootSchemaModelSerializer::class)
    val outerLayoutSchema: RootSchemaModel<LayoutSchemaModel, LayoutDisplayPreset, LayoutSettings>, // DCUI Outer layout
    @SerialName("slots") val slots: List<NetworkSlotLayout>,
)
