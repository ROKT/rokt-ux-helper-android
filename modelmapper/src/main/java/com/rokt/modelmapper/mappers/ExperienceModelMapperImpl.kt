package com.rokt.modelmapper.mappers

import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.data.DataBinding
import com.rokt.modelmapper.hmap.HMap
import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.set
import com.rokt.modelmapper.model.NetworkAction
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
import com.rokt.modelmapper.uimodel.CreativeIcon
import com.rokt.modelmapper.uimodel.CreativeImageModel
import com.rokt.modelmapper.uimodel.CreativeLink
import com.rokt.modelmapper.uimodel.CreativeModel
import com.rokt.modelmapper.uimodel.ExperienceModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.LayoutSettings
import com.rokt.modelmapper.uimodel.LayoutVariantModel
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

    private fun NetworkOfferLayout.toOfferModel(): OfferModel = OfferModel(
        campaignId = campaignId,
        creative = creative.toCreativeModel(),
    )

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

    private fun NetworkCreativeImage.toCreateImageModel(): CreativeImageModel = CreativeImageModel(
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

    private fun NetworkAction.toActionModel(): Action = when (this) {
        NetworkAction.Url -> Action.Url
        NetworkAction.CaptureOnly -> Action.CaptureOnly
    }

    private fun NetworkSignalType.toSignalTypeModel(): SignalType = when (this) {
        NetworkSignalType.SignalResponse -> SignalType.SignalResponse
        NetworkSignalType.SignalGatedResponse -> SignalType.SignalGatedResponse
    }

    private fun NetworkLayoutVariant.toLayoutVariantModel(offerModel: OfferModel?): LayoutVariantModel =
        LayoutVariantModel(
            layoutVariantId = layoutVariantId,
            moduleName = moduleName,
            layoutVariantSchema = transformLayoutSchemaModel(
                layoutSchemaModel = layoutVariantSchema,
                offerModel = offerModel,
            ),
        )

    private fun transformLayoutSchemaModel(
        layoutSchemaModel: LayoutSchemaModel,
        offerModel: OfferModel? = null,
        responseContextKey: String? = null,
    ): LayoutSchemaUiModel? = when (layoutSchemaModel) {
        is LayoutSchemaModel.BasicText -> transformBasicText(layoutSchemaModel) { value ->
            bindValue(value, responseContextKey, offerModel)
        }

        is LayoutSchemaModel.RichText -> transformRichText(layoutSchemaModel) { value ->
            bindValue(value, responseContextKey, offerModel)
        }

        is LayoutSchemaModel.Column -> transformColumn(
            layoutSchemaModel,
            false,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey) }

        is LayoutSchemaModel.ScrollableColumn -> transformColumn(
            layoutSchemaModel.toColumn(),
            true,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey) }

        is LayoutSchemaModel.Row -> transformRow(
            layoutSchemaModel,
            false,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey) }

        is LayoutSchemaModel.ScrollableRow -> transformRow(
            layoutSchemaModel.toRow(),
            true,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey) }

        is LayoutSchemaModel.ZStack -> transformZStack(
            layoutSchemaModel,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey) }

        is LayoutSchemaModel.ProgressIndicator -> transformProgressIndicator(layoutSchemaModel) { indicatorText ->
            bindValue(indicatorText, responseContextKey, offerModel)
        }

        is LayoutSchemaModel.CreativeResponse -> transformCreativeResponse(
            layoutSchemaModel,
            offerModel,
        ) { child, key -> transformLayoutSchemaModel(child, offerModel, key) }

        is LayoutSchemaModel.CloseButton -> transformCloseButton(
            layoutSchemaModel,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey) }

        is LayoutSchemaModel.StaticLink -> transformStaticLink(
            layoutSchemaModel,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey) }

        is LayoutSchemaModel.ToggleButtonStateTrigger -> transformToggleButtonStateTrigger(
            layoutSchemaModel,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey) }

        is LayoutSchemaModel.ProgressControl -> transformProgressControl(
            layoutSchemaModel,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey) }

        is LayoutSchemaModel.OneByOneDistribution -> transformOneByOneDistribution(layoutSchemaModel)
        is LayoutSchemaModel.GroupedDistribution -> transformGroupedDistribution(layoutSchemaModel)
        is LayoutSchemaModel.CarouselDistribution -> transformCarouselDistribution(layoutSchemaModel)
        is LayoutSchemaModel.Overlay -> transformOverlay(layoutSchemaModel) { child ->
            transformLayoutSchemaModel(child, offerModel, responseContextKey)
        }

        is LayoutSchemaModel.BottomSheet -> transformBottomSheet(layoutSchemaModel) { child ->
            transformLayoutSchemaModel(child, offerModel, responseContextKey)
        }

        is LayoutSchemaModel.StaticImage -> transformStaticImage(layoutSchemaModel)

        is LayoutSchemaModel.DataImage -> transformDataImage(layoutSchemaModel, offerModel)

        is LayoutSchemaModel.When -> transformWhen(
            layoutSchemaModel,
        ) { child -> transformLayoutSchemaModel(child, offerModel, responseContextKey) }

        is LayoutSchemaModel.DataIcon -> transformDataIcon(layoutSchemaModel, offerModel)

        is LayoutSchemaModel.StaticIcon -> transformStaticIcon(layoutSchemaModel)
        else -> null
    }

    private fun bindValue(value: String, contextKey: String? = null, offerModel: OfferModel? = null): BindData =
        dataBinding.bindValue(value, contextKey, offerModel)

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
    }
}
