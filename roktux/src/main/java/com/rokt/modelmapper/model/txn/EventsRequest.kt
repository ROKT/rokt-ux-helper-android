package com.rokt.modelmapper.model.txn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request body for the v2 events endpoint. The session is identified by the JWT
 * in the `Authorization` header, so there is no `session_id` in the body.
 * Channel context travels in [channel] (the same descriptor used by the offers
 * request).
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
 * A single event entry sent on a request.
 *  - [eventType] is the event name as it appears on the wire (snake_case).
 *  - [instanceId] is this event's own occurrence id.
 *  - [timestamp] is Unix epoch milliseconds.
 *  - [data] is an optional string map of supporting fields (e.g. `parent_id`,
 *    `token`, `page_instance_guid`).
 */
@Serializable
data class Event(
    @SerialName("event_type") val eventType: String,
    @SerialName("instance_id") val instanceId: String? = null,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("data") val data: Map<String, String>? = null,
)

/**
 * Response body for the v2 events endpoint. Only [sessionToken] is read — it is
 * carried forward so the next request authenticates against the same session.
 * Any other fields in the response are ignored.
 */
@Serializable
data class EventsResponse(@SerialName("session_token") val sessionToken: SessionToken)
