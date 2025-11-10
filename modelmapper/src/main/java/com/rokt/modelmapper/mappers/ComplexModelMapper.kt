package com.rokt.modelmapper.mappers

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.layout.ContentScale
import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.data.bindModel
import com.rokt.modelmapper.data.getOfferImages
import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.get
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_ALT
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_DARK
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_LIGHT
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_TITLE
import com.rokt.modelmapper.uimodel.BooleanWhenUiCondition
import com.rokt.modelmapper.uimodel.CatalogItemModel
import com.rokt.modelmapper.uimodel.ConditionalTransitionModifier
import com.rokt.modelmapper.uimodel.DataImageIndicators
import com.rokt.modelmapper.uimodel.DataImageTransition
import com.rokt.modelmapper.uimodel.DataImageTransition.Type
import com.rokt.modelmapper.uimodel.EqualityWhenUiCondition
import com.rokt.modelmapper.uimodel.ExistenceWhenUiCondition
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.OfferModel
import com.rokt.modelmapper.uimodel.OrderableWhenUiCondition
import com.rokt.modelmapper.uimodel.ProgressUiDirection
import com.rokt.modelmapper.uimodel.ResponseOptionModel
import com.rokt.modelmapper.uimodel.WhenUiHidden
import com.rokt.modelmapper.uimodel.WhenUiPredicate
import com.rokt.modelmapper.uimodel.WhenUiTransition
import com.rokt.network.model.BasicStateStylingBlock
import com.rokt.network.model.BooleanWhenCondition
import com.rokt.network.model.DataImageCarouselIndicatorStyles
import com.rokt.network.model.EqualityWhenCondition
import com.rokt.network.model.ExistenceWhenCondition
import com.rokt.network.model.InTransition
import com.rokt.network.model.IndicatorStyles
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.OrderableWhenCondition
import com.rokt.network.model.OutTransition
import com.rokt.network.model.ProgressIndicatorStyles
import com.rokt.network.model.ProgressionDirection
import com.rokt.network.model.WhenHidden
import com.rokt.network.model.WhenPredicate
import com.rokt.network.model.WhenTransition
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private const val defaultStartPosition = 1
private const val defaultAccessibilityHidden = true
private const val dataImageCarouselCustomKeyPrefix = "DataImageCarousel."

internal fun transformProgressIndicator(
    progressIndicatorModel: LayoutSchemaModel.ProgressIndicator,
    bindData: (value: String) -> BindData,
): LayoutSchemaUiModel.ProgressIndicatorUiModel {
    val ownStyles: ImmutableList<BasicStateStylingBlock<ProgressIndicatorStyles>>? =
        progressIndicatorModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )

    val conditionalStyleTransition = progressIndicatorModel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
                it.value.own?.container,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.ProgressIndicatorUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        startPosition = progressIndicatorModel.node.startPosition ?: defaultStartPosition,
        accessibilityHidden = progressIndicatorModel.node.accessibilityHidden ?: defaultAccessibilityHidden,
        indicatorText = bindData(progressIndicatorModel.node.indicator),
        indicator = transformProgressIndicatorItem(progressIndicatorModel.node.styles?.elements?.indicator),
        activeIndicator = progressIndicatorModel.node.styles?.elements?.activeIndicator?.let {
            transformProgressIndicatorItem(
                it,
            )
        },
        seenIndicator = progressIndicatorModel.node.styles?.elements?.seenIndicator?.let {
            transformProgressIndicatorItem(
                it,
            )
        },
    )
}

internal fun transformProgressIndicatorItem(
    indicator: List<BasicStateStylingBlock<IndicatorStyles>>?,
): LayoutSchemaUiModel.ProgressIndicatorItemUiModel {
    val ownModifiers = indicator?.toImmutableList().transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )

    return LayoutSchemaUiModel.ProgressIndicatorItemUiModel(
        ownModifiers = ownModifiers,
        containerProperties = indicator?.toImmutableList().transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        textStyles = indicator?.toImmutableList()
            ?.transformTextStyles { indicatorStyles ->
                indicatorStyles.toBasicStateStylingBlock { it.text }
            },
        conditionalTransitionModifiers = null,
    )
}

internal fun transformCreativeResponse(
    responseModel: LayoutSchemaModel.CreativeResponse,
    offerModel: OfferModel?,
    transformLayoutSchemaChildren: (model: LayoutSchemaModel, responseKey: String) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.CreativeResponseUiModel {
    val ownStyles = responseModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )
    val responseKey = responseModel.node.responseKey

    val conditionalStyleTransition = responseModel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
                it.value.own?.container,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.CreativeResponseUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        openLinks = transformOpenLinks(responseModel.node.openLinks),
        responseOption = bindModel<ResponseOptionModel>(responseKey, offerModel),
        children = responseModel.node.children.mapNotNull { child ->
            transformLayoutSchemaChildren(
                child,
                responseKey,
            )
        }.toImmutableList(),
    )
}

internal fun transformCatalogResponseButton(
    catalogResponseModel: LayoutSchemaModel.CatalogResponseButton,
    offerModel: OfferModel?,
    itemIndex: Int,
    transformLayoutSchemaChildren: (LayoutSchemaModel) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.CatalogResponseButtonUiModel {
    val ownStyles = catalogResponseModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )
    val conditionalStyleTransition = catalogResponseModel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
                it.value.own?.container,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.CatalogResponseButtonUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        children = catalogResponseModel.node.children.mapNotNull { child ->
            transformLayoutSchemaChildren(
                child,
            )
        }.toImmutableList(),
        catalogItemModel = bindModel<CatalogItemModel>(offerModel = offerModel, itemIndex = itemIndex)?.properties,
    )
}

internal fun transformCloseButton(
    closeButtonModel: LayoutSchemaModel.CloseButton,
    transformLayoutSchemaChildren: (LayoutSchemaModel) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.CloseButtonUiModel {
    val ownStyles = closeButtonModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )

    val conditionalStyleTransition = closeButtonModel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
                it.value.own?.container,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.CloseButtonUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        dismissalMethod = closeButtonModel.node.dismissalMethod,
        children = closeButtonModel.node.children.mapNotNull { child ->
            transformLayoutSchemaChildren(
                child,
            )
        }.toImmutableList(),
    )
}

internal fun transformStaticLink(
    staticLinkModel: LayoutSchemaModel.StaticLink,
    transformLayoutSchemaChildren: (LayoutSchemaModel) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.StaticLinkUiModel {
    val ownStyles = staticLinkModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )
    val conditionalStyleTransition = staticLinkModel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
                it.value.own?.container,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.StaticLinkUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        openLinks = transformOpenLinks(staticLinkModel.node.open),
        src = staticLinkModel.node.src,
        children = staticLinkModel.node.children.mapNotNull { child ->
            transformLayoutSchemaChildren(
                child,
            )
        }.toImmutableList(),
    )
}

internal fun transformToggleButtonStateTrigger(
    toggleButtonModel: LayoutSchemaModel.ToggleButtonStateTrigger,
    transformLayoutSchemaChildren: (LayoutSchemaModel) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.ToggleButtonStateTriggerUiModel {
    val ownStyles = toggleButtonModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )

    val conditionalStyleTransition = toggleButtonModel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
                it.value.own?.container,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.ToggleButtonStateTriggerUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        children = toggleButtonModel.node.children.mapNotNull { child ->
            transformLayoutSchemaChildren(
                child,
            )
        }.toImmutableList(),
        customStateKey = toggleButtonModel.node.customStateKey,
    )
}

internal fun transformProgressControl(
    progressControlModel: LayoutSchemaModel.ProgressControl,
    transformLayoutSchemaChildren: (LayoutSchemaModel) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.ProgressControlUiModel {
    val ownStyles = progressControlModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )

    val conditionalStyleTransition = progressControlModel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
                it.value.own?.container,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.ProgressControlUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        children = progressControlModel.node.children.mapNotNull { child ->
            transformLayoutSchemaChildren(
                child,
            )
        }.toImmutableList(),
        progressionDirection = progressControlModel.node.direction.toProgressionUiModel(),
    )
}

internal fun transformWhen(
    whenModel: LayoutSchemaModel.When,
    transformLayoutSchemaChildren: (LayoutSchemaModel) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.WhenUiModel = LayoutSchemaUiModel.WhenUiModel(
    predicates = whenModel.node.predicates.map { it.transformWhenPredicate() }.toImmutableList(),
    children = whenModel.node.children.mapNotNull { child ->
        transformLayoutSchemaChildren(child)
    }.toImmutableList(),
    transition = whenModel.node.transition?.toTransitionUiModel() ?: WhenUiTransition(
        EnterTransition.None,
        ExitTransition.None,
    ),
    hide = whenModel.node.hide?.toHideUiModel(),
)

internal fun WhenPredicate.transformWhenPredicate(): WhenUiPredicate = when (this) {
    is WhenPredicate.Breakpoint -> WhenUiPredicate.Breakpoint(
        condition = predicate.condition.toUiModel(),
        value = predicate.value,
    )

    is WhenPredicate.Position -> WhenUiPredicate.Position(
        condition = predicate.condition.toUiModel(),
        value = predicate.value,
    )

    is WhenPredicate.Progression -> WhenUiPredicate.Progression(
        condition = predicate.condition.toUiModel(),
        value = predicate.value,
    )

    is WhenPredicate.DarkMode -> WhenUiPredicate.DarkMode(
        condition = predicate.condition.toUiModel(),
        value = predicate.value,
    )

    is WhenPredicate.CreativeCopy -> WhenUiPredicate.CreativeCopy(
        condition = predicate.condition.toUiModel(),
        value = predicate.value,
    )

    is WhenPredicate.StaticBoolean -> WhenUiPredicate.StaticBoolean(
        condition = predicate.condition.toUiModel(),
        value = predicate.value,
    )

    is WhenPredicate.CustomState -> WhenUiPredicate.CustomState(
        condition = predicate.condition.toUiModel(),
        value = predicate.value,
        key = predicate.key,
    )

    is WhenPredicate.StaticString -> WhenUiPredicate.StaticString(
        condition = predicate.condition.toUiModel(),
        input = predicate.input,
        value = predicate.value,
    )

    else -> throw IllegalArgumentException()
}

internal fun transformDataImageCarousel(
    dataImageCarousel: LayoutSchemaModel.DataImageCarousel,
    offerModel: OfferModel?,
): LayoutSchemaUiModel.DataImageCarouselUiModel {
    val ownStyles = dataImageCarousel.node.styles?.elements?.own?.toImmutableList()
    val width = ownStyles?.firstOrNull()?.default?.dimension?.width
    val height = ownStyles?.firstOrNull()?.default?.dimension?.height
    val contentScale: ContentScale = ContentScale.Fit
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )

    val conditionalStyleTransition = dataImageCarousel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
                it.value.own?.container,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.DataImageCarouselUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        duration = dataImageCarousel.node.duration.toLong(),
        activeIndicator = dataImageCarousel.node.styles?.elements?.activeIndicator?.let {
            transformCarouselProgressIndicatorItem(it)
        },
        indicatorStyle = dataImageCarousel.node.styles?.elements?.indicator?.let {
            transformCarouselProgressIndicatorItem(it)
        },
        indicator = dataImageCarousel.node.indicators?.let {
            transformCarouselIndicators(it)
        },
        transition = dataImageCarousel.node.transition?.let {
            transformCarouselTransition(it)
        },
        seenIndicator = dataImageCarousel.node.styles?.elements?.seenIndicator?.let {
            transformCarouselProgressIndicatorItem(it)
        },
        progressIndicatorContainer = dataImageCarousel.node.styles?.elements?.progressIndicatorContainer?.let {
            transformCarouselProgressIndicatorItem(it)
        },
        images = getOfferImages(
            inputKey = dataImageCarousel.node.imageKey,
            offerModel = offerModel,
        ).mapValues { entry ->
            LayoutSchemaUiModel.ImageUiModel(
                ownModifiers = null, // it uses the modifiers from the DataImageCarousel
                containerProperties = null,
                conditionalTransitionModifiers = null,
                alt = entry.value.properties.get<String>(TypedKey<String>(KEY_ALT)),
                darkUrl = entry.value.properties.get<String>(TypedKey<String>(KEY_DARK)),
                lightUrl = entry.value.properties.get<String>(TypedKey<String>(KEY_LIGHT)).orEmpty(),
                title = entry.value.properties.get<String>(TypedKey<String>(KEY_TITLE)),
                scaleType = contentScale,
            )
        },
        customStateKey = "$dataImageCarouselCustomKeyPrefix${dataImageCarousel.node.imageKey}",
    )
}

internal fun transformCarouselProgressIndicatorItem(
    indicator: List<BasicStateStylingBlock<DataImageCarouselIndicatorStyles>>?,
): LayoutSchemaUiModel.ProgressIndicatorItemUiModel {
    val ownModifiers = indicator?.toImmutableList().transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )

    return LayoutSchemaUiModel.ProgressIndicatorItemUiModel(
        ownModifiers = ownModifiers,
        containerProperties = indicator?.toImmutableList().transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = null,
        textStyles = null,
    )
}

internal fun transformCarouselTransition(transition: Any?): DataImageTransition = when (transition) {
    is com.rokt.network.model.CarouselTransition.FadeInOut -> {
        val speed = transition.settings?.speed?.name
        DataImageTransition(
            type = DataImageTransition.Type.FadeInOut,
            settings = DataImageTransition.Settings(speed = speed),
        )
    }

    is com.rokt.network.model.CarouselTransition.SlideInOut -> {
        val speed = transition.settings?.speed?.name
        DataImageTransition(
            type = DataImageTransition.Type.SlideInOut,
            settings = DataImageTransition.Settings(speed = speed),
        )
    }

    else -> DataImageTransition(Type.None)
}

internal fun transformCarouselIndicators(indicators: Any?): DataImageIndicators = when (indicators) {
    is com.rokt.network.model.DataImageCarouselIndicators -> {
        val show = indicators.show ?: false
        val mode = when (indicators.activeIndicatorMode?.name?.lowercase()) {
            "timer" -> DataImageIndicators.Mode.Timer
            "manual" -> DataImageIndicators.Mode.Manual
            else -> DataImageIndicators.Mode.None
        }

        DataImageIndicators(show = show, activeIndicatorMode = mode)
    }

    else -> DataImageIndicators()
}

private fun OrderableWhenCondition.toUiModel() = when (this) {
    OrderableWhenCondition.Is -> OrderableWhenUiCondition.Is
    OrderableWhenCondition.IsNot -> OrderableWhenUiCondition.IsNot
    OrderableWhenCondition.IsBelow -> OrderableWhenUiCondition.IsBelow
    OrderableWhenCondition.IsAbove -> OrderableWhenUiCondition.IsAbove
}

private fun EqualityWhenCondition.toUiModel() = when (this) {
    EqualityWhenCondition.Is -> EqualityWhenUiCondition.Is
    EqualityWhenCondition.IsNot -> EqualityWhenUiCondition.IsNot
}

private fun BooleanWhenCondition.toUiModel() = when (this) {
    BooleanWhenCondition.IsTrue -> BooleanWhenUiCondition.IsTrue
    BooleanWhenCondition.IsFalse -> BooleanWhenUiCondition.IsFalse
}

private fun ExistenceWhenCondition.toUiModel() = when (this) {
    ExistenceWhenCondition.Exists -> ExistenceWhenUiCondition.Exists
    ExistenceWhenCondition.NotExists -> ExistenceWhenUiCondition.NotExists
}

private fun WhenTransition.toTransitionUiModel(): WhenUiTransition {
    var inUiTransition = EnterTransition.None
    inTransition?.forEach {
        when (it) {
            is InTransition.FadeIn -> inUiTransition += fadeIn(
                animationSpec = tween(durationMillis = it.settings.duration),
            )
        }
    }
    var outUiTransition = ExitTransition.None
    outTransition?.map {
        when (it) {
            is OutTransition.FadeOut -> outUiTransition += fadeOut(
                animationSpec = tween(durationMillis = it.settings.duration),
            )
        }
    }
    return WhenUiTransition(
        inTransition = inUiTransition,
        outTransition = outUiTransition,
    )
}

private fun WhenHidden.toHideUiModel() = when (this) {
    WhenHidden.Visually -> WhenUiHidden.Visually
    WhenHidden.Functionally -> WhenUiHidden.Functionally
}

private fun ProgressionDirection.toProgressionUiModel() = when (this) {
    ProgressionDirection.Forward -> ProgressUiDirection.Forward
    ProgressionDirection.Backward -> ProgressUiDirection.Backward
}
