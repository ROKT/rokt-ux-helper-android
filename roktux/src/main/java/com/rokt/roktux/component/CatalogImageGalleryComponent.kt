package com.rokt.roktux.component

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
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

        val selectedPage = ((offerState.customState[model.customStateKey] ?: 1) - 1)
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

            LaunchedEffect(selectedPage, galleryImages.size) {
                if (pagerState.currentPage != selectedPage) {
                    pagerState.scrollToPage(selectedPage)
                }
            }

            LaunchedEffect(pagerState.currentPage, galleryImages.size) {
                onEventSent(
                    LayoutContract.LayoutEvent.SetCustomState(
                        model.customStateKey,
                        pagerState.currentPage + 1,
                    ),
                )
            }

            HorizontalPager(
                state = pagerState,
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

            if (model.showIndicators && pagerState.pageCount > 1 && model.indicatorStyle != null) {
                IndicatorRow(
                    model = model,
                    selectedPage = pagerState.currentPage,
                    pageCount = pagerState.pageCount,
                    isPressed = isPressed,
                    offerState = offerState,
                    isDarkModeEnabled = isDarkModeEnabled,
                    breakpointIndex = breakpointIndex,
                )
            }
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
    ) {
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
                .semantics(mergeDescendants = true) {},
        )
    }
}
