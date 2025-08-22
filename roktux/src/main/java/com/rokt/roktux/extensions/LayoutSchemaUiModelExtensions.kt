package com.rokt.roktux.extensions

import com.rokt.modelmapper.uimodel.HeightUiModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.ModifierProperties
import com.rokt.modelmapper.uimodel.StateBlock
import kotlinx.collections.immutable.toImmutableList

/**
 * Extension functions for LayoutSchemaUiModel transformations
 * Note: These functions work with the sealed class pattern where each subtype is a data class
 */
fun LayoutSchemaUiModel.transformModifiers(
    predicate: (StateBlock<ModifierProperties>) -> Boolean,
    transformation: (StateBlock<ModifierProperties>) -> StateBlock<ModifierProperties>,
): LayoutSchemaUiModel {
    if (!hasModifiersMatching(predicate)) {
        return this
    }

    val modifiedModifiers = ownModifiers?.map { modifier ->
        if (predicate(modifier)) {
            transformation(modifier)
        } else {
            modifier
        }
    }?.toImmutableList()

    return when (this) {
        is LayoutSchemaUiModel.MarketingUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.BasicTextUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.RichTextUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.ColumnUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.RowUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.BoxUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.CatalogStackedCollectionUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.ProgressIndicatorUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.ProgressIndicatorItemUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.CreativeResponseUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.CloseButtonUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.CatalogResponseButtonUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.StaticLinkUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.ToggleButtonStateTriggerUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.ProgressControlUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.OneByOneDistributionUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.GroupedDistributionUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.CarouselDistributionUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.OverlayUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.BottomSheetUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.ImageUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.IconUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.WhenUiModel -> copy(ownModifiers = modifiedModifiers)
        is LayoutSchemaUiModel.DataImageCarouselUiModel -> copy(ownModifiers = modifiedModifiers)
        else -> this
    }
}

/**
 * Checks if any modifiers in this LayoutSchemaUiModel match the given predicate
 */
fun LayoutSchemaUiModel.hasModifiersMatching(predicate: (StateBlock<ModifierProperties>) -> Boolean): Boolean =
    ownModifiers?.any { predicate(it) } == true

fun LayoutSchemaUiModel.transformHeightForAllMatching(
    condition: (StateBlock<ModifierProperties>) -> Boolean,
    newHeight: HeightUiModel,
): LayoutSchemaUiModel = transformModifiers(
    predicate = { modifier -> condition(modifier) },
    transformation = { modifier ->
        StateBlock(
            default = modifier.default.copy(height = newHeight),
            pressed = modifier.pressed,
        )
    },
)
