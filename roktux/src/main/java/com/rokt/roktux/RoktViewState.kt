package com.rokt.roktux

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoktViewState(
    @SerialName("pluginId")
    val pluginId: String,
    @SerialName("customStates")
    val customStates: Map<String, Int>,
    @SerialName("offerCustomStates")
    val offerCustomStates: Map<String, Map<String, Int>>,
    @SerialName("offerIndex")
    val offerIndex: Int,
    @SerialName("pluginDismissed")
    val pluginDismissed: Boolean,
)
