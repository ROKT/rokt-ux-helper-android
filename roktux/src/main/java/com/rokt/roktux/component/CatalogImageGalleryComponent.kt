package com.rokt.roktux.component

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.utils.ROKT_ICONS_FONT_FAMILY
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

@OptIn(ExperimentalComposeUiApi::class)
internal class CatalogImageGalleryComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.CatalogImageGalleryUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.CatalogImageGalleryUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val container = modifierFactory.createContainerUiProperties(
            containerProperties = model.containerProperties,
            index = breakpointIndex,
            isPressed = isPressed,
        )
        val galleryImages = remember(model.images, isDarkModeEnabled) {
            model.images
                .asSequence()
                .mapNotNull { (key, image) ->
                    val url = if (isDarkModeEnabled) image.darkUrl ?: image.lightUrl else image.lightUrl
                    if (url.isNotEmpty()) {
                        key to image
                    } else {
                        null
                    }
                }
                .sortedBy { (key, _) -> key }
                .map { (_, image) -> image }
                .toList()
        }

        if (galleryImages.isEmpty()) {
            LaunchedEffect(Unit) {
                onEventSent(LayoutContract.LayoutEvent.SetCustomState(model.customStateKey, -1))
            }
            return
        }

        val selectedPageStateValue = offerState.customState[model.customStateKey]
        val selectedPage = ((selectedPageStateValue ?: 1) - 1)
            .coerceIn(0, galleryImages.lastIndex)

        Box(
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
                .then(
                    model.a11yLabel?.let { label ->
                        Modifier.semantics { contentDescription = label }
                    } ?: Modifier,
                ),
            contentAlignment = BiasAlignment(container.arrangementBias, container.alignmentBias),
        ) {
            val pagerState = rememberPagerState { galleryImages.size }

            fun navigateToPage(targetPage: Int) {
                val page = targetPage.coerceIn(0, galleryImages.lastIndex)
                if (page != pagerState.currentPage) {
                    onEventSent(
                        LayoutContract.LayoutEvent.SetCustomState(
                            model.customStateKey,
                            page + 1,
                        ),
                    )
                }
            }

            LaunchedEffect(selectedPage, galleryImages.size) {
                if (pagerState.currentPage != selectedPage) {
                    pagerState.requestScrollToPage(selectedPage)
                }
            }

            LaunchedEffect(pagerState.currentPage, galleryImages.size) {
                val currentPageStateValue = pagerState.currentPage + 1
                if (selectedPageStateValue != currentPageStateValue) {
                    onEventSent(
                        LayoutContract.LayoutEvent.SetCustomState(
                            model.customStateKey,
                            currentPageStateValue,
                        ),
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.catalogGalleryTapNavigation(
                    enabled = pagerState.pageCount > 1,
                    onTapBackward = { navigateToPage(pagerState.currentPage - 1) },
                    onTapForward = { navigateToPage(pagerState.currentPage + 1) },
                ),
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    snapAnimationSpec = tween(),
                ),
            ) { page ->
                factory.CreateComposable(
                    model = galleryImages[page],
                    modifier = Modifier.fillMaxWidth(),
                    isPressed = isPressed,
                    offerState = offerState,
                    isDarkModeEnabled = isDarkModeEnabled,
                    breakpointIndex = breakpointIndex,
                    onEventSent = onEventSent,
                )
            }

            NavigationButtons(
                model = model,
                selectedPage = pagerState.currentPage,
                pageCount = pagerState.pageCount,
                isDarkModeEnabled = isDarkModeEnabled,
                breakpointIndex = breakpointIndex,
                offerState = offerState,
                onEventSent = onEventSent,
                onBackwardSelected = { navigateToPage(pagerState.currentPage - 1) },
                onForwardSelected = { navigateToPage(pagerState.currentPage + 1) },
            )

            if (model.showIndicators && pagerState.pageCount > 1 && model.indicatorStyle != null) {
                IndicatorRow(
                    model = model,
                    selectedPage = pagerState.currentPage,
                    pageCount = pagerState.pageCount,
                    isPressed = isPressed,
                    offerState = offerState,
                    isDarkModeEnabled = isDarkModeEnabled,
                    breakpointIndex = breakpointIndex,
                    onIndicatorSelected = ::navigateToPage,
                )
            }
        }
    }

    @Composable
    private fun BoxScope.NavigationButtons(
        model: LayoutSchemaUiModel.CatalogImageGalleryUiModel,
        selectedPage: Int,
        pageCount: Int,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        offerState: OfferUiState,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
        onBackwardSelected: () -> Unit,
        onForwardSelected: () -> Unit,
    ) {
        val canGoBackward = selectedPage > 0 && !model.backwardIcon.isNullOrBlank()
        val canGoForward = selectedPage < pageCount - 1 && !model.forwardIcon.isNullOrBlank()
        if (!canGoBackward && !canGoForward) {
            return
        }

        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (canGoBackward) {
                NavigationButton(
                    icon = model.backwardIcon.orEmpty(),
                    contentDescription = PreviousImageContentDescription,
                    model = model.controlButton,
                    isDarkModeEnabled = isDarkModeEnabled,
                    breakpointIndex = breakpointIndex,
                    offerState = offerState,
                    onEventSent = onEventSent,
                    onClick = onBackwardSelected,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (canGoForward) {
                NavigationButton(
                    icon = model.forwardIcon.orEmpty(),
                    contentDescription = NextImageContentDescription,
                    model = model.controlButton,
                    isDarkModeEnabled = isDarkModeEnabled,
                    breakpointIndex = breakpointIndex,
                    offerState = offerState,
                    onEventSent = onEventSent,
                    onClick = onForwardSelected,
                )
            }
        }
    }

    @Composable
    private fun NavigationButton(
        icon: String,
        contentDescription: String,
        model: LayoutSchemaUiModel.CatalogImageGalleryControlButtonUiModel?,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        offerState: OfferUiState,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
        onClick: () -> Unit,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val container = modifierFactory.createContainerUiProperties(
            containerProperties = model?.containerProperties,
            index = breakpointIndex,
            isPressed = isPressed,
        )
        val textStyleUiState = modifierFactory.createTextStyle(
            text = icon,
            textStyles = model?.textStyles,
            breakpointIndex = breakpointIndex,
            isPressed = isPressed,
            isDarkModeEnabled = isDarkModeEnabled,
            defaultFontFamily = ROKT_ICONS_FONT_FAMILY,
            offerState = offerState,
            onEventSent = onEventSent,
        )

        Box(
            modifier = modifierFactory
                .createModifier(
                    modifierPropertiesList = model?.ownModifiers,
                    conditionalTransitionModifier = model?.conditionalTransitionModifiers,
                    breakpointIndex = breakpointIndex,
                    isPressed = isPressed,
                    isDarkModeEnabled = isDarkModeEnabled,
                    offerState = offerState,
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                )
                .semantics {
                    this.contentDescription = contentDescription
                },
            contentAlignment = BiasAlignment(container.arrangementBias, container.alignmentBias),
        ) {
            Text(
                text = textStyleUiState.value,
                style = textStyleUiState.textStyle,
            )
        }
    }

    @Composable
    private fun BoxScope.IndicatorRow(
        model: LayoutSchemaUiModel.CatalogImageGalleryUiModel,
        selectedPage: Int,
        pageCount: Int,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onIndicatorSelected: (Int) -> Unit,
    ) {
        val wrapperContainer = modifierFactory.createContainerUiProperties(
            containerProperties = model.progressIndicatorContainer?.containerProperties,
            index = breakpointIndex,
            isPressed = isPressed,
        )
        val selfAlignment = wrapperContainer.selfAlignmentBias
            ?.let { Modifier.align(BiasAlignment(0f, it)) }
            ?: Modifier.align(Alignment.BottomCenter)

        Row(
            modifier = modifierFactory
                .createModifier(
                    modifierPropertiesList = model.progressIndicatorContainer?.ownModifiers,
                    conditionalTransitionModifier = model.progressIndicatorContainer?.conditionalTransitionModifiers,
                    breakpointIndex = breakpointIndex,
                    isPressed = isPressed,
                    isDarkModeEnabled = isDarkModeEnabled,
                    offerState = offerState,
                )
                .then(selfAlignment)
                .semantics(mergeDescendants = true) {}
                .clearAndSetSemantics {
                    invisibleToUser()
                },
            horizontalArrangement = wrapperContainer.horizontalArrangement,
            verticalAlignment = BiasAlignment.Vertical(wrapperContainer.alignmentBias),
        ) {
            repeat(pageCount) { index ->
                val indicator = when {
                    index == selectedPage -> model.activeIndicator ?: model.seenIndicator ?: model.indicatorStyle
                    index < selectedPage -> model.seenIndicator ?: model.indicatorStyle
                    else -> model.indicatorStyle
                }
                IndicatorItem(
                    indicator = indicator,
                    baseIndicator = model.indicatorStyle,
                    isPressed = isPressed,
                    offerState = offerState,
                    isDarkModeEnabled = isDarkModeEnabled,
                    breakpointIndex = breakpointIndex,
                    onSelected = { onIndicatorSelected(index) },
                )
            }
        }
    }

    @Composable
    private fun IndicatorItem(
        indicator: LayoutSchemaUiModel.ProgressIndicatorItemUiModel?,
        baseIndicator: LayoutSchemaUiModel.ProgressIndicatorItemUiModel?,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onSelected: () -> Unit,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            modifier = modifierFactory
                .createModifier(
                    modifierPropertiesList = indicator?.ownModifiers,
                    conditionalTransitionModifier = indicator?.conditionalTransitionModifiers,
                    breakpointIndex = breakpointIndex,
                    isPressed = isPressed,
                    isDarkModeEnabled = isDarkModeEnabled,
                    offerState = offerState,
                    basePropertiesList = baseIndicator?.ownModifiers,
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onSelected,
                )
                .semantics(mergeDescendants = true) {},
        )
    }

    private fun Modifier.catalogGalleryTapNavigation(
        enabled: Boolean,
        onTapBackward: () -> Unit,
        onTapForward: () -> Unit,
    ): Modifier = if (!enabled) {
        this
    } else {
        pointerInput(onTapBackward, onTapForward) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val up = waitForUpOrCancellation()
                if (up != null && !up.isConsumed) {
                    if (down.position.x < size.width / 2f) {
                        onTapBackward()
                    } else {
                        onTapForward()
                    }
                }
            }
        }
    }

    private companion object {
        const val PreviousImageContentDescription = "Previous image"
        const val NextImageContentDescription = "Next image"
    }
}
