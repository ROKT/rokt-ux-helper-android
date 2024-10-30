package com.rokt.roktux.component

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.TransitionUiModel
import com.rokt.modelmapper.utils.DEFAULT_VIEWABLE_ITEMS
import com.rokt.roktux.utils.AnimationState
import com.rokt.roktux.utils.fadeInOutAnimationModifier
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.ImmutableList

internal class GroupedDistributionComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.GroupedDistributionUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.GroupedDistributionUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val navController: NavHostController = rememberNavController()
        val startDestination = remember(offerState.currentOfferIndex) {
            LayoutVariants.MarketingScreen(offerState.currentOfferIndex)
        }
        var animationState by remember { mutableStateOf(AnimationState.Show) }
        var firstRender by rememberSaveable {
            mutableStateOf(true)
        }
        LaunchedEffect(key1 = offerState.targetOfferIndex) {
            if (!firstRender) {
                animationState = AnimationState.Hide
            } else {
                firstRender = false
            }
        }
        val viewableItems = getViewableItems(
            breakpointIndex,
            model.viewableItems,
            offerState.lastOfferIndex,
        )
        LaunchedEffect(key1 = viewableItems) {
            onEventSent(LayoutContract.LayoutEvent.ViewableItemsChanged(viewableItems))
        }

        NavHost(
            modifier = modifierFactory
                .createModifier(
                    modifierPropertiesList = model.ownModifiers,
                    conditionalTransitionModifier = model.conditionalTransitionModifiers,
                    breakpointIndex = breakpointIndex,
                    isPressed = isPressed,
                    isDarkModeEnabled = isDarkModeEnabled,
                    offerState = offerState,
                )
                // For now fadeInOut is the only possible transition animation
                .fadeInOutAnimationModifier(
                    animationState = animationState,
                    duration = ((model.transition as? TransitionUiModel.FadeInOutTransition)?.duration?.div(2)) ?: 0,
                ) {
                    navController.popBackStack()
                    navController.navigate(
                        LayoutVariants.MarketingScreen(offerIndex = offerState.targetOfferIndex),
                    )
                    onEventSent(LayoutContract.LayoutEvent.SetCurrentOffer(offerState.targetOfferIndex))
                    animationState = AnimationState.Show
                }
                .animateContentSize()
                .then(modifier),
            navController = navController,
            startDestination = startDestination,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            composable<LayoutVariants.MarketingScreen> { backStackEntry ->
                val root: LayoutVariants.MarketingScreen = backStackEntry.toRoute()
                Column {
                    for (offerIndexOffset in 0 until viewableItems) {
                        factory.CreateComposable(
                            model = LayoutSchemaUiModel.MarketingUiModel(),
                            modifier = modifier,
                            isPressed = isPressed,
                            offerState = offerState.copy(
                                currentOfferIndex = root.offerIndex + offerIndexOffset,
                                viewableItems = viewableItems,
                            ),
                            isDarkModeEnabled = isDarkModeEnabled,
                            breakpointIndex = breakpointIndex,
                        ) { event ->
                            if (event is LayoutContract.LayoutEvent.ResponseOptionSelected) {
                                // Only progress to next offer if viewableItems is 1
                                if (viewableItems == DEFAULT_VIEWABLE_ITEMS) {
                                    onEventSent(event.copy(shouldProgress = true))
                                } else {
                                    onEventSent(event)
                                }
                            } else {
                                onEventSent.invoke(event)
                            }
                        }
                    }
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
internal fun getViewableItems(breakpointIndex: Int, viewableItemsList: ImmutableList<Int>, lastOfferIndex: Int): Int =
    remember(breakpointIndex, viewableItemsList, lastOfferIndex) {
        if (viewableItemsList.isEmpty()) {
            DEFAULT_VIEWABLE_ITEMS
        } else {
            val viewableItemsBreakpointIndex = if (breakpointIndex <= viewableItemsList.size - 1) {
                breakpointIndex
            } else {
                viewableItemsList.size - 1
            }
            viewableItemsList[viewableItemsBreakpointIndex].coerceIn(DEFAULT_VIEWABLE_ITEMS, lastOfferIndex + 1)
        }
    }
