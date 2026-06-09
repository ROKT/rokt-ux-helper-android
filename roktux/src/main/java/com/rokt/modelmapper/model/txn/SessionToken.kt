package com.rokt.modelmapper.model.txn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * JWT session token returned by the Transactions v2 endpoints. Shared by the
 * init / offers / events flows; the [token] is rotated forward on each call and
 * [expiresAt] is Unix epoch milliseconds.
 */
@Serializable
data class SessionToken(
    @SerialName("token") val token: String,
    @SerialName("expires_at") val expiresAt: Long,
)
