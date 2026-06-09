package com.rokt.modelmapper.model.txn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Channel descriptor sent on Transactions v2 offers and events requests. The
 * backend derives its `source` tag from [type] (`"msdk"` for the mobile SDK);
 * [sdkVersion] identifies the SDK build.
 *
 * Shared by the offers and events flows (it is NOT part of `/v2/sessions/init`,
 * which carries platform identity via `operating_system` instead).
 */
@Serializable
data class Channel(
    @SerialName("type") val type: String = CHANNEL_TYPE_MSDK,
    @SerialName("sdk_version") val sdkVersion: String,
) {
    companion object {
        const val CHANNEL_TYPE_MSDK = "msdk"
    }
}
