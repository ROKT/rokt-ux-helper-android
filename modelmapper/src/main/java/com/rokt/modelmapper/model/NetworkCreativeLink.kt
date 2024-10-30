package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCreativeLink(
    @SerialName("url") val url: String,
    @SerialName("title") val title: String,
)
