package com.rokt.modelmapper.mappers

import androidx.compose.ui.layout.ContentScale
import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.data.bindModel
import com.rokt.modelmapper.hmap.TypedKey
import com.rokt.modelmapper.hmap.get
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_ALT
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_DARK
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_LIGHT
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_TITLE
import com.rokt.modelmapper.uimodel.ConditionalTransitionModifier
import com.rokt.modelmapper.uimodel.ConditionalTransitionTextStyling
import com.rokt.modelmapper.uimodel.CreativeIcon
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.Module
import com.rokt.modelmapper.uimodel.OfferImageModel
import com.rokt.modelmapper.uimodel.OfferModel
import com.rokt.network.model.BasicStateStylingBlock
import com.rokt.network.model.BasicTextStyle
import com.rokt.network.model.DataIconStyles
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.StaticIconStyles
import kotlinx.collections.immutable.toImmutableList

internal fun transformBasicText(
    basicTextModel: LayoutSchemaModel.BasicText,
    bindData: (value: String) -> BindData,
): LayoutSchemaUiModel.BasicTextUiModel {
    val ownStyles = basicTextModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
    )

    var conditionalTransitionModifiers: ConditionalTransitionModifier? = null
    var conditionalTransitionTextStyling: ConditionalTransitionTextStyling? = null
    basicTextModel.node.styles?.conditionalTransitions?.let {
        conditionalTransitionModifiers = ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
        conditionalTransitionTextStyling = ConditionalTransitionTextStyling(
            textStyles = transformTextStylingProperties(
                it.value.own?.text,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.BasicTextUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalTransitionModifiers,
        conditionalTransitionTextStyling = conditionalTransitionTextStyling,
        textStyles = ownStyles.transformTextStyles { ownStyle: BasicStateStylingBlock<BasicTextStyle> ->
            ownStyle.toBasicStateStylingBlock { it.text }
        },
        value = bindData(
            basicTextModel.node.value,
        ),
    )
}

internal fun transformRichText(
    richTextModel: LayoutSchemaModel.RichText,
    bindData: (value: String) -> BindData,
): LayoutSchemaUiModel.RichTextUiModel {
    val ownStyles = richTextModel.node.styles?.elements?.own?.toImmutableList()
    val linkStyles = richTextModel.node.styles?.elements?.link
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
    )

    var conditionalTransitionModifiers: ConditionalTransitionModifier? = null
    var conditionalTransitionTextStyling: ConditionalTransitionTextStyling? = null
    richTextModel.node.styles?.conditionalTransitions?.let {
        conditionalTransitionModifiers = ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
        conditionalTransitionTextStyling = ConditionalTransitionTextStyling(
            textStyles = transformTextStylingProperties(
                it.value.own?.text,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.RichTextUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalTransitionModifiers,
        conditionalTransitionTextStyling = conditionalTransitionTextStyling,
        textStyles = ownStyles.transformTextStyles { ownStyle ->
            ownStyle.toBasicStateStylingBlock { it.text }
        },
        linkStyles = linkStyles?.map { it.toTextStylingProperties() }?.toImmutableList()
            .transformTextStyles { linkStyle ->
                linkStyle.toBasicStateStylingBlock { it }
            },
        openLinks = transformOpenLinks(richTextModel.node.openLinks),
        value = bindData(
            richTextModel.node.value,
        ),
    )
}

internal fun transformDataImage(
    dataImageModel: LayoutSchemaModel.DataImage,
    offerModel: OfferModel?,
    module: Module,
    itemIndex: Int,
): LayoutSchemaUiModel.ImageUiModel {
    val ownStyles = dataImageModel.node.styles?.elements?.own?.toImmutableList()
    val width = ownStyles?.firstOrNull()?.default?.dimension?.width
    val height = ownStyles?.firstOrNull()?.default?.dimension?.height
    val contentScale: ContentScale = ContentScale.Fit
    val ownModifiers = ownStyles.transformModifier(
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
    )
    val boundModel = bindModel<OfferImageModel>(dataImageModel.node.imageKey, offerModel, module, itemIndex)

    val conditionalStyleTransition = dataImageModel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.ImageUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        lightUrl = boundModel?.properties?.get<String>(TypedKey<String>(KEY_LIGHT)).orEmpty(),
        darkUrl = boundModel?.properties?.get<String>(TypedKey<String>(KEY_DARK)),
        title = boundModel?.properties?.get<String>(TypedKey<String>(KEY_TITLE)),
        alt = boundModel?.properties?.get<String>(TypedKey<String>(KEY_ALT)),
        scaleType = contentScale,
    )
}

internal fun transformStaticImage(staticImageModel: LayoutSchemaModel.StaticImage): LayoutSchemaUiModel.ImageUiModel {
    val ownStyles = staticImageModel.node.styles?.elements?.own?.toImmutableList()
    val width = ownStyles?.firstOrNull()?.default?.dimension?.width
    val height = ownStyles?.firstOrNull()?.default?.dimension?.height
    val contentScale: ContentScale = ContentScale.Fit
    val ownModifiers = ownStyles.transformModifier(
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
    )

    val conditionalStyleTransition = staticImageModel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.ImageUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        lightUrl = staticImageModel.node.url.light,
        darkUrl = staticImageModel.node.url.dark,
        title = staticImageModel.node.title,
        scaleType = contentScale,
        alt = staticImageModel.node.alt,
    )
}

internal fun transformDataIcon(
    dataIconModel: LayoutSchemaModel.DataIcon,
    offerModel: OfferModel?,
    module: Module,
    itemIndex: Int,
): LayoutSchemaUiModel.IconUiModel {
    val ownStyles = dataIconModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
    )

    val conditionalStyleTransition = dataIconModel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    val boundModel = bindModel<CreativeIcon>(dataIconModel.node.iconKey, offerModel, module, itemIndex)?.name.orEmpty()

    return LayoutSchemaUiModel.IconUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        textStyles = ownStyles.transformTextStyles { ownStyle: BasicStateStylingBlock<DataIconStyles> ->
            ownStyle.toBasicStateStylingBlock { it.text }
        },
        value = boundModel,
        accessibilityHidden = true,
    )
}

internal fun transformStaticIcon(staticIconModel: LayoutSchemaModel.StaticIcon): LayoutSchemaUiModel.IconUiModel {
    val ownStyles = staticIconModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
    )

    val conditionalStyleTransition = staticIconModel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    val alt = staticIconModel.node.description

    return LayoutSchemaUiModel.IconUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        textStyles = ownStyles.transformTextStyles { ownStyle: BasicStateStylingBlock<StaticIconStyles> ->
            ownStyle.toBasicStateStylingBlock { it.text }
        },
        value = staticIconModel.node.name,
        accessibilityHidden = alt?.isEmpty() == true,
        alt = alt,
    )
}
