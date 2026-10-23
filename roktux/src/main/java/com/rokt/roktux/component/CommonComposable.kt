package com.rokt.roktux.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.rokt.modelmapper.uimodel.ButtonUiModel
import com.rokt.modelmapper.uimodel.ConditionalStyleState
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.WhenUiPredicate
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.ImmutableList

// Carousels and galleries measure through SubcomposeLayout, which cannot answer intrinsic size queries.
// Distributions and layout variants count only when one of the experience's offer layouts holds one.
internal fun LayoutSchemaUiModel.hostsSubcomposition(offerLayoutsHostSubcomposition: Boolean): Boolean = when (this) {
    is LayoutSchemaUiModel.DataImageCarouselUiModel,
    is LayoutSchemaUiModel.CatalogImageGalleryUiModel,
    is LayoutSchemaUiModel.CarouselDistributionUiModel,
    -> true

    is LayoutSchemaUiModel.OneByOneDistributionUiModel,
    is LayoutSchemaUiModel.GroupedDistributionUiModel,
    is LayoutSchemaUiModel.MarketingUiModel,
    -> offerLayoutsHostSubcomposition

    is LayoutSchemaUiModel.RowUiModel -> children.anyHostsSubcomposition(offerLayoutsHostSubcomposition)

    is LayoutSchemaUiModel.ColumnUiModel -> children.anyHostsSubcomposition(offerLayoutsHostSubcomposition)

    is LayoutSchemaUiModel.BoxUiModel -> children.anyHostsSubcomposition(offerLayoutsHostSubcomposition)

    is LayoutSchemaUiModel.WhenUiModel -> children.anyHostsSubcomposition(offerLayoutsHostSubcomposition)

    is LayoutSchemaUiModel.CatalogStackedCollectionUiModel ->
        children.anyHostsSubcomposition(offerLayoutsHostSubcomposition)

    is ButtonUiModel -> children.anyHostsSubcomposition(offerLayoutsHostSubcomposition)

    is LayoutSchemaUiModel.CatalogCombinedCollectionUiModel ->
        childrenByCatalogItem.values.any { it.anyHostsSubcomposition(offerLayoutsHostSubcomposition) }

    is LayoutSchemaUiModel.OverlayUiModel -> child.hostsSubcomposition(offerLayoutsHostSubcomposition)

    is LayoutSchemaUiModel.BottomSheetUiModel -> child.hostsSubcomposition(offerLayoutsHostSubcomposition)

    else -> false
}

private fun List<LayoutSchemaUiModel?>.anyHostsSubcomposition(offerLayoutsHostSubcomposition: Boolean) =
    any { it?.hostsSubcomposition(offerLayoutsHostSubcomposition) == true }

// Whether any offer layout of the experience hosts subcomposition. Without a provider every distribution counts.
internal val LocalOfferLayoutsHostSubcomposition = compositionLocalOf { true }

// A pager or scrolling row must be measured with a finite width. Under a horizontal scroll the width
// is unbounded, so cap it at the window width; a width the layout already bounds is left unchanged.
@Composable
internal fun Modifier.boundedWidthWhenUnbounded(): Modifier {
    val windowWidthPx = LocalWindowInfo.current.containerSize.width.takeIf { it > 0 }
        ?: with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.roundToPx() }
    return layout { measurable, constraints ->
        val placeable = measurable.measure(
            if (constraints.hasBoundedWidth) {
                constraints
            } else {
                constraints.copy(maxWidth = maxOf(windowWidthPx, constraints.minWidth))
            },
        )
        layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }
}

@Composable
internal fun evaluateState(
    predicates: ImmutableList<WhenUiPredicate>?,
    breakpointIndex: Int,
    isDarkModeEnabled: Boolean,
    offerState: OfferUiState,
): ConditionalStyleState? = predicates.takeIf { it?.isNotEmpty() == true }?.let {
    if (evaluateWhenPredicates(
            predicates = it,
            breakpointIndex = breakpointIndex,
            isDarkModeEnabled = isDarkModeEnabled,
            offerState = offerState,
        )
    ) {
        ConditionalStyleState.Transition
    } else {
        ConditionalStyleState.Normal
    }
}
