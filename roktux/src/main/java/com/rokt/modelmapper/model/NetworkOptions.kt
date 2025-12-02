package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkOptions(@SerialName("useDiagnosticEvents") val useDiagnosticEvents: Boolean)
