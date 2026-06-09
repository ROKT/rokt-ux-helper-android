package com.rokt.modelmapper.model.txn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for `POST /v2/sessions/events` on the Transactions API.
 *
 * The session is identified solely by the JWT `sub` claim in the `Authorization`
 * header — there is intentionally no `session_id` in the body. Platform/channel
 * context travels in [channel] (the same descriptor used by the offers flow).
 *
 * Naming convention: types in the `...model.txn` package carry no `Txn`/`V2`
 * prefix — the package namespaces them. This grouping is transitional; once the
 * v1 models are retired these become the standard models and the `txn` package
 * can be flattened. Class/package names do not affect the wire (driven by
 * [SerialName]).
 */
@Serializable
data class EventsRequest(@SerialName("channel") val channel: Channel, @SerialName("events") val events: List<Event>)

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
data class Event(
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
data class EventsResponse(@SerialName("session_token") val sessionToken: SessionToken)
