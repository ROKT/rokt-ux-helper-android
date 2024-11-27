package com.rokt.roktux.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.PeekThroughSizeUiModel
import com.rokt.modelmapper.utils.DEFAULT_VIEWABLE_ITEMS
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

internal class CarouselDistributionComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.CarouselDistributionUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.CarouselDistributionUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val pagerState = rememberPagerState { offerState.lastOfferIndex + 1 }
        var canScroll by remember { mutableStateOf(true) }
        val coroutineScope = rememberCoroutineScope()
        var viewWidth by remember { mutableIntStateOf(0) }
        val viewableItems = getViewableItems(
            breakpointIndex = breakpointIndex,
            viewableItemsList = model.viewableItems,
            lastOfferIndex = offerState.lastOfferIndex,
        )
        onEventSent(LayoutContract.LayoutEvent.ViewableItemsChanged(viewableItems))
        val container = modifierFactory.createContainerUiProperties(
            containerProperties = model.containerProperties,
            index = breakpointIndex,
            isPressed = isPressed,
        )
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                onEventSent(
                    LayoutContract.LayoutEvent.LayoutVariantSwiped(
                        page,
                    ),
                )
            }
        }
        LaunchedEffect(key1 = offerState.targetOfferIndex) {
            coroutineScope.launch {
                if (offerState.targetOfferIndex != (pagerState.currentPage)) {
                    canScroll = false
                    pagerState.animateScrollToPage(offerState.targetOfferIndex)
                    onEventSent(LayoutContract.LayoutEvent.SetCurrentOffer(offerState.targetOfferIndex))
                    canScroll = true
                }
            }
        }

        HorizontalPager(
            modifier = modifierFactory
                .createModifier(
                    modifierPropertiesList = model.ownModifiers,
                    conditionalTransitionModifier = model.conditionalTransitionModifiers,
                    breakpointIndex = breakpointIndex,
                    isPressed = isPressed,
                    isDarkModeEnabled = isDarkModeEnabled,
                    offerState = offerState,
                )
                .then(modifier)
                .onSizeChanged {
                    viewWidth = it.width
                },
            verticalAlignment = BiasAlignment.Vertical(
                container.alignmentBias,
            ),
            state = pagerState,
            pageSize = carouselPageSize(viewableItems),
            contentPadding = getPeekThroughDimension(
                breakpointIndex = breakpointIndex,
                viewWidth = viewWidth,
                peekThroughSizeItems = model.peekThroughSizeUiModel,
                viewableItems = viewableItems,
            ),
            pageSpacing = container.gap ?: 0.dp,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                pagerSnapDistance = PagerSnapDistance.atMost(viewableItems),
            ),
            userScrollEnabled = canScroll,
        ) { page ->
            factory.CreateComposable(
                model = LayoutSchemaUiModel.MarketingUiModel(),
                modifier = modifier,
                isPressed = isPressed,
                offerState = offerState.copy(currentOfferIndex = page, viewableItems = viewableItems),
                isDarkModeEnabled = isDarkModeEnabled,
                breakpointIndex = breakpointIndex,
            ) { event ->
                coroutineScope.launch {
                    if (event is LayoutContract.LayoutEvent.ResponseOptionSelected) {
                        // Only progress to next offer if viewableItems is 1
                        if (viewableItems == DEFAULT_VIEWABLE_ITEMS) {
                            onEventSent(event.copy(shouldProgress = true))
                        }
                    }
                    onEventSent.invoke(event)
                }
            }
        }
        LaunchedEffect(key1 = Unit) {
            onEventSent(
                LayoutContract.LayoutEvent.FirstOfferLoaded,
            )
        }
    }
}

@Composable
private fun getPeekThroughDimension(
    breakpointIndex: Int,
    viewWidth: Int,
    peekThroughSizeItems: ImmutableList<PeekThroughSizeUiModel>,
    viewableItems: Int,
): PaddingValues = remember(breakpointIndex, viewWidth, viewableItems) {
    if (peekThroughSizeItems.isEmpty()) {
        PaddingValues(0.dp)
    } else {
        val peekThroughBreakpointIndex = if (breakpointIndex <= peekThroughSizeItems.size - 1) {
            breakpointIndex
        } else {
            peekThroughSizeItems.size - 1
        }
        val transformedPeekThroughSize = when (val peekThroughSize = peekThroughSizeItems[peekThroughBreakpointIndex]) {
            is PeekThroughSizeUiModel.Fixed -> peekThroughSize.value.dp
            is PeekThroughSizeUiModel.Percentage ->
                (viewWidth.toFloat() * (peekThroughSize.value / 100)).dp
        }
        PaddingValues(
            transformedPeekThroughSize,
        )
    }
}

private fun carouselPageSize(viewableItems: Int) = object : PageSize {
    override fun Density.calculateMainAxisPageSize(availableSpace: Int, pageSpacing: Int): Int =
        availableSpace / viewableItems
}
