package com.rokt.modelmapper.mappers

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.modelmapper.data.DataBindingImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies [ExperienceModelMapperImpl] re-homes the snake_case [SelectResponse] into
 * the renderer's `ExperienceModel` with no field loss — including catalog,
 * response-option, transaction and catalog-group data the bundled fixtures don't
 * exercise.
 */
@RunWith(AndroidJUnit4::class)
class SelectResponseMapperTest {

    private fun map(json: String) = ExperienceModelMapperImpl(json, DataBindingImpl()).transformResponse().getOrThrow()

    @Test
    fun `maps full response into the experience model`() {
        val model = map(FULL_PAYLOAD)

        assertEquals("session-1", model.sessionId)
        assertEquals("ctx-token", model.token)
        assertEquals("page-1", model.pageId)
        assertEquals("page-instance-1", model.placementContext.pageInstanceGuid)
        // v2 carries no options; diagnostic events default on.
        assertTrue(model.options.useDiagnosticEvents)

        val plugin = model.plugins.single()
        assertEquals("plugin-1", plugin.id)
        assertEquals("#target", plugin.targetElementSelector)
        assertEquals("plugin-instance-1", plugin.instanceGuid)
        assertEquals("config-token", plugin.token)

        val offer = plugin.slots.single().offer
        assertNotNull(offer)
        assertEquals("campaign-1", offer!!.campaignId)
        assertEquals("creative-1", offer.creative.referralCreativeId)
        assertEquals("Hello", offer.creative.copy["title"])
        // Android keeps the response-option map keyed as sent (no positive/negative bucketing).
        assertEquals(setOf("a_key", "b_key"), offer.creative.responseOptions.keys)
    }

    @Test
    fun `maps catalog item copy and fields`() {
        val item = map(FULL_PAYLOAD).plugins.single().slots.single().offer!!.catalogItems.single()
        assertEquals("Save 20%", item.copy["provider.discountLabel"])
    }

    @Test
    fun `maps transaction data`() {
        val txn = map(FULL_PAYLOAD).plugins.single().slots.single().offer!!.transactionData
        assertNotNull(txn)
        assertEquals("CARD", txn!!.paymentType)
        assertTrue(txn.isPartnerManagedPurchase)
        assertEquals("ref-1", txn.partnerPaymentReference)
        assertEquals("1 Main St", txn.shippingAddress?.address1)
        assertEquals("NY", txn.shippingAddress?.stateCode)
        assertEquals(listOf("CARD", "APPLE_PAY"), txn.supportedPaymentMethods?.map { it.type })
        assertEquals("v", txn.metadata["k"])
    }

    @Test
    fun `maps catalog item group`() {
        val group = map(FULL_PAYLOAD).plugins.single().slots.single().offer!!.catalogItemGroup
        assertNotNull(group)
        assertEquals("group-1", group!!.groupId)
        assertEquals(listOf("cat-1", "cat-2"), group.catalogItemIds)
        assertEquals("size", group.attributes.single().attributeId)
        assertEquals("Small", group.attributes.single().options.single().label)
    }

    @Test
    fun `drops offer that has no creative`() {
        val offer = map(NO_CREATIVE_PAYLOAD).plugins.single().slots.single().offer
        assertNull(offer)
    }

    private companion object {
        val FULL_PAYLOAD = """
        {
          "session_id": "session-1",
          "session_token": { "token": "session-token", "expires_at": 0 },
          "page_instance_guid": "page-instance-1",
          "page_context": { "page_id": "page-1", "page_instance_guid": "page-instance-1", "token": "ctx-token" },
          "plugins": [
            {
              "plugin": {
                "id": "plugin-1",
                "target_element_selector": "#target",
                "config": {
                  "instance_guid": "plugin-instance-1",
                  "token": "config-token",
                  "slots": [
                    {
                      "instance_guid": "slot-1",
                      "offer": {
                        "campaign_id": "campaign-1",
                        "creative": {
                          "referral_creative_id": "creative-1",
                          "copy": { "title": "Hello" },
                          "response_options_map": {
                            "a_key": { "id": "ro-pos", "is_positive": true, "action": "Url", "signal_type": "SignalResponse" },
                            "b_key": { "id": "ro-neg", "is_positive": false, "action": "CaptureOnly" }
                          }
                        },
                        "catalog_items": [
                          { "catalog_item_id": "cat-1", "title": "Item", "price": 14.99, "currency": "USD", "copy": { "provider.discountLabel": "Save 20%" } }
                        ],
                        "catalog_item_group": {
                          "group_id": "group-1",
                          "catalog_item_ids": ["cat-1", "cat-2"],
                          "attributes": [ { "attribute_id": "size", "label": "Size", "options": [ { "label": "Small", "catalog_item_ids": ["cat-1"] } ] } ]
                        },
                        "transaction_data": {
                          "payment_type": "CARD",
                          "is_partner_managed_purchase": true,
                          "partner_payment_reference": "ref-1",
                          "shipping_address": { "name": "Jane", "address1": "1 Main St", "state_code": "NY" },
                          "supported_payment_methods": [ { "type": "CARD" }, { "type": "APPLE_PAY" } ],
                          "metadata": { "k": "v" }
                        }
                      }
                    }
                  ]
                }
              }
            }
          ]
        }
        """.trimIndent()

        val NO_CREATIVE_PAYLOAD = """
        {
          "session_id": "s",
          "session_token": { "token": "t", "expires_at": 0 },
          "plugins": [
            { "plugin": { "id": "p", "config": { "instance_guid": "g", "slots": [ { "instance_guid": "slot", "offer": { "campaign_id": "c" } } ] } } }
          ]
        }
        """.trimIndent()
    }
}
