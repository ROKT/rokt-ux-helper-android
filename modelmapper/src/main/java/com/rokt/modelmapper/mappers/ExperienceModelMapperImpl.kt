package com.rokt.modelmapper.mappers

import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.data.DataBinding
import com.rokt.modelmapper.hmap.HMap
import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.set
import com.rokt.modelmapper.model.NetworkAction
import com.rokt.modelmapper.model.NetworkCatalogItem
import com.rokt.modelmapper.model.NetworkCreativeImage
import com.rokt.modelmapper.model.NetworkCreativeLayout
import com.rokt.modelmapper.model.NetworkExperienceResponse
import com.rokt.modelmapper.model.NetworkLayoutVariant
import com.rokt.modelmapper.model.NetworkOfferLayout
import com.rokt.modelmapper.model.NetworkOptions
import com.rokt.modelmapper.model.NetworkPageContext
import com.rokt.modelmapper.model.NetworkPlugin
import com.rokt.modelmapper.model.NetworkResponseOption
import com.rokt.modelmapper.model.NetworkSignalType
import com.rokt.modelmapper.model.NetworkSlotLayout
import com.rokt.modelmapper.uimodel.Action
import com.rokt.modelmapper.uimodel.CatalogImageWrapperModel
import com.rokt.modelmapper.uimodel.CatalogItemModel
import com.rokt.modelmapper.uimodel.CreativeIcon
import com.rokt.modelmapper.uimodel.CreativeLink
import com.rokt.modelmapper.uimodel.CreativeModel
import com.rokt.modelmapper.uimodel.ExperienceModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.LayoutSettings
import com.rokt.modelmapper.uimodel.LayoutVariantModel
import com.rokt.modelmapper.uimodel.Module
import com.rokt.modelmapper.uimodel.OfferImageModel
import com.rokt.modelmapper.uimodel.OfferModel
import com.rokt.modelmapper.uimodel.OptionsModel
import com.rokt.modelmapper.uimodel.PlacementContextModel
import com.rokt.modelmapper.uimodel.PluginModel
import com.rokt.modelmapper.uimodel.ResponseOptionModel
import com.rokt.modelmapper.uimodel.SignalType
import com.rokt.modelmapper.uimodel.SlotModel
import com.rokt.network.model.LayoutSchemaModel
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface ModelMapper {
    fun transformResponse(): Result<ExperienceModel>
    fun getSavedExperience(): ExperienceModel?
}

class ExperienceModelMapperImpl(private val experienceResponse: String, private val dataBinding: DataBinding) :
    ModelMapper {

    var savedExperienceModel: Result<ExperienceModel>? = null

    private val json by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }

    override fun transformResponse(): Result<ExperienceModel> {
        savedExperienceModel = try {
            json.decodeFromString<NetworkExperienceResponse>(experienceResponse)
                .let { Result.success(it.toExperienceModel()) }
        } catch (e: Throwable) {
            Result.failure(e)
        }
        return savedExperienceModel ?: Result.failure(Exception())
    }

    override fun getSavedExperience(): ExperienceModel? = savedExperienceModel?.getOrNull()

    private fun NetworkExperienceResponse.toExperienceModel(): ExperienceModel = ExperienceModel(
        sessionId = sessionId,
        token = pageContext.token,
        pageId = pageContext.pageId,
        placementContext = pageContext.toPlacementContextModel(),
        plugins = plugins.map { it.plugin.toPluginModel() }.toImmutableList(),
        options = options.toOptionsModel(),
    )

    private fun NetworkPageContext.toPlacementContextModel(): PlacementContextModel =
        PlacementContextModel(pageInstanceGuid, token)

    private fun NetworkOptions.toOptionsModel(): OptionsModel = OptionsModel(useDiagnosticEvents)

    private fun NetworkPlugin.toPluginModel(): PluginModel = PluginModel(
        id = id,
        name = name,
        targetElementSelector = targetElementSelector,
        instanceGuid = config.instanceGuid,
        token = config.token,
        outerLayoutSchema = transformLayoutSchemaModel(config.outerLayoutSchema.layout),
        slots = config.slots.map { it.toSlotModel() }.toImmutableList(),
        breakpoint = config.outerLayoutSchema.breakpoints.buildBreakpoints(),
        settings = buildSettings(config.outerLayoutSchema.settings),
    )

    private fun buildSettings(settings: com.rokt.network.model.LayoutSettings?): LayoutSettings = LayoutSettings(
        closeOnComplete = settings?.closeOnComplete ?: true,
    )

    private fun HashMap<String, Float>.buildBreakpoints(): ImmutableMap<String, Int> {
        val breakpoints = mutableMapOf<String, Int>()
        // Add the default breakpoint
        breakpoints["default"] = 0
        // Add the other breakpoints
        breakpoints.putAll(
            this.mapValues { pair -> pair.value.toInt() },
        )
        return breakpoints.toImmutableMap()
    }

    private fun NetworkSlotLayout.toSlotModel(): SlotModel {
        val offerModel = offer?.toOfferModel()
        return SlotModel(
            instanceGuid = instanceGuid,
            token = token,
            offer = offerModel,
            layoutVariant = layoutVariant?.toLayoutVariantModel(offerModel),
        )
    }

    private fun NetworkOfferLayout.toOfferModel(): OfferModel {
        val offerModel = OfferModel(
            campaignId = campaignId,
            creative = creative.toCreativeModel(),
            catalogItems = catalogItems.map { it.toCatalogItemModel() }.toImmutableList(),
        )
        return offerModel
    }

    private fun NetworkCreativeLayout.toCreativeModel(): CreativeModel = CreativeModel(
        referralCreativeId = referralCreativeId,
        instanceGuid = instanceGuid,
        token = token,
        responseOptions = responseOptions.mapValues { it.value.toResponseOptionModel() }.toImmutableMap(),
        copy = copy.toImmutableMap(),
        icons = icons.mapValues { CreativeIcon(it.value.name) }.toImmutableMap(),
        images = images.mapValues { it.value.toCreateImageModel() }.toImmutableMap(),
        links = links.mapValues { CreativeLink(it.value.url, it.value.title) }.toImmutableMap(),
    )

    private fun NetworkCreativeImage.toCreateImageModel(): OfferImageModel = OfferImageModel(
        light = light,
        dark = dark,
        alt = alt,
        title = title,
    )

    private fun NetworkResponseOption.toResponseOptionModel(): ResponseOptionModel = ResponseOptionModel(
        HMap().apply {
            set(TypedKey<String>(KEY_ID), id)
            set(TypedKey<Action>(KEY_ACTION), action?.toActionModel())
            set(TypedKey<String>(KEY_INSTANCE_GUID), instanceGuid)
            set(TypedKey<String>(KEY_TOKEN), token)
            set(TypedKey<SignalType>(KEY_SIGNAL_TYPE), signalType.toSignalTypeModel())
            set(TypedKey<String>(KEY_SHORT_LABEL), shortLabel)
            set(TypedKey<String>(KEY_LONG_LABEL), longLabel)
            set(TypedKey<String>(KEY_SHORT_SUCCESS_LABEL), shortSuccessLabel)
            set(TypedKey<Boolean>(KEY_IS_POSITIVE), isPositive)
            set(TypedKey<String>(KEY_URL), url)
            set(TypedKey<Boolean>(KEY_IGNORE_BRANCH), ignoreBranch)
        },
    )

    private fun NetworkCatalogItem.toCatalogItemModel(): CatalogItemModel = CatalogItemModel(
        HMap().apply {
            set(TypedKey<String>(KEY_CATALOG_ITEM_ID), catalogItemId)
            set(TypedKey<String>(KEY_CART_ITEM_ID), cartItemId)
            set(TypedKey<String>(KEY_INSTANCE_GUID), instanceGuid)
            set(TypedKey<String>(KEY_TITLE), title)
            set(TypedKey<String>(KEY_DESCRIPTION), description)
            set(TypedKey<Double>(KEY_PRICE), price)
            set(TypedKey<Double>(KEY_ORIGINAL_PRICE), originalPrice)
            set(TypedKey<String>(KEY_ORIGINAL_PRICE_FORMATTED), originalPriceFormatted)
            set(TypedKey<String>(KEY_CURRENCY), currency)
            set(TypedKey<SignalType>(KEY_SIGNAL_TYPE), signalType.toSignalTypeModel())
            set(TypedKey<String>(KEY_URL), url)
            set(TypedKey<Int>(KEY_MIN_ITEM_COUNT), minItemCount)
            set(TypedKey<Int>(KEY_MAX_ITEM_COUNT), maxItemCount)
            set(TypedKey<Int>(KEY_PRE_SELECTED_QUANTITY), preSelectedQuantity)
            set(TypedKey<String>(KEY_PROVIDER_DATA), providerData)
            set(TypedKey<String>(KEY_URL_BEHAVIOUR), urlBehavior)
            set(TypedKey<String>(KEY_LINKED_PRODUCT_ID), linkedProductId)
            set(TypedKey<Boolean>(KEY_QUANTITY_MUST_BE_SYNCHRONIZED), quantityMustBeSynchronized)
            set(TypedKey<String>(KEY_POSITIVE_RESPONSE_TEXT), positiveResponseText)
            set(TypedKey<String>(KEY_NEGATIVE_RESPONSE_TEXT), negativeResponseText)
            set(TypedKey<String>(KEY_PRICE_FORMATTED), priceFormatted)
            set(TypedKey<String>(KEY_ADD_ON_PLUGIN_URL), addOnPluginUrl)
            set(TypedKey<String>(KEY_ADD_ON_PLUGIN_NAME), addOnPluginName)
            set(TypedKey<String>(KEY_TOKEN), token)
        },
        imageWrapper = transformImage(images),
    )

    private fun NetworkAction.toActionModel(): Action = when (this) {
        NetworkAction.Url -> Action.Url
        NetworkAction.CaptureOnly -> Action.CaptureOnly
    }

    private fun NetworkSignalType.toSignalTypeModel(): SignalType = when (this) {
        NetworkSignalType.SignalResponse -> SignalType.SignalResponse
        NetworkSignalType.SignalGatedResponse -> SignalType.SignalGatedResponse
    }

    private fun NetworkCreativeImage.toCatalogItemImage(): OfferImageModel = OfferImageModel(
        light = light,
        dark = dark,
        alt = alt,
        title = title,
    )

    private fun transformImage(imageMap: Map<String, NetworkCreativeImage>): CatalogImageWrapperModel =
        CatalogImageWrapperModel(
            HMap().apply {
                imageMap.forEach { (key, value) ->
                    set(TypedKey<OfferImageModel>(key), value.toCatalogItemImage())
                }
            },
        )

    private fun NetworkLayoutVariant.toLayoutVariantModel(offerModel: OfferModel?): LayoutVariantModel =
        LayoutVariantModel(
            layoutVariantId = layoutVariantId,
            moduleName = moduleName,
            layoutVariantSchema = transformLayoutSchemaModel(
                layoutSchemaModel = layoutVariantSchema,
                offerModel = offerModel,
                module = Module.fromString(moduleName),
            ),
        )

    private fun transformLayoutSchemaModel(
        layoutSchemaModel: LayoutSchemaModel,
        offerModel: OfferModel? = null,
        responseContextKey: String? = null,
        itemIndex: Int = 0,
        module: Module = Module.StandardMarketing,
    ): LayoutSchemaUiModel? = when (layoutSchemaModel) {
        is LayoutSchemaModel.BasicText -> transformBasicText(layoutSchemaModel) { value ->
            bindValue(value, responseContextKey, offerModel, itemIndex)
        }

        is LayoutSchemaModel.RichText -> transformRichText(layoutSchemaModel) { value ->
            bindValue(value, responseContextKey, offerModel, itemIndex)
        }

        is LayoutSchemaModel.Column -> transformColumn(
            layoutSchemaModel,
            false,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module) }

        is LayoutSchemaModel.ScrollableColumn -> transformColumn(
            layoutSchemaModel.toColumn(),
            true,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module) }

        is LayoutSchemaModel.Row -> transformRow(
            layoutSchemaModel,
            false,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module) }

        is LayoutSchemaModel.ScrollableRow -> transformRow(
            layoutSchemaModel.toRow(),
            true,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module) }

        is LayoutSchemaModel.ZStack -> transformZStack(
            layoutSchemaModel,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module) }

        is LayoutSchemaModel.ProgressIndicator -> transformProgressIndicator(layoutSchemaModel) { indicatorText ->
            bindValue(indicatorText, responseContextKey, offerModel, itemIndex)
        }

        is LayoutSchemaModel.CreativeResponse -> transformCreativeResponse(
            layoutSchemaModel,
            offerModel,
        ) { child, key -> transformLayoutSchemaModel(child, offerModel, key, itemIndex, module) }

        is LayoutSchemaModel.CloseButton -> transformCloseButton(
            layoutSchemaModel,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module) }

        is LayoutSchemaModel.StaticLink -> transformStaticLink(
            layoutSchemaModel,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module) }

        is LayoutSchemaModel.ToggleButtonStateTrigger -> transformToggleButtonStateTrigger(
            layoutSchemaModel,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module) }

        is LayoutSchemaModel.ProgressControl -> transformProgressControl(
            layoutSchemaModel,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module) }

        is LayoutSchemaModel.OneByOneDistribution -> transformOneByOneDistribution(layoutSchemaModel)
        is LayoutSchemaModel.GroupedDistribution -> transformGroupedDistribution(layoutSchemaModel)
        is LayoutSchemaModel.CarouselDistribution -> transformCarouselDistribution(layoutSchemaModel)
        is LayoutSchemaModel.Overlay -> transformOverlay(layoutSchemaModel) { child ->
            transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module)
        }

        is LayoutSchemaModel.BottomSheet -> transformBottomSheet(layoutSchemaModel) { child ->
            transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module)
        }

        is LayoutSchemaModel.StaticImage -> transformStaticImage(layoutSchemaModel)

        is LayoutSchemaModel.DataImage -> transformDataImage(layoutSchemaModel, offerModel, module, itemIndex)

        is LayoutSchemaModel.When -> transformWhen(
            layoutSchemaModel,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module) }

        is LayoutSchemaModel.DataIcon -> transformDataIcon(layoutSchemaModel, offerModel, module, itemIndex)

        is LayoutSchemaModel.StaticIcon -> transformStaticIcon(layoutSchemaModel)

        else -> null
    }

    private fun bindValue(
        value: String,
        contextKey: String? = null,
        offerModel: OfferModel?,
        itemIndex: Int,
    ): BindData = dataBinding.bindValue(value, contextKey, offerModel, itemIndex)

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_TOKEN = "token"
        private const val KEY_SHORT_LABEL = "shortLabel"
        private const val KEY_LONG_LABEL = "longLabel"
        private const val KEY_SHORT_SUCCESS_LABEL = "shortSuccessLabel"
        const val KEY_URL = "url"
        private const val KEY_IGNORE_BRANCH = "ignoreBranch"
        const val KEY_ACTION = "action"
        const val KEY_INSTANCE_GUID = "instanceGuid"
        const val KEY_SIGNAL_TYPE = "signalType"
        const val KEY_IS_POSITIVE = "isPositive"

        private const val KEY_IMAGES = "images"
        private const val KEY_CATALOG_ITEM_ID = "catalogItemId"
        private const val KEY_CART_ITEM_ID = "cartItemId"
        private const val KEY_TITLE = "title"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_PRICE = "price"
        private const val KEY_ORIGINAL_PRICE = "originalPrice"
        private const val KEY_ORIGINAL_PRICE_FORMATTED = "originalPriceFormatted"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_MIN_ITEM_COUNT = "minItemCount"
        private const val KEY_MAX_ITEM_COUNT = "maxItemCount"
        private const val KEY_PRE_SELECTED_QUANTITY = "preSelectedQuantity"
        private const val KEY_PROVIDER_DATA = "providerData"
        private const val KEY_URL_BEHAVIOUR = "urlBehavior"
        private const val KEY_LINKED_PRODUCT_ID = "linkedProductId"
        private const val KEY_QUANTITY_MUST_BE_SYNCHRONIZED = "quantityMustBeSynchronized"
        private const val KEY_POSITIVE_RESPONSE_TEXT = "positiveResponseText"
        private const val KEY_NEGATIVE_RESPONSE_TEXT = "negativeResponseText"
        private const val KEY_PRICE_FORMATTED = "priceFormatted"
        private const val KEY_ADD_ON_PLUGIN_URL = "addOnPluginUrl"
        private const val KEY_ADD_ON_PLUGIN_NAME = "addOnPluginName"
    }
}
