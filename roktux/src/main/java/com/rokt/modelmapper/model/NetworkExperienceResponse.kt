package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkExperienceResponse(
    @SerialName("sessionId") val sessionId: String,
    @SerialName("pageContext") val pageContext: NetworkPageContext,
    @SerialName("options") val options: NetworkOptions,
    @SerialName("plugins") val plugins: List<NetworkPluginContainer>,
    @SerialName("success") val success: Boolean,
)
