package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCreativeLayout(
    @SerialName("referralCreativeId") val referralCreativeId: String,
    @SerialName("instanceGuid") val instanceGuid: String,
    @SerialName("token") val token: String,
    @SerialName("responseOptionsMap") val responseOptions: Map<String, NetworkResponseOption>,
    @SerialName("copy") val copy: Map<String, String>,
    @SerialName("images") val images: Map<String, NetworkCreativeImage>,
    @SerialName("links") val links: Map<String, NetworkCreativeLink>,
    @SerialName("icons") val icons: Map<String, NetworkCreativeIcon>,
)
