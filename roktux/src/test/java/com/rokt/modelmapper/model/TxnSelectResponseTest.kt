package com.rokt.modelmapper.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TxnSelectResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `decodes a fully populated selection response`() {
        val response = json.decodeFromString<TxnSelectResponse>(FULL_PAYLOAD)

        assertEquals("session-1", response.sessionId)
        assertEquals("token-1", response.sessionToken.token)
        assertEquals(1_711_038_600_000L, response.sessionToken.expiresAt)
        assertEquals("page-instance-1", response.pageInstanceGuid)
        assertEquals("page-1", response.pageContext?.pageId)
        assertEquals("en", response.pageContext?.language)

        val plugin = response.plugins!!.single().plugin!!
        assertEquals("plugin-1", plugin.id)
        assertEquals("Layout", plugin.name)
        assertEquals("#target", plugin.targetElementSelector)

        val config = plugin.config!!
        assertEquals("plugin-instance-1", config.instanceGuid)
        // Layout schemas are passed through as raw JSON strings.
        assertEquals("{\"key\":\"value\"}", config.outerLayoutSchema)

        val slot = config.slots.single()
        assertEquals("slot-1", slot.instanceGuid)
        assertEquals("variant-1", slot.layoutVariant?.layoutVariantId)
        assertEquals("module-1", slot.layoutVariant?.moduleName)
        assertEquals("{\"variant\":true}", slot.layoutVariant?.layoutVariantSchema)

        val offer = slot.offer!!
        assertEquals("campaign-1", offer.campaignId)
        assertEquals(1, offer.catalogItems?.size)

        val creative = offer.creative!!
        assertEquals("creative-1", creative.referralCreativeId)
        assertEquals("Hello", creative.copy?.get("title"))
        assertEquals("https://example.com/light.png", creative.images?.get("hero")?.light)
        assertEquals("https://example.com/dark.png", creative.images?.get("hero")?.dark)
        assertEquals("https://example.com/privacy", creative.links?.get("privacy")?.url)
        assertEquals("close", creative.icons?.get("close")?.name)

        val responseOption = creative.responseOptionsMap!!.getValue("positive")
        assertEquals("url", responseOption.action)
        assertEquals("signal-response", responseOption.signalType)
        assertEquals("Yes", responseOption.shortLabel)
        assertEquals("Yes please", responseOption.longLabel)
        assertEquals("Done", responseOption.shortSuccessLabel)
        assertTrue(responseOption.isPositive)
        assertEquals(false, responseOption.ignoreBranch)

        assertEquals("event-token", response.eventData?.getValue("entity-1")?.token)
    }

    @Test
    fun `applies defaults and nulls when optional fields are omitted`() {
        val response = json.decodeFromString<TxnSelectResponse>(MINIMAL_PAYLOAD)

        assertEquals("session-2", response.sessionId)
        assertEquals("", response.pageInstanceGuid)
        assertNull(response.pageContext)
        assertNull(response.plugins)
        assertNull(response.eventData)
    }

    @Test
    fun `round-trips a model built from the constructors`() {
        val original = TxnSelectResponse(
            sessionId = "session-3",
            sessionToken = TxnSessionToken(token = "token-3", expiresAt = 42L),
            pageInstanceGuid = "page-instance-3",
            pageContext = TxnSelectPageContext(
                pageInstanceGuid = "page-instance-3",
                pageId = "page-3",
                language = "en",
                token = "ctx-token",
            ),
            plugins = listOf(
                TxnSelectPlugin(
                    plugin = TxnSelectPluginLayout(
                        id = "plugin-3",
                        name = "Layout",
                        targetElementSelector = "#target",
                        config = TxnSelectPluginConfig(
                            slots = listOf(
                                TxnSelectSlot(
                                    instanceGuid = "slot-3",
                                    token = "slot-token",
                                    layoutVariant = TxnSelectLayoutVariant(
                                        layoutVariantId = "variant-3",
                                        moduleName = "module-3",
                                        layoutVariantSchema = "{\"variant\":true}",
                                    ),
                                    offer = TxnSelectOffer(
                                        campaignId = "campaign-3",
                                        catalogItems = listOf(buildJsonObject { put("id", "catalog-3") }),
                                        creative = TxnSelectCreative(
                                            referralCreativeId = "creative-3",
                                            instanceGuid = "creative-instance-3",
                                            token = "creative-token",
                                            copy = mapOf("title" to "Hello"),
                                            images = mapOf(
                                                "hero" to TxnSelectImage(
                                                    light = "https://example.com/light.png",
                                                    dark = "https://example.com/dark.png",
                                                    alt = "alt",
                                                    title = "title",
                                                ),
                                            ),
                                            links = mapOf("privacy" to TxnSelectLink(url = "https://example.com/privacy", title = "Privacy")),
                                            icons = mapOf("close" to TxnSelectIcon(name = "close")),
                                            responseOptionsMap = mapOf(
                                                "positive" to TxnSelectResponseOption(
                                                    id = "ro-3",
                                                    action = "url",
                                                    instanceGuid = "ro-instance-3",
                                                    token = "ro-token",
                                                    signalType = "signal-response",
                                                    shortLabel = "Yes",
                                                    longLabel = "Yes please",
                                                    shortSuccessLabel = "Done",
                                                    isPositive = true,
                                                    url = "https://example.com/accept",
                                                    ignoreBranch = false,
                                                ),
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                            instanceGuid = "plugin-instance-3",
                            outerLayoutSchema = "{\"key\":\"value\"}",
                            token = "config-token",
                        ),
                    ),
                ),
            ),
            eventData = mapOf(
                "entity-3" to TxnSelectEventDataEntry(token = "event-token", events = mapOf("impression" to buildJsonObject { })),
            ),
        )

        val decoded = json.decodeFromString<TxnSelectResponse>(json.encodeToString(original))

        assertEquals(original, decoded)
    }

    private companion object {
        val FULL_PAYLOAD = """
            {
              "session_id": "session-1",
              "session_token": { "token": "token-1", "expires_at": 1711038600000 },
              "page_instance_guid": "page-instance-1",
              "page_context": { "page_instance_guid": "page-instance-1", "page_id": "page-1", "language": "en", "token": "ctx-token" },
              "plugins": [
                {
                  "plugin": {
                    "id": "plugin-1",
                    "name": "Layout",
                    "target_element_selector": "#target",
                    "config": {
                      "instance_guid": "plugin-instance-1",
                      "outer_layout_schema": "{\"key\":\"value\"}",
                      "token": "config-token",
                      "slots": [
                        {
                          "instance_guid": "slot-1",
                          "token": "slot-token",
                          "layout_variant": { "layout_variant_id": "variant-1", "module_name": "module-1", "layout_variant_schema": "{\"variant\":true}" },
                          "offer": {
                            "campaign_id": "campaign-1",
                            "catalog_items": [ { "id": "catalog-1" } ],
                            "creative": {
                              "referral_creative_id": "creative-1",
                              "instance_guid": "creative-instance-1",
                              "token": "creative-token",
                              "copy": { "title": "Hello" },
                              "images": { "hero": { "light": "https://example.com/light.png", "dark": "https://example.com/dark.png", "alt": "alt", "title": "title" } },
                              "links": { "privacy": { "url": "https://example.com/privacy", "title": "Privacy" } },
                              "icons": { "close": { "name": "close" } },
                              "response_options_map": {
                                "positive": {
                                  "id": "ro-1",
                                  "action": "url",
                                  "instance_guid": "ro-instance-1",
                                  "token": "ro-token",
                                  "signal_type": "signal-response",
                                  "short_label": "Yes",
                                  "long_label": "Yes please",
                                  "short_success_label": "Done",
                                  "is_positive": true,
                                  "url": "https://example.com/accept",
                                  "ignore_branch": false
                                }
                              }
                            }
                          }
                        }
                      ]
                    }
                  }
                }
              ],
              "event_data": { "entity-1": { "token": "event-token", "events": { "impression": {} } } }
            }
        """.trimIndent()

        val MINIMAL_PAYLOAD = """
            { "session_id": "session-2", "session_token": { "token": "token-2", "expires_at": 0 } }
        """.trimIndent()
    }
}
