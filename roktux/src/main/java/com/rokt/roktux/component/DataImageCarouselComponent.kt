package com.rokt.roktux.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import coil.ImageLoader
import coil.request.ImageRequest
import com.rokt.modelmapper.uimodel.DataImageTransition
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.di.layout.LocalLayoutComponent
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
internal class DataImageCarouselComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.DataImageCarouselUiModel> {
    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.DataImageCarouselUiModel,
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
        // State is stored from 1 to n, but the pager starts from 0
        val carouselPosition = offerState.customState.getOrDefault(model.customStateKey, 1) - 1
        val context = LocalContext.current
        val imageLoader = LocalLayoutComponent.current[ImageLoader::class.java]
        val carouselImages = remember(isDarkModeEnabled) {
            model.images
                .asSequence()
                .mapNotNull { (key, image) ->
                    val url = if (isDarkModeEnabled) image.darkUrl else image.lightUrl
                    if (!url.isNullOrEmpty()) {
                        Triple(key, image, url)
                    } else {
                        null
                    }
                }
                .sortedBy { it.first }
                .map { (_, image, url) -> image to url }
                .toList()
        }

        LaunchedEffect(isDarkModeEnabled) {
            carouselImages.forEach { (_, url) ->
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .build()
                imageLoader.enqueue(request)
            }
        }

        if (carouselImages.isNotEmpty()) {
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
                    .then(modifier),
                contentAlignment = BiasAlignment(container.arrangementBias, container.alignmentBias),
            ) {
                val pagerState = rememberPagerState { carouselImages.size }
                LaunchedEffect(Unit) {
                    onEventSent(
                        LayoutContract.LayoutEvent.SetCustomState(
                            model.customStateKey,
                            carouselPosition + 1,
                        ),
                    )
                    while (true) {
                        delay(model.duration)
                        val pagePosition = if (pagerState.currentPage == pagerState.pageCount - 1) {
                            0
                        } else {
                            pagerState.currentPage + 1
                        }
                        pagerState.scrollToPage(pagePosition)
                        onEventSent(
                            LayoutContract.LayoutEvent.SetCustomState(
                                model.customStateKey,
                                pagePosition + 1,
                            ),
                        )
                    }
                }
                val currentPage = pagerState.currentPage

                HorizontalPager(
                    state = pagerState,
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                        snapAnimationSpec = tween(),
                    ),
                    userScrollEnabled = false,
                ) { page ->
                    val duration = model.transition?.settings?.durationMillis()?.toInt() ?: 300
                    AnimatedContent(
                        targetState = carouselImages[currentPage].first,
                        transitionSpec = {
                            if (model.transition?.type == DataImageTransition.Type.FadeInOut) {
                                fadeIn(animationSpec = tween<Float>(durationMillis = duration)) togetherWith
                                    fadeOut(animationSpec = tween<Float>(durationMillis = duration))
                            } else {
                                slideInHorizontally(
                                    animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing),
                                    initialOffsetX = { fullWidth -> fullWidth },
                                ) togetherWith slideOutHorizontally(
                                    animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing),
                                    targetOffsetX = { fullWidth -> -fullWidth / 2 },
                                )
                            }
                        },
                        label = "Image Slide Animation",
                    ) { image ->
                        image?.let {
                            factory.CreateComposable(
                                model = image,
                                modifier = Modifier,
                                isPressed = isPressed,
                                offerState = offerState,
                                isDarkModeEnabled = isDarkModeEnabled,
                                breakpointIndex = breakpointIndex,
                                onEventSent = onEventSent,
                            )
                        }
                    }
                }
                if (pagerState.pageCount > 1) {
                    val wrapperContainer = modifierFactory.createContainerUiProperties(
                        containerProperties = model.progressIndicatorContainer?.containerProperties,
                        index = breakpointIndex,
                        isPressed = isPressed,
                    )
                    val selfAlignment =
                        wrapperContainer.selfAlignmentBias?.let { Modifier.align(BiasAlignment(0f, it)) }
                            ?: Modifier.align(Alignment.BottomCenter)
                    if (model.indicator?.show ?: false) {
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
                            verticalAlignment = BiasAlignment.Vertical(
                                wrapperContainer.alignmentBias,
                            ),
                        ) {
                            for (index in 0 until pagerState.pageCount) {
                                var needsAnimation = false
                                val defaultIndicator = model.indicatorStyle
                                if (defaultIndicator != null) {
                                    val indicator: LayoutSchemaUiModel.ProgressIndicatorItemUiModel = when {
                                        index < carouselPosition -> {
                                            model.seenIndicator ?: defaultIndicator
                                        }

                                        index == carouselPosition -> {
                                            needsAnimation = true
                                            model.activeIndicator ?: model.seenIndicator ?: defaultIndicator
                                        }

                                        else -> {
                                            defaultIndicator
                                        }
                                    }
                                    val indicatorContainerProperty = modifierFactory.createContainerUiProperties(
                                        containerProperties = indicator.containerProperties,
                                        index = breakpointIndex,
                                        isPressed = isPressed,
                                        baseProperties = defaultIndicator.containerProperties,
                                    )
                                    var childModifier: Modifier = Modifier
                                    indicatorContainerProperty.weight?.let {
                                        childModifier = childModifier.then(Modifier.weight(it))
                                    }
                                    CarouselIndicatorItemComponent(
                                        modifier = childModifier,
                                        model = indicator,
                                        baseModel = model.indicatorStyle!!, // Safe to unwrap as we have already checked for null
                                        isPressed = isPressed,
                                        isDarkModeEnabled = isDarkModeEnabled,
                                        breakpointIndex = breakpointIndex,
                                        needsAnimation = needsAnimation,
                                        offerState = offerState,
                                        duration = model.duration.toInt(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            LaunchedEffect(Unit) {
                onEventSent(
                    LayoutContract.LayoutEvent.SetCustomState(
                        model.customStateKey,
                        -1,
                    ),
                )
            }
        }
    }

    @Composable
    fun CarouselIndicatorItemComponent(
        model: LayoutSchemaUiModel.ProgressIndicatorItemUiModel?,
        baseModel: LayoutSchemaUiModel.ProgressIndicatorItemUiModel,
        offerState: OfferUiState,
        isPressed: Boolean,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        needsAnimation: Boolean,
        duration: Int,
        modifier: Modifier = Modifier,
    ) {
        Box(
            modifier = modifierFactory
                .createModifier(
                    modifierPropertiesList = model?.ownModifiers,
                    conditionalTransitionModifier = model?.conditionalTransitionModifiers,
                    breakpointIndex = breakpointIndex,
                    isPressed = isPressed,
                    isDarkModeEnabled = isDarkModeEnabled,
                    offerState = offerState,
                    basePropertiesList = baseModel.ownModifiers,
                )
                .then(modifier).then(
                    if (needsAnimation) {
                        Modifier.background(Color.LightGray)
                    } else {
                        Modifier
                    },
                )
                .semantics(mergeDescendants = true) { },
        ) {
            if (needsAnimation) {
                var progress by remember { mutableFloatStateOf(0f) }
                val animatedWidth by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(duration, easing = LinearEasing),
                    label = "storyProgress",
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedWidth)
                        .then(
                            modifierFactory
                                .createModifier(
                                    modifierPropertiesList = model?.ownModifiers,
                                    conditionalTransitionModifier = model?.conditionalTransitionModifiers,
                                    breakpointIndex = breakpointIndex,
                                    isPressed = isPressed,
                                    isDarkModeEnabled = isDarkModeEnabled,
                                    offerState = offerState,
                                    basePropertiesList = baseModel.ownModifiers,
                                )
                                .animateContentSize()
                                .semantics(mergeDescendants = true) { },
                        ),
                ) {
                    LaunchedEffect(Unit) {
                        progress = 1f
                    }
                }
            }
        }
    }
}
