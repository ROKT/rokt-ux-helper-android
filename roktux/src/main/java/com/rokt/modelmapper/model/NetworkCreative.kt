package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCreative(
    @SerialName("referralCreativeId") val referralCreativeId: String,
    @SerialName("instanceGuid") val instanceGuid: String,
    @SerialName("token") val token: String,
    @SerialName("responseOptions") val responseOptions: List<NetworkResponseOption>,
    @SerialName("copy") val copy: Map<String, String>,
)
