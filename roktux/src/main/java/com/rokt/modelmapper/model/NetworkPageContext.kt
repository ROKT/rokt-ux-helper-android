package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkPageContext(
    @SerialName("pageInstanceGuid") val pageInstanceGuid: String,
    @SerialName("pageId") val pageId: String,
    @SerialName("language") val language: String? = null,
    @SerialName("isPageDetected") val isPageDetected: Boolean,
    @SerialName("pageVariantName") val pageVariantName: String? = null,
    @SerialName("token") val token: String,
)
