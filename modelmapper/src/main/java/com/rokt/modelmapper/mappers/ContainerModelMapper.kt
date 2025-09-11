package com.rokt.modelmapper.mappers

import com.rokt.modelmapper.uimodel.ConditionalTransitionModifier
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.ModifierProperties
import com.rokt.modelmapper.uimodel.Module
import com.rokt.modelmapper.uimodel.OfferModel
import com.rokt.modelmapper.uimodel.StateBlock
import com.rokt.modelmapper.uimodel.WidthUiModel
import com.rokt.network.model.BasicStateStylingBlock
import com.rokt.network.model.CatalogStackedCollectionLayoutSchemaTemplateNode
import com.rokt.network.model.ColumnElements
import com.rokt.network.model.ColumnModel
import com.rokt.network.model.ColumnStyle
import com.rokt.network.model.ContainerStylingProperties
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.LayoutStyle
import com.rokt.network.model.RowElements
import com.rokt.network.model.RowModel
import com.rokt.network.model.RowStyle
import com.rokt.network.model.ScrollableColumnStyle
import com.rokt.network.model.ScrollableRowStyle
import com.rokt.network.model.ZStackContainerStylingProperties
import kotlinx.collections.immutable.toImmutableList

internal fun transformColumn(
    columnModel: LayoutSchemaModel.Column,
    isScrollable: Boolean,
    transformLayoutSchemaChildren: (LayoutSchemaModel) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.ColumnUiModel {
    val ownStyles = columnModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )

    val conditionalStyleTransition = columnModel.node.styles?.conditionalTransitions?.let {
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

    return LayoutSchemaUiModel.ColumnUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        isScrollable = isScrollable,
        children = columnModel.node.children.mapNotNull { child ->
            transformLayoutSchemaChildren(
                child,
            )
        }.toImmutableList(),
    )
}

internal fun transformRow(
    rowModel: LayoutSchemaModel.Row,
    isScrollable: Boolean,
    transformLayoutSchemaChildren: (LayoutSchemaModel) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.RowUiModel {
    val ownStyles = rowModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )?.let { modifiers ->
        if (modifiers.isNotEmpty() && modifiers[0].default.width == null) {
            modifiers.map {
                StateBlock(
                    default = it.default.copy(width = WidthUiModel.MatchParent),
                    pressed = it.pressed,
                )
            }.toImmutableList()
        } else {
            modifiers
        }
    } ?: listOf(StateBlock(default = ModifierProperties(width = WidthUiModel.MatchParent))).toImmutableList()

    val conditionalStyleTransition = rowModel.node.styles?.conditionalTransitions?.let {
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

    return LayoutSchemaUiModel.RowUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        isScrollable = isScrollable,
        children = rowModel.node.children.mapNotNull { child ->
            transformLayoutSchemaChildren(
                child,
            )
        }.toImmutableList(),
    )
}

internal fun transformZStack(
    zStackModel: LayoutSchemaModel.ZStack,
    transformLayoutSchemaChildren: (LayoutSchemaModel) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.BoxUiModel {
    val ownStyles = zStackModel.node.styles?.elements?.own?.toImmutableList()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle ->
            ownStyle.toBasicStateStylingBlock { style -> style.container?.toContainerStyling() }
        },
    )

    val conditionalStyleTransition = zStackModel.node.styles?.conditionalTransitions?.let {
        ConditionalTransitionModifier(
            modifier = transformModifier(
                it.value.own?.spacing,
                it.value.own?.dimension,
                it.value.own?.background,
                it.value.own?.border,
                it.value.own?.container?.toContainerStyling(),
            ),
            predicates = it.predicates.map { predicate -> predicate.transformWhenPredicate() }.toImmutableList(),
            duration = it.duration,
        )
    }

    return LayoutSchemaUiModel.BoxUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle ->
                ownStyle.toBasicStateStylingBlock { style -> style.container?.toContainerStyling() }
            },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        children = zStackModel.node.children.mapNotNull { child ->
            transformLayoutSchemaChildren(
                child,
            )
        }.toImmutableList(),
    )
}

internal fun transformCatalogStackedCollection(
    catalogStackedCollection: LayoutSchemaModel.CatalogStackedCollection,
    offerModel: OfferModel?,
    transformLayoutSchemaChildren: (Int, Module, LayoutSchemaModel) -> LayoutSchemaUiModel?,
): LayoutSchemaUiModel.CatalogStackedCollectionUiModel {
    val ownStyles = catalogStackedCollection.node.styles?.elements?.own?.toImmutableList().toBasicStateStylingBlock()
    val ownModifiers = ownStyles.transformModifier(
        transformSpacing = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.spacing } },
        transformDimension = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.dimension } },
        transformBackground = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.background } },
        transformBorder = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.border } },
        transformContainer = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.container } },
    )

    val conditionalStyleTransition = catalogStackedCollection.node.styles?.conditionalTransitions?.let {
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
    val catalogItemList = mutableListOf<LayoutSchemaUiModel>()
    offerModel?.catalogItems?.forEachIndexed { i, _ ->
        transformLayoutSchemaChildren(
            i,
            Module.AddToCart,
            catalogStackedCollection.node.template.toLayoutSchemaModel(),
        )?.let {
            catalogItemList.add(i, it)
        }
    }
    return LayoutSchemaUiModel.CatalogStackedCollectionUiModel(
        ownModifiers = ownModifiers,
        containerProperties = ownStyles.transformContainer(
            transformContainer = { ownStyle ->
                ownStyle.toBasicStateStylingBlock { style -> style.container }
            },
            transformFlexChild = { ownStyle -> ownStyle.toBasicStateStylingBlock { style -> style.flexChild } },
        ),
        conditionalTransitionModifiers = conditionalStyleTransition,
        children = catalogItemList.toImmutableList(),
    )
}

private fun CatalogStackedCollectionLayoutSchemaTemplateNode.toLayoutSchemaModel(): LayoutSchemaModel = when (this) {
    is CatalogStackedCollectionLayoutSchemaTemplateNode.Column -> LayoutSchemaModel.Column(node = this.node)
    is CatalogStackedCollectionLayoutSchemaTemplateNode.Row -> LayoutSchemaModel.Row(node = this.node)
}

private fun ZStackContainerStylingProperties.toContainerStyling() = ContainerStylingProperties(
    justifyContent = justifyContent,
    alignItems = alignItems,
    shadow = shadow,
    overflow = overflow,
    gap = null,
    blur = blur,
    opacity = opacity,
)

internal fun LayoutSchemaModel.ScrollableRow.toRow(): LayoutSchemaModel.Row {
    val styles = this.node.styles?.elements?.own?.map {
        BasicStateStylingBlock(
            it.default.toRowStyle(),
            it.pressed?.toRowStyle(),
            it.hovered?.toRowStyle(),
            it.focussed?.toRowStyle(),
            it.disabled?.toRowStyle(),
        )
    }
    val transitions = null

    return LayoutSchemaModel.Row(
        RowModel(
            styles = styles?.let { LayoutStyle(RowElements(it), transitions) },
            children = this.node.children,
        ),
    )
}

internal fun LayoutSchemaModel.ScrollableColumn.toColumn(): LayoutSchemaModel.Column {
    val styles = this.node.styles?.elements?.own?.map {
        BasicStateStylingBlock(
            it.default.toColumnStyle(),
            it.pressed?.toColumnStyle(),
            it.hovered?.toColumnStyle(),
            it.focussed?.toColumnStyle(),
            it.disabled?.toColumnStyle(),
        )
    }
    val transitions = null

    return LayoutSchemaModel.Column(
        ColumnModel(
            styles = styles?.let { LayoutStyle(ColumnElements(it), transitions) },
            children = this.node.children,
        ),
    )
}

private fun ScrollableRowStyle.toRowStyle(): RowStyle = RowStyle(
    container,
    background,
    border,
    dimension,
    flexChild,
    spacing,
)

private fun ScrollableColumnStyle.toColumnStyle(): ColumnStyle = ColumnStyle(
    container,
    background,
    border,
    dimension,
    flexChild,
    spacing,
)
