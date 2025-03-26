package com.rokt.modelmapper.uimodel

import com.rokt.modelmapper.hmap.HMap
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

data class ExperienceModel(
    val sessionId: String,
    val token: String,
    val pageId: String?,
    val placementContext: PlacementContextModel,
    val plugins: ImmutableList<PluginModel>,
    val options: OptionsModel,
)

data class PlacementContextModel(val pageInstanceGuid: String, val token: String)

data class OptionsModel(val useDiagnosticEvents: Boolean)

data class PluginModel(
    val id: String,
    val name: String,
    val targetElementSelector: String,
    val instanceGuid: String,
    val token: String,
    val outerLayoutSchema: LayoutSchemaUiModel?,
    val slots: ImmutableList<SlotModel>,
    val breakpoint: ImmutableMap<String, Int>,
    val settings: LayoutSettings,
)

data class LayoutSettings(val closeOnComplete: Boolean)

data class SlotModel(
    val instanceGuid: String,
    val token: String,
    val offer: OfferModel?,
    val layoutVariant: LayoutVariantModel?,
)

data class OfferModel(
    val campaignId: String,
    val creative: CreativeModel,
    val catalogItems: ImmutableList<CatalogItemModel>,
)

data class CreativeModel(
    val referralCreativeId: String,
    val instanceGuid: String,
    val token: String,
    val responseOptions: ImmutableMap<String, ResponseOptionModel>,
    val copy: ImmutableMap<String, String>,
    val images: ImmutableMap<String, OfferImageModel>,
    val links: ImmutableMap<String, CreativeLink>,
    val icons: ImmutableMap<String, CreativeIcon>,
)

data class OfferImageModel(val properties: HMap)

data class CreativeLink(val url: String, val title: String)

data class CreativeIcon(val name: String)

data class ResponseOptionModel(val properties: HMap)

data class CatalogItemModel(val properties: HMap, val imageWrapper: CatalogImageWrapperModel)

data class CatalogImageWrapperModel(val properties: HMap)

data class LayoutVariantModel(
    val layoutVariantId: String,
    val moduleName: String,
    val layoutVariantSchema: LayoutSchemaUiModel?,
)

enum class Action {
    Url,

    CaptureOnly,
}

enum class SignalType {
    SignalResponse,

    SignalGatedResponse,
}

enum class Module(val value: String) {
    StandardMarketing("standard-marketing"),
    AddToCart("add-to-cart"),
    ;

    companion object {
        fun fromString(value: String): Module =
            values().associateBy(Module::value)[value.lowercase()] ?: StandardMarketing
    }
}
