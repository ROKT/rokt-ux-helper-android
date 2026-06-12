package com.rokt.modelmapper.model

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NetworkCatalogItemTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `decodes catalog item when backend omits null optional fields`() {
        val catalogItem = json.decodeFromString<NetworkCatalogItem>(
            """
            {
              "images": {},
              "instanceGuid": "catalog-instance-guid",
              "cartItemId": "cart-item",
              "catalogItemId": "catalog-item",
              "title": "Everyday sneakers",
              "price": 79.99,
              "originalPrice": 99.99,
              "originalPriceFormatted": "${'$'}99.99",
              "currency": "USD",
              "signalType": "SignalResponse",
              "minItemCount": 1,
              "maxItemCount": 3,
              "preSelectedQuantity": 1,
              "providerData": "{}",
              "urlBehavior": "External",
              "priceFormatted": "${'$'}79.99",
              "token": "test"
            }
            """.trimIndent(),
        )

        assertThat(catalogItem.description).isEmpty()
        assertThat(catalogItem.url).isEmpty()
        assertThat(catalogItem.linkedProductId).isEmpty()
        assertThat(catalogItem.quantityMustBeSynchronized).isFalse()
        assertThat(catalogItem.positiveResponseText).isEmpty()
        assertThat(catalogItem.negativeResponseText).isEmpty()
        assertThat(catalogItem.addOnPluginUrl).isEmpty()
        assertThat(catalogItem.addOnPluginName).isEmpty()
    }
}
