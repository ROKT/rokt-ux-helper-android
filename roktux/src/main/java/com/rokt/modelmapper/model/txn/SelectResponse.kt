package com.rokt.modelmapper.model.txn

import com.rokt.modelmapper.model.NetworkLayoutSchemaSerializer
import com.rokt.modelmapper.model.RootSchemaModelSerializer
import com.rokt.network.model.LayoutDisplayPreset
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.LayoutSettings
import com.rokt.network.model.RootSchemaModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * Selection response for a v2 offers request — the model the renderer consumes,
 * alongside the v1 `NetworkExperienceResponse`. The layout schema fields are
 * parsed into the renderer's typed [RootSchemaModel] / [LayoutSchemaModel] (the
 * SDK-side wire model keeps the same fields as raw strings instead).
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
    @SerialName("page_instance_guid") val pageInstanceGuid: String? = null,
    @SerialName("page_id") val pageId: String? = null,
    @SerialName("language") val language: String? = null,
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
    @SerialName("catalog_items") val catalogItems: List<JsonObject>? = null,
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
data class SelectEventDataEntry(
    @SerialName("token") val token: String,
    @SerialName("events") val events: Map<String, JsonElement>? = null,
)
