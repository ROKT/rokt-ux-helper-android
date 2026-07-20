package com.rokt.modelmapper.model.txn

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Channel descriptor sent on the v2 offers and events requests. [type]
 * identifies the integration channel (`"msdk"` for the mobile SDK) and
 * [sdkVersion] identifies the SDK build.
 *
 * [type] is `@EncodeDefault(ALWAYS)` so its default value is always written to
 * the wire even when the serializing `Json` has `encodeDefaults` disabled.
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
