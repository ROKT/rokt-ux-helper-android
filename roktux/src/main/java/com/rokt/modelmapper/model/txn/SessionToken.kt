package com.rokt.modelmapper.model.txn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Session token returned on the v2 offers and events responses. */
@Serializable
data class SessionToken(@SerialName("token") val token: String, @SerialName("expires_at") val expiresAt: Long)
