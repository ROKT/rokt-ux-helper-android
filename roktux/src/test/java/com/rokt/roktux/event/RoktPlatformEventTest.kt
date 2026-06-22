package com.rokt.roktux.event

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

class RoktPlatformEventTest {

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
