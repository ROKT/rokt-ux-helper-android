package com.rokt.roktux.event

import com.rokt.roktux.RoktIntegrationConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoktPlatformEventTest {

    @Test
    fun `wrapper toJsonString emits the v2 sessions events body`() {
        val wrapper = RoktPlatformEventsWrapper(
            integration = emptyIntegration,
            events = listOf(
                RoktPlatformEvent(
                    eventType = EventType.SignalImpression,
                    sessionId = "session-id",
                    parentGuid = "slot-instance-guid",
                    token = "slot-token",
                    pageInstanceGuid = "page-instance-guid",
                ),
            ),
        )

        val body = Json.parseToJsonElement(wrapper.toJsonString()).jsonObject
        assertEquals("s2s", body.getValue("channel").jsonObject.getValue("type").jsonPrimitive.content)
        // single_session pins the batch to the synchronous events path (one session per batch).
        assertEquals(true, body.getValue("single_session").jsonPrimitive.content.toBoolean())

        val event = body.getValue("events").jsonArray.single().jsonObject
        // Legacy enum is translated to the registry string.
        assertEquals("impression", event.getValue("event_type").jsonPrimitive.content)
        assertTrue(event.getValue("instance_id").jsonPrimitive.content.isNotEmpty())
        // session_id is a top-level canonical envelope field, not part of data.
        assertEquals("session-id", event.getValue("session_id").jsonPrimitive.content)

        val data = event.getValue("data").jsonObject
        assertEquals("slot-token", data.getValue("token").jsonPrimitive.content)
        // parent_id is the bare instance_guid.
        assertEquals("slot-instance-guid", data.getValue("parent_id").jsonPrimitive.content)
        assertEquals("page-instance-guid", data.getValue("page_instance_guid").jsonPrimitive.content)
        assertEquals("ClientProvided", data.getValue("capture_method").jsonPrimitive.content)
    }

    private val emptyIntegration = RoktIntegrationConfig(
        name = "",
        version = "",
        framework = "",
        platform = "",
        layoutSchemaVersion = "",
        packageVersion = "",
        packageName = "",
        operatingSystem = "",
        operatingSystemVersion = "",
        deviceLocale = "",
        deviceType = "",
        deviceModel = "",
        metadata = emptyMap(),
    )

    @Test
    fun `RoktPlatformEvent serializes objectData`() {
        val event = RoktPlatformEvent(
            eventType = EventType.SignalUserInteraction,
            sessionId = "session-id",
            parentGuid = "catalog-instance-guid",
            objectData = mapOf(
                "action" to "ThumbnailClick",
                "context" to "CatalogImageGallery",
            ),
        )

        val json = Json.parseToJsonElement(event.toJsonString()).jsonObject
        val objectData = json.getValue("objectData").jsonObject

        assertEquals("ThumbnailClick", objectData.getValue("action").jsonPrimitive.content)
        assertEquals("CatalogImageGallery", objectData.getValue("context").jsonPrimitive.content)
    }

    @Test
    fun `RoktPlatformEvent serializes token`() {
        val event = RoktPlatformEvent(
            eventType = EventType.SignalImpression,
            sessionId = "session-id",
            parentGuid = "slot-instance-guid",
            token = "slot-token",
        )

        val json = Json.parseToJsonElement(event.toJsonString()).jsonObject

        assertEquals("slot-token", json.getValue("token").jsonPrimitive.content)
    }
}
