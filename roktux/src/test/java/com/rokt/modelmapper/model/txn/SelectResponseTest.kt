package com.rokt.modelmapper.model.txn

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `decodes a fully populated selection response`() {
        val response = json.decodeFromString<SelectResponse>(FULL_PAYLOAD)

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
        // Typed layout schemas are parsed by the shared schema serializers (covered by
        // the render-model tests); this fixture omits them, so they decode as null.
        assertNull(config.outerLayoutSchema)

        val slot = config.slots.single()
        assertEquals("slot-1", slot.instanceGuid)
        assertEquals("variant-1", slot.layoutVariant?.layoutVariantId)
        assertEquals("module-1", slot.layoutVariant?.moduleName)
        assertNull(slot.layoutVariant?.layoutVariantSchema)

        val offer = slot.offer!!
        assertEquals("campaign-1", offer.campaignId)
        assertEquals(1, offer.catalogItems?.size)

        // Only `instance_guid` and `title` are guaranteed; campaign-specific
        // fields vary by campaign type and round-trip via `raw`.
        val catalogItem = offer.catalogItems!!.single()
        assertEquals("catalog-instance-1", catalogItem.instanceGuid)
        assertEquals("Catalog title", catalogItem.title)
        assertEquals(9.99, catalogItem.raw["price"]?.jsonPrimitive?.double)
        assertEquals("varies-by-campaign", catalogItem.raw["custom_field"]?.jsonPrimitive?.content)

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
        val response = json.decodeFromString<SelectResponse>(MINIMAL_PAYLOAD)

        assertEquals("session-2", response.sessionId)
        assertEquals("", response.pageInstanceGuid)
        assertNull(response.pageContext)
        assertNull(response.plugins)
        assertNull(response.eventData)
    }

    @Test
    fun `decodes a catalog item when the guaranteed fields are absent`() {
        // The catalog-item shape is open and campaign-specific; only `instance_guid`
        // and `title` are guaranteed. Decoding must not fail when they are absent —
        // the typed accessors are null and the payload is retained in `raw`.
        val catalogItem = json.decodeFromString<SelectCatalogItem>(
            """{ "campaign_only_field": 7, "nested": { "k": "v" } }""",
        )

        assertNull(catalogItem.instanceGuid)
        assertNull(catalogItem.title)
        assertEquals(7, catalogItem.raw["campaign_only_field"]?.jsonPrimitive?.int)
        assertEquals("v", catalogItem.raw["nested"]?.jsonObject?.get("k")?.jsonPrimitive?.content)
    }

    @Test
    fun `catalog item narrows a non-string guaranteed field to null but retains it in raw`() {
        // A guaranteed field arriving as a non-string is a server contract violation.
        // Decoding deliberately tolerates it: the typed accessor narrows to null (the
        // narrowing is lossy and pinned here on purpose), while the original value is
        // always preserved untyped in `raw`.
        val catalogItem = json.decodeFromString<SelectCatalogItem>(
            """{ "instance_guid": 12345, "title": true }""",
        )

        assertNull(catalogItem.instanceGuid)
        assertNull(catalogItem.title)
        assertEquals(12345, catalogItem.raw["instance_guid"]?.jsonPrimitive?.int)
        assertEquals(true, catalogItem.raw["title"]?.jsonPrimitive?.boolean)
    }

    @Test
    fun `offer skips non-object catalog items without failing the decode`() {
        // `catalog_items` is open; a non-object element (string, number, null, array)
        // must not fail the whole response decode. Only object-shaped elements become
        // typed items; everything else is skipped.
        val offer = json.decodeFromString<SelectOffer>(
            """
            {
              "campaign_id": "campaign-x",
              "catalog_items": [
                { "instance_guid": "keep-1", "title": "Kept" },
                "bare-string",
                42,
                null,
                ["nested", "array"],
                { "instance_guid": "keep-2" }
              ]
            }
            """.trimIndent(),
        )

        assertEquals(2, offer.catalogItems?.size)
        assertEquals(listOf("keep-1", "keep-2"), offer.catalogItems!!.map { it.instanceGuid })
    }

    @Test
    fun `offer distinguishes an empty catalog items array from an absent field`() {
        // Absent key -> null; present-but-empty array -> empty (non-null) list.
        val absentOffer = json.decodeFromString<SelectOffer>(
            """{ "campaign_id": "campaign-absent" }""",
        )
        val emptyOffer = json.decodeFromString<SelectOffer>(
            """{ "campaign_id": "campaign-empty", "catalog_items": [] }""",
        )

        assertNull(absentOffer.catalogItems)
        assertTrue(emptyOffer.catalogItems!!.isEmpty())
    }

    @Test
    fun `round-trips a model built from the constructors`() {
        val original = SelectResponse(
            sessionId = "session-3",
            sessionToken = SessionToken(token = "token-3", expiresAt = 42L),
            pageInstanceGuid = "page-instance-3",
            pageContext = SelectPageContext(
                pageInstanceGuid = "page-instance-3",
                pageId = "page-3",
                language = "en",
                token = "ctx-token",
            ),
            plugins = listOf(
                SelectPlugin(
                    plugin = SelectPluginLayout(
                        id = "plugin-3",
                        name = "Layout",
                        targetElementSelector = "#target",
                        config = SelectPluginConfig(
                            slots = listOf(
                                SelectSlot(
                                    instanceGuid = "slot-3",
                                    token = "slot-token",
                                    layoutVariant = SelectLayoutVariant(
                                        layoutVariantId = "variant-3",
                                        moduleName = "module-3",
                                    ),
                                    offer = SelectOffer(
                                        campaignId = "campaign-3",
                                        catalogItems = listOf(
                                            SelectCatalogItem(
                                                raw = buildJsonObject {
                                                    put("instance_guid", "catalog-instance-3")
                                                    put("title", "Catalog title")
                                                    put("price", 9.99)
                                                },
                                            ),
                                        ),
                                        creative = SelectCreative(
                                            referralCreativeId = "creative-3",
                                            instanceGuid = "creative-instance-3",
                                            token = "creative-token",
                                            copy = mapOf("title" to "Hello"),
                                            images = mapOf(
                                                "hero" to SelectImage(
                                                    light = "https://example.com/light.png",
                                                    dark = "https://example.com/dark.png",
                                                    alt = "alt",
                                                    title = "title",
                                                ),
                                            ),
                                            links = mapOf("privacy" to SelectLink(url = "https://example.com/privacy", title = "Privacy")),
                                            icons = mapOf("close" to SelectIcon(name = "close")),
                                            responseOptionsMap = mapOf(
                                                "positive" to SelectResponseOption(
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
                            token = "config-token",
                        ),
                    ),
                ),
            ),
            eventData = mapOf(
                "entity-3" to SelectEventDataEntry(token = "event-token", events = mapOf("impression" to buildJsonObject { })),
            ),
        )

        val decoded = json.decodeFromString<SelectResponse>(json.encodeToString(original))

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
                      "token": "config-token",
                      "slots": [
                        {
                          "instance_guid": "slot-1",
                          "token": "slot-token",
                          "layout_variant": { "layout_variant_id": "variant-1", "module_name": "module-1" },
                          "offer": {
                            "campaign_id": "campaign-1",
                            "catalog_items": [ { "instance_guid": "catalog-instance-1", "title": "Catalog title", "price": 9.99, "custom_field": "varies-by-campaign" } ],
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
