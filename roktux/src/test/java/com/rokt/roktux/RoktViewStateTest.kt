package com.rokt.roktux

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RoktViewStateTest {

    @Test
    fun `decodes old view state without domain states as empty map`() {
        val viewState = Json.decodeFromString<RoktViewState>(
            """
            {
              "pluginId": "plugin-id",
              "customStates": {},
              "offerCustomStates": {},
              "offerIndex": 0,
              "pluginDismissed": false
            }
            """.trimIndent(),
        )

        assertThat(viewState.domainStates).isEmpty()
    }

    @Test
    fun `encodes and decodes domain states`() {
        val viewState = RoktViewState(
            pluginId = "plugin-id",
            customStates = emptyMap(),
            offerCustomStates = emptyMap(),
            domainStates = mapOf("checkout" to 2),
            offerIndex = 0,
            pluginDismissed = false,
        )

        val decoded = Json.decodeFromString<RoktViewState>(Json.encodeToString(viewState))

        assertThat(decoded.domainStates).containsEntry("checkout", 2)
    }
}
