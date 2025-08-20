package com.rokt.roktux.component

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.PeekThroughSizeUiModel
import com.rokt.modelmapper.utils.DEFAULT_VIEWABLE_ITEMS
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

private const val ACCESSIBILITY_READOUT_TEXT = "Page %d of %d"

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
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }
        val coroutineScope = rememberCoroutineScope()
        var viewWidth by remember { mutableIntStateOf(0) }

        val allChildrenHeights = remember { mutableStateOf<List<Dp>>(emptyList()) }
        val maxHeight = remember { mutableStateOf(0.dp) }

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
                    // requestFocus only works a single time so we need to clear focus and request it again after delay
                    // this is because the focus Active state is maintained and not automatically set to Inactive
                    // see: androidx.compose.ui.focus.FocusTransactions.kt#64
                    focusManager.clearFocus(true)
                    delay(10)
                    focusRequester.requestFocus()
                }
            }
        }

        // Calculate the content padding and page spacing that will be applied
        val contentPadding = getPeekThroughDimension(
            breakpointIndex = breakpointIndex,
            viewWidth = viewWidth,
            peekThroughSizeItems = model.peekThroughSizeUiModel,
            viewableItems = viewableItems,
        )
        val pageSpacing = container.gap ?: 0.dp

        // Get the layout direction for calculations
        val layoutDirection = LocalLayoutDirection.current

        Box(
            modifier = Modifier
                .semantics {
                    contentDescription =
                        getAccessibilityDescription(offerState)
                }
                .focusRequester(
                    focusRequester,
                )
                .focusable(),
        ) {
            SubcomposeLayout(
                modifier = Modifier.fillMaxWidth()
            ) { constraints ->
                val measuredHeights = mutableListOf<Dp>()

                // Calculate the actual available width for each page content
                val totalHorizontalPadding = (contentPadding.calculateStartPadding(layoutDirection) +
                    contentPadding.calculateEndPadding(layoutDirection)).toPx()
                val totalPageSpacing = (pageSpacing * (viewableItems - 1)).toPx()
                val availableWidthForContent = (constraints.maxWidth - totalHorizontalPadding - totalPageSpacing) / viewableItems

                // Create adjusted constraints that respect the actual available space per page
                val adjustedConstraints = constraints.copy(
                    maxWidth = availableWidthForContent.toInt(),
                    minWidth = availableWidthForContent.toInt()
                )

                for (pageIndex in 0 until pagerState.pageCount) {
                    val subcomposables = subcompose(pageIndex) {
                        factory.CreateComposable(
                            model = LayoutSchemaUiModel.MarketingUiModel(true),
                            modifier = modifier,
                            isPressed = isPressed,
                            offerState = offerState.copy(currentOfferIndex = pageIndex, viewableItems = viewableItems),
                            isDarkModeEnabled = isDarkModeEnabled,
                            breakpointIndex = breakpointIndex,
                        ) { event -> }
                    }

                    val placeable = subcomposables.firstOrNull()?.measure(adjustedConstraints)

                    with(this) {
                        placeable?.let {
                            val viewHeight = it.height.toDp()
                            measuredHeights.add(viewHeight)
                        }
                    }
                }

                allChildrenHeights.value = measuredHeights
                maxHeight.value = measuredHeights.maxOrNull() ?: 0.dp

                val pagerPlaceable = subcompose(pagerState) {
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
                            modifier = modifier
                                .height(maxHeight.value),
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
                                    } else {
                                        onEventSent(event)
                                    }
                                } else {
                                    onEventSent(event)
                                }
                            }
                        }
                    }
                }.first().measure(constraints)

                layout(pagerPlaceable.width, pagerPlaceable.height) {
                    pagerPlaceable.placeRelative(0, 0)
                }
            }
            LaunchedEffect(key1 = Unit) {
                onEventSent(
                    LayoutContract.LayoutEvent.FirstOfferLoaded,
                )
            }
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
            start = transformedPeekThroughSize,
            end = transformedPeekThroughSize,
        )
    }
}

private fun carouselPageSize(viewableItems: Int) = object : PageSize {
    override fun Density.calculateMainAxisPageSize(availableSpace: Int, pageSpacing: Int): Int =
        availableSpace / viewableItems
}

private fun getAccessibilityDescription(offerState: OfferUiState): String = ACCESSIBILITY_READOUT_TEXT.format(
    ceil((offerState.currentOfferIndex + 1).toDouble() / offerState.viewableItems).toInt(),
    ceil((offerState.lastOfferIndex + 1).toDouble() / offerState.viewableItems).toInt(),
)
