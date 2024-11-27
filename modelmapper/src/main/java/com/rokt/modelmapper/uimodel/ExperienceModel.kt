package com.rokt.modelmapper.uimodel

import com.rokt.modelmapper.hmap.HMap
import com.rokt.modelmapper.hmap.get
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

data class OfferModel(val campaignId: String, val creative: CreativeModel)

data class CreativeModel(
    val referralCreativeId: String,
    val instanceGuid: String,
    val token: String,
    val responseOptions: ImmutableMap<String, ResponseOptionModel>,
    val copy: ImmutableMap<String, String>,
    val images: ImmutableMap<String, CreativeImageModel>,
    val links: ImmutableMap<String, CreativeLink>,
    val icons: ImmutableMap<String, CreativeIcon>,
)

data class CreativeImageModel(val light: String, val dark: String, val alt: String, val title: String)

data class CreativeLink(val url: String, val title: String)

data class CreativeIcon(val name: String)

data class ResponseOptionModel(val properties: HMap)

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
