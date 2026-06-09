package com.rokt.modelmapper.model.txn

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Channel descriptor sent on Transactions v2 offers and events requests. The
 * backend derives its `source` tag from [type] (`"msdk"` for the mobile SDK);
 * [sdkVersion] identifies the SDK build.
 *
 * Shared by the offers and events flows (it is NOT part of `/v2/sessions/init`,
 * which carries platform identity via `operating_system` instead).
 *
 * [type] is `@EncodeDefault(ALWAYS)` because the SDK's production `Json`
 * disables `encodeDefaults`; without it the default `"msdk"` would be dropped
 * from the body and the backend would lose the channel source (there are no
 * longer any `rokt-platform-type`/`rokt-integration-type` headers carrying it).
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Channel(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    @SerialName("type") val type: String = CHANNEL_TYPE_MSDK,
    @SerialName("sdk_version") val sdkVersion: String,
) {
    companion object {
        const val CHANNEL_TYPE_MSDK = "msdk"
    }
}
