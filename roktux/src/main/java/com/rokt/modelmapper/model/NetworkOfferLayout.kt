package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkOfferLayout(
    @SerialName("campaignId") val campaignId: String,
    @SerialName("creative") val creative: NetworkCreativeLayout,
    @SerialName("catalogItems") val catalogItems: List<NetworkCatalogItem> = emptyList(),
)
