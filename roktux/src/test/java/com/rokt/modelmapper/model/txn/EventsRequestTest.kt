package com.rokt.modelmapper.model.txn

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EventsRequestTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `decodes a fully populated events request`() {
        val request = json.decodeFromString<EventsRequest>(FULL_REQUEST_PAYLOAD)

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
        val response = json.decodeFromString<EventsResponse>(RESPONSE_PAYLOAD)

        assertEquals("rotated-token", response.sessionToken.token)
        assertEquals(1_711_038_900_000L, response.sessionToken.expiresAt)
    }

    @Test
    fun `applies the channel type default when omitted`() {
        val request = json.decodeFromString<EventsRequest>(MINIMAL_REQUEST_PAYLOAD)

        assertEquals(Channel.CHANNEL_TYPE_MSDK, request.channel.type)
        assertEquals("9.9.9", request.channel.sdkVersion)
        assertEquals(1, request.events.size)
        assertEquals("placement_ready", request.events.single().eventType)
    }

    @Test
    fun `serializes the default channel type even when encodeDefaults is off`() {
        // The SDK's production Json disables encodeDefaults; @EncodeDefault(ALWAYS)
        // on Channel.type must still force "msdk" onto the wire so the backend
        // keeps the channel source.
        val encoded = Json.encodeToString(Channel(sdkVersion = "1.2.3"))

        assertTrue(encoded, encoded.contains("\"type\":\"msdk\""))
    }

    @Test
    fun `round-trips a request built from the constructors`() {
        val original = EventsRequest(
            channel = Channel(sdkVersion = "4.5.6"),
            events = listOf(
                Event(
                    eventType = "signal_response",
                    instanceId = "instance-3",
                    timestamp = 42L,
                    data = mapOf(
                        "parent_id" to "parent-3",
                        "token" to "token-3",
                        "page_instance_guid" to "page-3",
                    ),
                ),
                Event(eventType = "placement_impression", timestamp = 43L),
            ),
        )

        val decoded = json.decodeFromString<EventsRequest>(json.encodeToString(original))

        assertEquals(original, decoded)
    }

    @Test
    fun `round-trips an events response built from the constructor`() {
        val original = EventsResponse(sessionToken = SessionToken(token = "token-5", expiresAt = 99L))

        val decoded = json.decodeFromString<EventsResponse>(json.encodeToString(original))

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
