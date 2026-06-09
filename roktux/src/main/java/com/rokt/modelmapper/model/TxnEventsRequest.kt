package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for `POST /v2/sessions/events` on the Transactions API.
 *
 * Colocated with the v2 offers models ([TxnSelectResponse]) so the whole
 * Transactions v2 contract lives in one place and the shared types ([TxnChannel],
 * [TxnSessionToken]) are defined once. The session is identified solely by the
 * JWT `sub` claim in the `Authorization` header — there is intentionally no
 * `session_id` in the body. Platform/channel context travels in [channel] (the
 * same descriptor used by the offers flow).
 */
@Serializable
data class TxnEventsRequest(
    @SerialName("channel") val channel: TxnChannel,
    @SerialName("events") val events: List<TxnEvent>,
)

/**
 * Channel descriptor sent on Transactions v2 offers and events requests. The
 * backend derives its `source` tag from [type] (`"msdk"` for the mobile SDK);
 * [sdkVersion] identifies the SDK build.
 *
 * Shared by the offers and events flows (it is NOT part of `/v2/sessions/init`,
 * which carries platform identity via `operating_system` instead).
 */
@Serializable
data class TxnChannel(
    @SerialName("type") val type: String = CHANNEL_TYPE_MSDK,
    @SerialName("sdk_version") val sdkVersion: String,
) {
    companion object {
        const val CHANNEL_TYPE_MSDK = "msdk"
    }
}

/**
 * A single Transactions v2 event. Mirrors the provider's `TransactionEvent`:
 *  - [eventType] must resolve to a registered registry type; unknown strings emit
 *    `unknown_event_type` warnings server-side and break attribution.
 *  - [instanceId] is the event's **own** occurrence id (legacy `instanceGuid`).
 *    The trackable entity id and its echo token live in [data] as `parent_id`
 *    and `token` respectively.
 *  - [timestamp] is Unix epoch milliseconds. The provider accepts a JSON number
 *    here (its decoder also accepts an RFC3339 string).
 *  - [data] is an opaque string map carrying `parent_id`, `token`,
 *    `page_instance_guid`, `capture_method`, plus folded attributes/metadata and
 *    type-specific markers (`gated`, `sdk_event`, `interaction_type`).
 */
@Serializable
data class TxnEvent(
    @SerialName("event_type") val eventType: String,
    @SerialName("instance_id") val instanceId: String? = null,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("data") val data: Map<String, String>? = null,
)

/**
 * Response body for `POST /v2/sessions/events`. Only [sessionToken] is consumed —
 * it is rotated forward so the next offers/events call authenticates against the
 * same session. The provider also returns `event_ids`/`errors`/`warnings`, which
 * are ignored here.
 */
@Serializable
data class TxnEventsResponse(@SerialName("session_token") val sessionToken: TxnSessionToken)
