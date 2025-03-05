package com.rokt.roktux.component

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.TransitionUiModel
import com.rokt.roktux.utils.AnimationState
import com.rokt.roktux.utils.fadeInOutAnimationModifier
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.serialization.Serializable

private const val ACCESSIBILITY_READOUT_TEXT = "Offer %d of %d"

internal class OneByOneDistributionComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.OneByOneDistributionUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.OneByOneDistributionUiModel,
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
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(key1 = offerState.targetOfferIndex) {
            if (!firstRender) {
                animationState = AnimationState.Hide
                // the NavHost maintains focus so the same workaround as Carousel is not needed
                focusRequester.requestFocus()
            } else {
                firstRender = false
            }
        }

        val viewModelStoreOwner = LocalViewModelStoreOwner.current
        try {
            checkNotNull(viewModelStoreOwner) {
                "NavHost requires a ViewModelStoreOwner to be provided via LocalViewModelStoreOwner"
            }
            navController.setViewModelStore(viewModelStoreOwner.viewModelStore)
        } catch (e: Exception) {
            onEventSent(LayoutContract.LayoutEvent.UiException(e, true))
            return
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
                .then(modifier)
                .semantics {
                    contentDescription =
                        ACCESSIBILITY_READOUT_TEXT.format(
                            offerState.currentOfferIndex + 1,
                            offerState.lastOfferIndex + 1,
                        )
                }
                .focusRequester(focusRequester)
                .focusable(),
            navController = navController,
            startDestination = startDestination,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            composable<LayoutVariants.MarketingScreen> { _ ->
                factory.CreateComposable(
                    model = LayoutSchemaUiModel.MarketingUiModel(),
                    modifier = modifier,
                    isPressed = isPressed,
                    offerState = offerState,
                    isDarkModeEnabled = isDarkModeEnabled,
                    breakpointIndex = breakpointIndex,
                ) { event ->
                    if (event is LayoutContract.LayoutEvent.ResponseOptionSelected) {
                        onEventSent(event.copy(shouldProgress = true))
                    } else {
                        onEventSent.invoke(event)
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

internal sealed interface LayoutVariants {
    @Serializable
    data class MarketingScreen(val offerIndex: Int) : LayoutVariants
}
