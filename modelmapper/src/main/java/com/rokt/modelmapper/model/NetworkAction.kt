package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NetworkAction {
    @SerialName("Url")
    Url,

    @SerialName("CaptureOnly")
    CaptureOnly,

    @SerialName("ExternalPaymentTrigger")
    ExternalPaymentTrigger,
}
