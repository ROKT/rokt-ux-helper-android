package com.rokt.modelmapper.mappers

import com.rokt.modelmapper.uimodel.ConditionalTransitionModifier
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.PeekThroughSizeUiModel
import com.rokt.modelmapper.uimodel.TransitionUiModel
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.PeekThroughSize
import com.rokt.network.model.Transition
import kotlinx.collections.immutable.toImmutableList

internal fun transformOneByOneDistribution(
    oneByOneDistributionModel: LayoutSchemaModel.OneByOneDistribution,
): LayoutSchemaUiModel.OneByOneDistributionUiModel {
    val ownStyles =
        oneByOneDistributionModel.node.styles?.elements?.own?.toImmutableList().toBasicStateStylingBlock()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )

    val conditionalStyleTransition = oneByOneDistributionModel.node.styles?.conditionalTransitions?.let {
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

    return LayoutSchemaUiModel.OneByOneDistributionUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        transition = oneByOneDistributionModel.node.transition.toTransitionUiModel(),
    )
}

internal fun transformGroupedDistribution(
    groupedDistributionModel: LayoutSchemaModel.GroupedDistribution,
): LayoutSchemaUiModel.GroupedDistributionUiModel {
    val ownStyles =
        groupedDistributionModel.node.styles?.elements?.own?.toImmutableList().toBasicStateStylingBlock()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )
    val conditionalStyleTransition = groupedDistributionModel.node.styles?.conditionalTransitions?.let {
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

    return LayoutSchemaUiModel.GroupedDistributionUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        viewableItems = groupedDistributionModel.node.viewableItems.map { it.toInt() }.toImmutableList(),
        transition = groupedDistributionModel.node.transition.toTransitionUiModel(),
    )
}

internal fun transformCarouselDistribution(
    carouselDistributionModel: LayoutSchemaModel.CarouselDistribution,
): LayoutSchemaUiModel.CarouselDistributionUiModel {
    val ownStyles =
        carouselDistributionModel.node.styles?.elements?.own?.toImmutableList().toBasicStateStylingBlock()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )

    val conditionalStyleTransition = carouselDistributionModel.node.styles?.conditionalTransitions?.let {
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

    return LayoutSchemaUiModel.CarouselDistributionUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        viewableItems = carouselDistributionModel.node.viewableItems.map { it.toInt() }.toImmutableList(),
        peekThroughSizeUiModel = carouselDistributionModel.node.peekThroughSize.map { peekThroughSize ->
            when (peekThroughSize) {
                is PeekThroughSize.Fixed -> PeekThroughSizeUiModel.Fixed(peekThroughSize.value)
                is PeekThroughSize.Percentage -> PeekThroughSizeUiModel.Percentage(peekThroughSize.value)
            }
        }.toImmutableList(),
    )
}

private fun Transition.toTransitionUiModel(): TransitionUiModel = when (this) {
    is Transition.FadeInOut -> TransitionUiModel.FadeInOutTransition(this.settings.duration)
}
