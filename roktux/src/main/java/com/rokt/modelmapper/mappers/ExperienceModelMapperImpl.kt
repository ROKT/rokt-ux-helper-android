package com.rokt.modelmapper.mappers

import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.data.DataBinding
import com.rokt.modelmapper.hmap.HMap
import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.set
import com.rokt.modelmapper.model.txn.SelectAddress
import com.rokt.modelmapper.model.txn.SelectCatalogItem
import com.rokt.modelmapper.model.txn.SelectCatalogItemGroup
import com.rokt.modelmapper.model.txn.SelectCatalogItemGroupAttribute
import com.rokt.modelmapper.model.txn.SelectCatalogItemGroupOption
import com.rokt.modelmapper.model.txn.SelectCreative
import com.rokt.modelmapper.model.txn.SelectImage
import com.rokt.modelmapper.model.txn.SelectLayoutVariant
import com.rokt.modelmapper.model.txn.SelectOffer
import com.rokt.modelmapper.model.txn.SelectPaymentMethod
import com.rokt.modelmapper.model.txn.SelectPluginLayout
import com.rokt.modelmapper.model.txn.SelectResponse
import com.rokt.modelmapper.model.txn.SelectResponseOption
import com.rokt.modelmapper.model.txn.SelectSlot
import com.rokt.modelmapper.model.txn.SelectTransactionData
import com.rokt.modelmapper.uimodel.Action
import com.rokt.modelmapper.uimodel.Address
import com.rokt.modelmapper.uimodel.CatalogImageWrapperModel
import com.rokt.modelmapper.uimodel.CatalogItemGroupAttributeModel
import com.rokt.modelmapper.uimodel.CatalogItemGroupModel
import com.rokt.modelmapper.uimodel.CatalogItemGroupOptionModel
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
import com.rokt.modelmapper.uimodel.PaymentMethod
import com.rokt.modelmapper.uimodel.PlacementContextModel
import com.rokt.modelmapper.uimodel.PluginModel
import com.rokt.modelmapper.uimodel.ResponseOptionModel
import com.rokt.modelmapper.uimodel.SignalType
import com.rokt.modelmapper.uimodel.SlotModel
import com.rokt.modelmapper.uimodel.TransactionData
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.roktux.logging.RoktUXLogger
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface ModelMapper {
    fun transformResponse(): Result<ExperienceModel>
    fun getSavedExperience(): ExperienceModel?
}

class ExperienceModelMapperImpl(
    private val experienceResponse: String?,
    private val parsedExperienceResponse: SelectResponse?,
    private val dataBinding: DataBinding,
) : ModelMapper {

    constructor(experienceResponse: String, dataBinding: DataBinding) : this(experienceResponse, null, dataBinding)

    var savedExperienceModel: Result<ExperienceModel>? = null

    private val json by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }

    override fun transformResponse(): Result<ExperienceModel> {
        RoktUXLogger.verbose { "Transforming experience response" }
        savedExperienceModel = try {
            val response = parsedExperienceResponse ?: json.decodeFromString<SelectResponse>(
                requireNotNull(experienceResponse) { "Experience response is required" },
            )
            Result.success(response.toExperienceModel())
        } catch (e: Throwable) {
            RoktUXLogger.error(error = e) { "Failed to transform experience response" }
            Result.failure(e)
        }
        return savedExperienceModel ?: Result.failure(Exception())
    }

    override fun getSavedExperience(): ExperienceModel? = savedExperienceModel?.getOrNull()

    private fun SelectResponse.toExperienceModel(): ExperienceModel {
        val token = pageContext?.token ?: sessionToken.token
        return ExperienceModel(
            sessionId = sessionId,
            token = token,
            pageId = pageContext?.pageId,
            placementContext = PlacementContextModel(
                pageInstanceGuid = pageContext?.pageInstanceGuid ?: pageInstanceGuid,
                token = token,
            ),
            plugins = (plugins ?: emptyList()).mapNotNull { it.plugin?.toPluginModel() }.toImmutableList(),
            // The v2 response carries no `options`; diagnostic events default on (matches
            // the previous SDK experience-response behaviour).
            options = OptionsModel(useDiagnosticEvents = true),
        )
    }

    private fun SelectPluginLayout.toPluginModel(): PluginModel {
        val outer = config?.outerLayoutSchema
        return PluginModel(
            id = id.orEmpty(),
            name = name.orEmpty(),
            targetElementSelector = targetElementSelector.orEmpty(),
            instanceGuid = config?.instanceGuid.orEmpty(),
            token = config?.token.orEmpty(),
            outerLayoutSchema = outer?.layout?.let { transformOuterLayoutSchemaModel(it) },
            slots = (config?.slots ?: emptyList()).map { it.toSlotModel() }.toImmutableList(),
            breakpoint = outer?.breakpoints.buildBreakpoints(),
            settings = buildSettings(outer?.settings),
        )
    }

    private fun buildSettings(settings: com.rokt.network.model.LayoutSettings?): LayoutSettings = LayoutSettings(
        closeOnComplete = settings?.closeOnComplete ?: true,
    )

    private fun HashMap<String, Float>?.buildBreakpoints(): ImmutableMap<String, Int> {
        val breakpoints = mutableMapOf<String, Int>()
        // Add the default breakpoint
        breakpoints["default"] = 0
        // Add the other breakpoints
        this?.let {
            breakpoints.putAll(it.mapValues { pair -> pair.value.toInt() })
        }
        return breakpoints.toImmutableMap()
    }

    private fun SelectSlot.toSlotModel(): SlotModel {
        val offerModel = offer?.toOfferModel()
        return SlotModel(
            instanceGuid = instanceGuid.orEmpty(),
            token = token.orEmpty(),
            offer = offerModel,
            layoutVariant = layoutVariant?.toLayoutVariantModel(offerModel),
        )
    }

    // An offer is only renderable with a creative; without one there is nothing to map.
    private fun SelectOffer.toOfferModel(): OfferModel? {
        val creativeModel = creative?.toCreativeModel() ?: return null
        return OfferModel(
            campaignId = campaignId.orEmpty(),
            creative = creativeModel,
            catalogItems = (catalogItems ?: emptyList()).map { it.toCatalogItemModel() }.toImmutableList(),
            transactionData = transactionData?.toTransactionDataModel(),
            catalogItemGroup = catalogItemGroup?.toCatalogItemGroupModel(),
        )
    }

    private fun SelectTransactionData.toTransactionDataModel(): TransactionData = TransactionData(
        shippingAddress = shippingAddress?.toAddressModel(),
        billingAddress = billingAddress?.toAddressModel(),
        paymentType = paymentType,
        supportedPaymentMethods = supportedPaymentMethods?.map { it.toPaymentMethodModel() },
        isPartnerManagedPurchase = isPartnerManagedPurchase ?: true,
        partnerPaymentReference = partnerPaymentReference,
        confirmationRef = confirmationRef,
        metadata = metadata ?: emptyMap(),
    )

    private fun SelectPaymentMethod.toPaymentMethodModel(): PaymentMethod = PaymentMethod(type = type.orEmpty())

    private fun SelectAddress.toAddressModel(): Address = Address(
        name = name.orEmpty(),
        address1 = address1.orEmpty(),
        address2 = address2,
        city = city.orEmpty(),
        state = state.orEmpty(),
        stateCode = stateCode.orEmpty(),
        country = country.orEmpty(),
        countryCode = countryCode.orEmpty(),
        zip = zip,
    )

    private fun SelectCreative.toCreativeModel(): CreativeModel = CreativeModel(
        referralCreativeId = referralCreativeId.orEmpty(),
        instanceGuid = instanceGuid.orEmpty(),
        token = token.orEmpty(),
        responseOptions = (responseOptionsMap ?: emptyMap())
            .mapValues { it.value.toResponseOptionModel() }.toImmutableMap(),
        copy = (copy ?: emptyMap()).toImmutableMap(),
        icons = (icons ?: emptyMap()).mapValues { CreativeIcon(it.value.name.orEmpty()) }.toImmutableMap(),
        images = (images ?: emptyMap()).mapValues { it.value.toCreateImageModel() }.toImmutableMap(),
        links = (links ?: emptyMap()).mapValues { CreativeLink(it.value.url.orEmpty(), it.value.title.orEmpty()) }
            .toImmutableMap(),
    )

    private fun SelectImage.toCreateImageModel(): OfferImageModel = OfferImageModel(
        HMap().apply {
            set(TypedKey<String>(KEY_LIGHT), light)
            set(TypedKey<String>(KEY_DARK), dark)
            set(TypedKey<String>(KEY_ALT), alt)
            set(TypedKey<String>(KEY_TITLE), title)
        },
    )

    private fun SelectResponseOption.toResponseOptionModel(): ResponseOptionModel = ResponseOptionModel(
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

    private fun SelectCatalogItem.toCatalogItemModel(): CatalogItemModel = CatalogItemModel(
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
            set(TypedKey<String>(KEY_TOKEN), token)
            set(TypedKey<String>(KEY_INVENTORY_STATUS), inventoryStatus)
        },
        imageWrapper = transformImage(images ?: emptyMap()),
        copy = copy ?: emptyMap(),
    )

    private fun SelectCatalogItemGroup.toCatalogItemGroupModel(): CatalogItemGroupModel = CatalogItemGroupModel(
        groupId = groupId.orEmpty(),
        catalogItemIds = (catalogItemIds ?: emptyList()).toImmutableList(),
        attributes = (attributes ?: emptyList()).map { it.toCatalogItemGroupAttributeModel() }.toImmutableList(),
        metadata = (metadata ?: emptyMap()).toImmutableMap(),
    )

    private fun SelectCatalogItemGroupAttribute.toCatalogItemGroupAttributeModel(): CatalogItemGroupAttributeModel =
        CatalogItemGroupAttributeModel(
            attributeId = attributeId.orEmpty(),
            label = label,
            options = (options ?: emptyList()).map { it.toCatalogItemGroupOptionModel() }.toImmutableList(),
            metadata = (metadata ?: emptyMap()).toImmutableMap(),
        )

    private fun SelectCatalogItemGroupOption.toCatalogItemGroupOptionModel(): CatalogItemGroupOptionModel =
        CatalogItemGroupOptionModel(
            label = label,
            catalogItemIds = (catalogItemIds ?: emptyList()).toImmutableList(),
            metadata = (metadata ?: emptyMap()).toImmutableMap(),
        )

    // v2 sends `action` / `signal_type` as strings; map to the renderer enums with a
    // safe default for anything unrecognised.
    private fun String.toActionModel(): Action = when (this) {
        "CaptureOnly" -> Action.CaptureOnly
        "ExternalPaymentTrigger" -> Action.ExternalPaymentTrigger
        else -> Action.Url
    }

    private fun String?.toSignalTypeModel(): SignalType = when (this) {
        "SignalGatedResponse" -> SignalType.SignalGatedResponse
        else -> SignalType.SignalResponse
    }

    private fun SelectImage.toCatalogItemImage(): OfferImageModel = OfferImageModel(
        HMap().apply {
            set(TypedKey<String>(KEY_LIGHT), light)
            set(TypedKey<String>(KEY_DARK), dark)
            set(TypedKey<String>(KEY_ALT), alt)
            set(TypedKey<String>(KEY_TITLE), title)
        },
    )

    private fun transformImage(imageMap: Map<String, SelectImage>): CatalogImageWrapperModel = CatalogImageWrapperModel(
        HMap().apply {
            imageMap.forEach { (key, value) ->
                set(TypedKey<OfferImageModel>(key), value.toCatalogItemImage())
            }
        },
    )

    private fun SelectLayoutVariant.toLayoutVariantModel(offerModel: OfferModel?): LayoutVariantModel {
        nextCatalogDropdownAttributeIndex = 0
        return LayoutVariantModel(
            layoutVariantId = layoutVariantId.orEmpty(),
            moduleName = moduleName.orEmpty(),
            layoutVariantSchema = layoutVariantSchema?.let {
                transformLayoutSchemaModel(
                    layoutSchemaModel = it,
                    offerModel = offerModel,
                    module = Module.fromString(moduleName.orEmpty()),
                )
            },
        )
    }

    private fun transformOuterLayoutSchemaModel(layoutSchemaModel: LayoutSchemaModel): LayoutSchemaUiModel? {
        nextCatalogDropdownAttributeIndex = 0
        return transformLayoutSchemaModel(layoutSchemaModel)
    }

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
            transformLayoutSchemaChildren = { child ->
                transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module)
            },
            bindPlaceholderValue = { value ->
                bindValue(value, responseContextKey, offerModel, itemIndex)
            },
        )

        is LayoutSchemaModel.DataIcon -> transformDataIcon(layoutSchemaModel, offerModel, module, itemIndex)

        is LayoutSchemaModel.StaticIcon -> transformStaticIcon(layoutSchemaModel)

        is LayoutSchemaModel.DataImageCarousel -> transformDataImageCarousel(
            layoutSchemaModel,
            offerModel,
        )

        is LayoutSchemaModel.CatalogStackedCollection -> transformCatalogStackedCollection(
            layoutSchemaModel,
            offerModel,
        ) { index, catalogItemModule, child ->
            transformLayoutSchemaModel(child, offerModel, responseContextKey, index, catalogItemModule)
        }

        is LayoutSchemaModel.CatalogCombinedCollection -> transformCatalogCombinedCollectionWithScopedDropdowns(
            layoutSchemaModel,
            offerModel,
            responseContextKey,
        )

        is LayoutSchemaModel.CatalogResponseButton -> transformCatalogResponseButton(
            layoutSchemaModel,
            offerModel,
            itemIndex,
        ) { child ->
            transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module)
        }

        is LayoutSchemaModel.AccessibilityGrouped -> TODO("AccessibilityGrouped mapping is not implemented")

        is LayoutSchemaModel.CatalogDevicePayButton -> transformCatalogDevicePayButton(
            layoutSchemaModel,
            offerModel,
            itemIndex,
        ) { child ->
            transformLayoutSchemaModel(child, offerModel, responseContextKey, itemIndex, module)
        }

        is LayoutSchemaModel.CatalogDropdown -> transformCatalogDropdown(
            layoutSchemaModel,
            offerModel,
            nextCatalogDropdownAttributeIndex++,
        )

        is LayoutSchemaModel.CatalogImageGallery -> transformCatalogImageGallery(
            layoutSchemaModel,
            offerModel,
            itemIndex,
            module,
        )

        is LayoutSchemaModel.SlideStateTrigger -> TODO("SlideStateTrigger mapping is not implemented")
    }

    private fun bindValue(
        value: String,
        contextKey: String? = null,
        offerModel: OfferModel?,
        itemIndex: Int,
    ): BindData = dataBinding.bindValue(value, contextKey, offerModel, itemIndex)

    private var nextCatalogDropdownAttributeIndex = 0

    private fun transformCatalogCombinedCollectionWithScopedDropdowns(
        layoutSchemaModel: LayoutSchemaModel.CatalogCombinedCollection,
        offerModel: OfferModel?,
        responseContextKey: String?,
    ): LayoutSchemaUiModel.CatalogCombinedCollectionUiModel {
        val dropdownIndexBeforeCollection = nextCatalogDropdownAttributeIndex
        var dropdownIndexAfterTemplate: Int? = null

        val result = transformCatalogCombinedCollection(
            layoutSchemaModel,
            offerModel,
        ) { index, catalogItemModule, child ->
            nextCatalogDropdownAttributeIndex = dropdownIndexBeforeCollection
            transformLayoutSchemaModel(child, offerModel, responseContextKey, index, catalogItemModule)
                .also {
                    if (dropdownIndexAfterTemplate == null) {
                        dropdownIndexAfterTemplate = nextCatalogDropdownAttributeIndex
                    }
                }
        }

        nextCatalogDropdownAttributeIndex = dropdownIndexAfterTemplate ?: dropdownIndexBeforeCollection
        return result
    }

    companion object {
        private const val KEY_ID = "id"
        const val KEY_TOKEN = "token"
        private const val KEY_SHORT_LABEL = "shortLabel"
        private const val KEY_LONG_LABEL = "longLabel"
        private const val KEY_SHORT_SUCCESS_LABEL = "shortSuccessLabel"
        const val KEY_URL = "url"
        private const val KEY_IGNORE_BRANCH = "ignoreBranch"
        const val KEY_ACTION = "action"
        const val KEY_INSTANCE_GUID = "instanceGuid"
        const val KEY_SIGNAL_TYPE = "signalType"
        const val KEY_IS_POSITIVE = "isPositive"
        const val KEY_LIGHT = "light"
        const val KEY_DARK = "dark"
        const val KEY_ALT = "alt"
        const val KEY_TITLE = "title"

        private const val KEY_IMAGES = "images"
        private const val KEY_CATALOG_ITEM_ID = "catalogItemId"
        private const val KEY_CART_ITEM_ID = "cartItemId"
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
        const val KEY_INVENTORY_STATUS = "inventoryStatus"
    }
}
