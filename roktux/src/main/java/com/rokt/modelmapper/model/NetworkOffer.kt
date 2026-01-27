package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkOffer(
    @SerialName("campaignId") val campaignId: String,
    @SerialName("creative") val creative: NetworkCreative,
)
