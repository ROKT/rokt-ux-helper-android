package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkResponseOption(
    @SerialName("id") val id: String,
    @SerialName("action") val action: NetworkAction?,
    @SerialName("instanceGuid") val instanceGuid: String,
    @SerialName("token") val token: String,
    @SerialName("signalType") val signalType: NetworkSignalType,
    @SerialName("shortLabel") val shortLabel: String,
    @SerialName("longLabel") val longLabel: String,
    @SerialName("shortSuccessLabel") val shortSuccessLabel: String? = null,
    @SerialName("isPositive") val isPositive: Boolean,
    @SerialName("url") val url: String? = null,
    @SerialName("ignoreBranch") val ignoreBranch: Boolean? = null,
)
