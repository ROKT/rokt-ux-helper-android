package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCreativeImage(
    @SerialName("light") val light: String,
    @SerialName("dark") val dark: String,
    @SerialName("alt") val alt: String,
    @SerialName("title") val title: String,
)
