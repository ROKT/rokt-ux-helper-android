package com.rokt.modelmapper.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TxnEventsRequestTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `decodes a fully populated events request`() {
        val request = json.decodeFromString<TxnEventsRequest>(FULL_REQUEST_PAYLOAD)

        assertEquals("msdk", request.channel.type)
        assertEquals("1.2.3", request.channel.sdkVersion)
        assertEquals(2, request.events.size)

        val signal = request.events.first()
        assertEquals("signal_response", signal.eventType)
        assertEquals("instance-1", signal.instanceId)
        assertEquals(1_711_038_600_000L, signal.timestamp)
        assertEquals("parent-1", signal.data?.get("parent_id"))
        assertEquals("token-1", signal.data?.get("token"))

        val impression = request.events[1]
        assertEquals("placement_impression", impression.eventType)
        assertNull(impression.instanceId)
        assertNull(impression.data)
    }

    @Test
    fun `decodes the events response and exposes the rotated session token`() {
        val response = json.decodeFromString<TxnEventsResponse>(RESPONSE_PAYLOAD)

        assertEquals("rotated-token", response.sessionToken.token)
        assertEquals(1_711_038_900_000L, response.sessionToken.expiresAt)
    }

    @Test
    fun `applies the channel type default when omitted`() {
        val request = json.decodeFromString<TxnEventsRequest>(MINIMAL_REQUEST_PAYLOAD)

        assertEquals(TxnChannel.CHANNEL_TYPE_MSDK, request.channel.type)
        assertEquals("9.9.9", request.channel.sdkVersion)
        assertEquals(1, request.events.size)
        assertEquals("placement_ready", request.events.single().eventType)
    }

    @Test
    fun `round-trips a request built from the constructors`() {
        val original = TxnEventsRequest(
            channel = TxnChannel(sdkVersion = "4.5.6"),
            events = listOf(
                TxnEvent(
                    eventType = "signal_response",
                    instanceId = "instance-3",
                    timestamp = 42L,
                    data = mapOf(
                        "parent_id" to "parent-3",
                        "token" to "token-3",
                        "page_instance_guid" to "page-3",
                    ),
                ),
                TxnEvent(eventType = "placement_impression", timestamp = 43L),
            ),
        )

        val decoded = json.decodeFromString<TxnEventsRequest>(json.encodeToString(original))

        assertEquals(original, decoded)
    }

    @Test
    fun `round-trips an events response built from the constructor`() {
        val original = TxnEventsResponse(sessionToken = TxnSessionToken(token = "token-5", expiresAt = 99L))

        val decoded = json.decodeFromString<TxnEventsResponse>(json.encodeToString(original))

        assertEquals(original, decoded)
    }

    private companion object {
        val FULL_REQUEST_PAYLOAD = """
            {
              "channel": { "type": "msdk", "sdk_version": "1.2.3" },
              "events": [
                {
                  "event_type": "signal_response",
                  "instance_id": "instance-1",
                  "timestamp": 1711038600000,
                  "data": { "parent_id": "parent-1", "token": "token-1", "page_instance_guid": "page-1" }
                },
                { "event_type": "placement_impression", "timestamp": 1711038600500 }
              ]
            }
        """.trimIndent()

        val MINIMAL_REQUEST_PAYLOAD = """
            {
              "channel": { "sdk_version": "9.9.9" },
              "events": [ { "event_type": "placement_ready", "timestamp": 0 } ]
            }
        """.trimIndent()

        val RESPONSE_PAYLOAD = """
            { "session_token": { "token": "rotated-token", "expires_at": 1711038900000 } }
        """.trimIndent()
    }
}
