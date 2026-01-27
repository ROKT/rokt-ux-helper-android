package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkPage(
    @SerialName("pageId") val pageId: String,
    @SerialName("pageType") val pageType: String,
    @SerialName("platformType") val platformType: String,
)
