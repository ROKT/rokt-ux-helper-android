package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSlotLayout(
    @SerialName("instanceGuid") val instanceGuid: String,
    @SerialName("token") val token: String,
    @SerialName("offer") val offer: NetworkOfferLayout? = null,
    @SerialName("layoutVariant") val layoutVariant: NetworkLayoutVariant? = null,
)
