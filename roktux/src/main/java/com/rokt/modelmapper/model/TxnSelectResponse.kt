package com.rokt.modelmapper.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * Selection response for a v2 offers request — the model the renderer consumes,
 * alongside the v1 [NetworkExperienceResponse]. The layout schema fields arrive
 * as JSON strings and are passed through as-is.
 */
@Serializable
data class TxnSelectResponse(
    @SerialName("session_id") val sessionId: String,
    @SerialName("session_token") val sessionToken: TxnSessionToken,
    @SerialName("page_instance_guid") val pageInstanceGuid: String = "",
    @SerialName("page_context") val pageContext: TxnSelectPageContext? = null,
    @SerialName("plugins") val plugins: List<TxnSelectPlugin>? = null,
    @SerialName("event_data") val eventData: Map<String, TxnSelectEventDataEntry>? = null,
)

@Serializable
data class TxnSessionToken(@SerialName("token") val token: String, @SerialName("expires_at") val expiresAt: Long)

@Serializable
data class TxnSelectPageContext(
    @SerialName("page_instance_guid") val pageInstanceGuid: String? = null,
    @SerialName("page_id") val pageId: String? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("token") val token: String? = null,
)

@Serializable
data class TxnSelectPlugin(@SerialName("plugin") val plugin: TxnSelectPluginLayout? = null)

@Serializable
data class TxnSelectPluginLayout(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("target_element_selector") val targetElementSelector: String? = null,
    @SerialName("config") val config: TxnSelectPluginConfig? = null,
)

@Serializable
data class TxnSelectPluginConfig(
    @SerialName("slots") val slots: List<TxnSelectSlot> = emptyList(),
    @SerialName("instance_guid") val instanceGuid: String? = null,
    @SerialName("outer_layout_schema") val outerLayoutSchema: String? = null,
    @SerialName("token") val token: String? = null,
)

@Serializable
data class TxnSelectSlot(
    @SerialName("instance_guid") val instanceGuid: String? = null,
    @SerialName("layout_variant") val layoutVariant: TxnSelectLayoutVariant? = null,
    @SerialName("offer") val offer: TxnSelectOffer? = null,
    @SerialName("token") val token: String? = null,
)

@Serializable
data class TxnSelectLayoutVariant(
    @SerialName("layout_variant_id") val layoutVariantId: String? = null,
    @SerialName("module_name") val moduleName: String? = null,
    @SerialName("layout_variant_schema") val layoutVariantSchema: String? = null,
)

@Serializable
data class TxnSelectOffer(
    @SerialName("campaign_id") val campaignId: String? = null,
    @SerialName("creative") val creative: TxnSelectCreative? = null,
    @SerialName("catalog_items") val catalogItems: List<JsonObject>? = null,
)

@Serializable
data class TxnSelectCreative(
    @SerialName("referral_creative_id") val referralCreativeId: String? = null,
    @SerialName("instance_guid") val instanceGuid: String? = null,
    @SerialName("token") val token: String? = null,
    @SerialName("response_options_map") val responseOptionsMap: Map<String, TxnSelectResponseOption>? = null,
    @SerialName("copy") val copy: Map<String, String>? = null,
    @SerialName("images") val images: Map<String, TxnSelectImage>? = null,
    @SerialName("links") val links: Map<String, TxnSelectLink>? = null,
    @SerialName("icons") val icons: Map<String, TxnSelectIcon>? = null,
)

@Serializable
data class TxnSelectResponseOption(
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
data class TxnSelectImage(
    @SerialName("light") val light: String? = null,
    @SerialName("dark") val dark: String? = null,
    @SerialName("alt") val alt: String? = null,
    @SerialName("title") val title: String? = null,
)

@Serializable
data class TxnSelectLink(@SerialName("url") val url: String? = null, @SerialName("title") val title: String? = null)

@Serializable
data class TxnSelectIcon(@SerialName("name") val name: String? = null)

@Serializable
data class TxnSelectEventDataEntry(
    @SerialName("token") val token: String,
    @SerialName("events") val events: Map<String, JsonElement>? = null,
)
