package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCatalogItem(
    @SerialName("images") val images: Map<String, NetworkCreativeImage>,
    @SerialName("instanceGuid") val instanceGuid: String,
    @SerialName("cartItemId") val cartItemId: String,
    @SerialName("catalogItemId") val catalogItemId: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("price") val price: Double,
    @SerialName("originalPrice") val originalPrice: Double,
    @SerialName("originalPriceFormatted") val originalPriceFormatted: String,
    @SerialName("currency") val currency: String,
    @SerialName("signalType") val signalType: NetworkSignalType,
    @SerialName("url") val url: String,
    @SerialName("minItemCount") val minItemCount: Int,
    @SerialName("maxItemCount") val maxItemCount: Int,
    @SerialName("preSelectedQuantity") val preSelectedQuantity: Int = 0,
    @SerialName("providerData") val providerData: String,
    @SerialName("urlBehavior") val urlBehavior: String,
    @SerialName("linkedProductId") val linkedProductId: String,
    @SerialName("quantityMustBeSynchronized") val quantityMustBeSynchronized: Boolean,
    @SerialName("positiveResponseText") val positiveResponseText: String,
    @SerialName("negativeResponseText") val negativeResponseText: String,
    @SerialName("priceFormatted") val priceFormatted: String,
    @SerialName("addOnPluginUrl") val addOnPluginUrl: String,
    @SerialName("addOnPluginName") val addOnPluginName: String,
    @SerialName("token") val token: String,
)
