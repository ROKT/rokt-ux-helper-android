package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NetworkSignalType {
    @SerialName("SignalResponse")
    SignalResponse,

    @SerialName("SignalGatedResponse")
    SignalGatedResponse,
}
