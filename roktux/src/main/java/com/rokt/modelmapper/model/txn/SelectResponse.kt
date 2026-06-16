package com.rokt.modelmapper.model.txn

import com.rokt.modelmapper.model.NetworkLayoutSchemaSerializer
import com.rokt.modelmapper.model.RootSchemaModelSerializer
import com.rokt.network.model.LayoutDisplayPreset
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.LayoutSettings
import com.rokt.network.model.RootSchemaModel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

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

@Serializable(with = SelectOfferSerializer::class)
data class SelectOffer(
    val campaignId: String? = null,
    val creative: SelectCreative? = null,
    val catalogItems: List<SelectCatalogItem>? = null,
)

/**
 * Decodes `catalog_items` as opaque [JsonElement]s first so a non-object element
 * (a bare string, number, null, nested array, etc.) does not fail the whole
 * response decode — consistent with the "never fail on shape drift" promise. Only
 * the object-shaped elements become typed [SelectCatalogItem]s; anything else is
 * skipped. An absent key stays `null`; an empty array stays an empty (non-null)
 * list. Mirrors the iOS `SelectOffer.init(from:)` resilient skip.
 */
internal object SelectOfferSerializer : KSerializer<SelectOffer> {
    @Serializable
    private data class Surrogate(
        @SerialName("campaign_id") val campaignId: String? = null,
        @SerialName("creative") val creative: SelectCreative? = null,
        @SerialName("catalog_items") val catalogItems: List<JsonElement>? = null,
    )

    override val descriptor: SerialDescriptor = Surrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): SelectOffer {
        val surrogate = Surrogate.serializer().deserialize(decoder)
        return SelectOffer(
            campaignId = surrogate.campaignId,
            creative = surrogate.creative,
            // absent -> null; empty -> empty list; non-object elements skipped (Behavior B).
            catalogItems = surrogate.catalogItems
                ?.mapNotNull { it as? JsonObject }
                ?.map { SelectCatalogItem(raw = it) },
        )
    }

    override fun serialize(encoder: Encoder, value: SelectOffer) {
        Surrogate.serializer().serialize(
            encoder,
            Surrogate(
                campaignId = value.campaignId,
                creative = value.creative,
                catalogItems = value.catalogItems?.map { it.raw },
            ),
        )
    }
}

/**
 * A catalog item from a v2 offers selection response.
 *
 * The transactions catalog-item shape is open and campaign-specific — only
 * [instanceGuid] and [title] are guaranteed; every other field varies by
 * campaign type. To stay type-safe without risking decode failures as that
 * shape changes, the guaranteed fields are surfaced as (optional) typed
 * accessors while the full payload is retained in [raw] so any
 * campaign-specific field still round-trips. Decoding never fails on an
 * unrecognised shape.
 *
 * When the renderer starts consuming catalog items, promote the fields it needs
 * from [raw] into typed (optional) properties here.
 */
@Serializable(with = SelectCatalogItemSerializer::class)
data class SelectCatalogItem(
    /** The complete decoded payload, keyed by the raw (snake_case) JSON key. */
    val raw: JsonObject,
) {
    /**
     * Guaranteed by the server contract for every catalog item.
     *
     * Surfaced only when the wire value is a JSON string. If the server sends a
     * non-string (number, bool, `null`, object, or array) for this guaranteed
     * field — a server contract violation — this narrows to `null`,
     * indistinguishable from the field being absent. This lossiness is
     * deliberate: decoding never fails on shape drift. The original, untyped
     * value is always preserved in [raw], so callers needing to observe a
     * wrong-typed value can read [raw] directly.
     */
    val instanceGuid: String?
        get() = raw.stringValue("instance_guid")

    /** Guaranteed by the server contract for every catalog item. See [instanceGuid]. */
    val title: String?
        get() = raw.stringValue("title")

    private companion object {
        /**
         * Reads [key] only when its wire value is an actual JSON string.
         *
         * `as? JsonPrimitive` is `null` for objects/arrays (so it never throws,
         * unlike `jsonPrimitive`), and `isString` filters out unquoted
         * number/bool/null literals so they narrow to `null` — matching the iOS
         * `stringValue` narrowing exactly.
         */
        private fun JsonObject.stringValue(key: String): String? =
            (this[key] as? JsonPrimitive)?.takeIf { it.isString }?.content
    }
}

/**
 * Captures the whole catalog-item object into [SelectCatalogItem.raw] so unknown
 * / campaign-specific fields are preserved, rather than mapping a fixed field
 * set. Keeps [raw] as the single source of truth for the wire shape.
 */
internal object SelectCatalogItemSerializer : KSerializer<SelectCatalogItem> {
    private val delegate = JsonObject.serializer()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun deserialize(decoder: Decoder): SelectCatalogItem {
        val input = decoder as? JsonDecoder
            ?: throw SerializationException("SelectCatalogItem can only be deserialized from JSON")
        return SelectCatalogItem(raw = delegate.deserialize(input))
    }

    override fun serialize(encoder: Encoder, value: SelectCatalogItem) {
        val output = encoder as? JsonEncoder
            ?: throw SerializationException("SelectCatalogItem can only be serialized to JSON")
        delegate.serialize(output, value.raw)
    }
}

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
