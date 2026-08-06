package com.rokt.modelmapper.model.txn

import com.rokt.modelmapper.model.NetworkLayoutSchemaSerializer
import com.rokt.modelmapper.model.RootSchemaModelSerializer
import com.rokt.network.model.LayoutDisplayPreset
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.LayoutSettings
import com.rokt.network.model.RootSchemaModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Selection response for a `v2/sessions/offers` request — the single canonical,
 * helper-owned response model the renderer consumes. The layout schema fields are
 * parsed into the renderer's typed [RootSchemaModel] / [LayoutSchemaModel] during
 * decode; [ExperienceModelMapperImpl] maps this tree into the renderer's
 * `ExperienceModel`. Only the consumed fields are typed; any other keys are ignored.
 */
@Serializable
data class SelectResponse(
    @SerialName("session_id") val sessionId: String,
    @SerialName("session_token") val sessionToken: SessionToken,
    @SerialName("page_instance_guid") val pageInstanceGuid: String = "",
    @SerialName("page_context") val pageContext: SelectPageContext? = null,
    @SerialName("plugins") val plugins: List<SelectPlugin>? = null,
    @SerialName("event_data") val eventData: Map<String, SelectEventDataEntry>? = null,
)

@Serializable
data class SelectPageContext(
    @SerialName("rokt_tag_id") val roktTagId: String? = null,
    @SerialName("page_instance_guid") val pageInstanceGuid: String? = null,
    @SerialName("page_id") val pageId: String? = null,
    @SerialName("page_type") val pageType: String? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("is_page_detected") val isPageDetected: Boolean? = null,
    @SerialName("page_variant_name") val pageVariantName: String? = null,
    @SerialName("partner_content_template") val partnerContentTemplate: String? = null,
    @SerialName("token") val token: String? = null,
)

@Serializable
data class SelectPlugin(@SerialName("plugin") val plugin: SelectPluginLayout? = null)

@Serializable
data class SelectPluginLayout(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("target_element_selector") val targetElementSelector: String? = null,
    @SerialName("config") val config: SelectPluginConfig? = null,
)

@Serializable
data class SelectPluginConfig(
    @SerialName("slots") val slots: List<SelectSlot> = emptyList(),
    @SerialName("instance_guid") val instanceGuid: String? = null,
    @Serializable(with = RootSchemaModelSerializer::class)
    @SerialName("outer_layout_schema")
    val outerLayoutSchema: RootSchemaModel<LayoutSchemaModel, LayoutDisplayPreset, LayoutSettings>? = null,
    @SerialName("token") val token: String? = null,
)

@Serializable
data class SelectSlot(
    @SerialName("instance_guid") val instanceGuid: String? = null,
    @SerialName("layout_variant") val layoutVariant: SelectLayoutVariant? = null,
    @SerialName("offer") val offer: SelectOffer? = null,
    @SerialName("token") val token: String? = null,
)

@Serializable
data class SelectLayoutVariant(
    @SerialName("layout_variant_id") val layoutVariantId: String? = null,
    @SerialName("module_name") val moduleName: String? = null,
    @Serializable(with = NetworkLayoutSchemaSerializer::class)
    @SerialName("layout_variant_schema")
    val layoutVariantSchema: LayoutSchemaModel? = null,
)

@Serializable
data class SelectOffer(
    @SerialName("campaign_id") val campaignId: String? = null,
    @SerialName("creative") val creative: SelectCreative? = null,
    @SerialName("catalog_items") val catalogItems: List<SelectCatalogItem>? = null,
    @SerialName("catalog_item_group") val catalogItemGroup: SelectCatalogItemGroup? = null,
    @SerialName("transaction_data") val transactionData: SelectTransactionData? = null,
)

/**
 * A shoppable catalog item from an offers selection response. Only the fields the
 * renderer / purchase events consume are modelled; every field is optional and any
 * other keys on the wire are ignored. The mapping into the renderer's
 * `CatalogItemModel` supplies defaults for the keys it requires but the response
 * can omit.
 */
@Serializable
data class SelectCatalogItem(
    @SerialName("catalog_item_id") val catalogItemId: String? = null,
    @SerialName("instance_guid") val instanceGuid: String? = null,
    @SerialName("cart_item_id") val cartItemId: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("price") val price: Double? = null,
    @SerialName("original_price") val originalPrice: Double? = null,
    @SerialName("price_formatted") val priceFormatted: String? = null,
    @SerialName("original_price_formatted") val originalPriceFormatted: String? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("url_behavior") val urlBehavior: String? = null,
    @SerialName("signal_type") val signalType: String? = null,
    @SerialName("min_item_count") val minItemCount: Int? = null,
    @SerialName("max_item_count") val maxItemCount: Int? = null,
    @SerialName("pre_selected_quantity") val preSelectedQuantity: Int? = null,
    @SerialName("provider_data") val providerData: String? = null,
    @SerialName("linked_product_id") val linkedProductId: String? = null,
    @SerialName("quantity_must_be_synchronized") val quantityMustBeSynchronized: Boolean? = null,
    @SerialName("positive_response_text") val positiveResponseText: String? = null,
    @SerialName("negative_response_text") val negativeResponseText: String? = null,
    @SerialName("inventory_status") val inventoryStatus: String? = null,
    @SerialName("copy") val copy: Map<String, String>? = null,
    @SerialName("images") val images: Map<String, SelectImage>? = null,
    @SerialName("token") val token: String? = null,
)

@Serializable
data class SelectCreative(
    @SerialName("referral_creative_id") val referralCreativeId: String? = null,
    @SerialName("instance_guid") val instanceGuid: String? = null,
    @SerialName("token") val token: String? = null,
    @SerialName("response_options_map") val responseOptionsMap: Map<String, SelectResponseOption>? = null,
    @SerialName("copy") val copy: Map<String, String>? = null,
    @SerialName("images") val images: Map<String, SelectImage>? = null,
    @SerialName("links") val links: Map<String, SelectLink>? = null,
    @SerialName("icons") val icons: Map<String, SelectIcon>? = null,
)

@Serializable
data class SelectResponseOption(
    @SerialName("id") val id: String? = null,
    @SerialName("action") val action: String? = null,
    @SerialName("instance_guid") val instanceGuid: String? = null,
    @SerialName("token") val token: String? = null,
    @SerialName("signal_type") val signalType: String? = null,
    @SerialName("short_label") val shortLabel: String? = null,
    @SerialName("long_label") val longLabel: String? = null,
    @SerialName("short_success_label") val shortSuccessLabel: String? = null,
    @SerialName("is_positive") val isPositive: Boolean = false,
    @SerialName("url") val url: String? = null,
    @SerialName("ignore_branch") val ignoreBranch: Boolean? = null,
)

@Serializable
data class SelectImage(
    @SerialName("light") val light: String? = null,
    @SerialName("dark") val dark: String? = null,
    @SerialName("alt") val alt: String? = null,
    @SerialName("title") val title: String? = null,
)

@Serializable
data class SelectLink(@SerialName("url") val url: String? = null, @SerialName("title") val title: String? = null)

@Serializable
data class SelectIcon(@SerialName("name") val name: String? = null)

@Serializable
data class SelectCatalogItemGroup(
    @SerialName("group_id") val groupId: String? = null,
    @SerialName("catalog_item_ids") val catalogItemIds: List<String>? = null,
    @SerialName("attributes") val attributes: List<SelectCatalogItemGroupAttribute>? = null,
    @SerialName("metadata") val metadata: Map<String, String>? = null,
)

@Serializable
data class SelectCatalogItemGroupAttribute(
    @SerialName("attribute_id") val attributeId: String? = null,
    @SerialName("label") val label: String? = null,
    @SerialName("options") val options: List<SelectCatalogItemGroupOption>? = null,
    @SerialName("metadata") val metadata: Map<String, String>? = null,
)

@Serializable
data class SelectCatalogItemGroupOption(
    @SerialName("label") val label: String? = null,
    @SerialName("catalog_item_ids") val catalogItemIds: List<String>? = null,
    @SerialName("metadata") val metadata: Map<String, String>? = null,
)

@Serializable
data class SelectTransactionData(
    @SerialName("shipping_address") val shippingAddress: SelectAddress? = null,
    @SerialName("billing_address") val billingAddress: SelectAddress? = null,
    @SerialName("payment_type") val paymentType: String? = null,
    @SerialName("supported_payment_methods") val supportedPaymentMethods: List<SelectPaymentMethod>? = null,
    @SerialName("is_partner_managed_purchase") val isPartnerManagedPurchase: Boolean? = null,
    @SerialName("partner_payment_reference") val partnerPaymentReference: String? = null,
    @SerialName("confirmation_ref") val confirmationRef: String? = null,
    @SerialName("metadata") val metadata: Map<String, String>? = null,
)

@Serializable
data class SelectAddress(
    @SerialName("name") val name: String? = null,
    @SerialName("address1") val address1: String? = null,
    @SerialName("address2") val address2: String? = null,
    @SerialName("city") val city: String? = null,
    @SerialName("state") val state: String? = null,
    @SerialName("state_code") val stateCode: String? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    @SerialName("zip") val zip: String? = null,
)

@Serializable
data class SelectPaymentMethod(@SerialName("type") val type: String? = null)

@Serializable
data class SelectEventDataEntry(
    @SerialName("token") val token: String,
    @SerialName("events") val events: Map<String, SelectRealTimeEvent>? = null,
)

@Serializable
data class SelectRealTimeEvent(
    @SerialName("event_type") val eventType: String? = null,
    @SerialName("payload") val payload: String? = null,
)
