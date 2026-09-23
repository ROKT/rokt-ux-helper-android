package com.rokt.roktux.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.TransitionUiModel
import com.rokt.roktux.utils.AnimationState
import com.rokt.roktux.utils.OfferScopedViewModelStoreOwner
import com.rokt.roktux.utils.fadeInOutAnimationModifier
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        var animationState by remember { mutableStateOf(AnimationState.Show) }
        var firstRender by rememberSaveable {
            mutableStateOf(true)
        }
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(key1 = offerState.targetOfferIndex) {
            if (!firstRender) {
                animationState = AnimationState.Hide
            } else {
                firstRender = false
            }
        }

        key(offerState.currentOfferIndex) {
            OfferScopedViewModelStoreOwner {
                factory.CreateComposable(
                    model = LayoutSchemaUiModel.MarketingUiModel(),
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
                        // For now fadeInOut is the only possible transition animation
                        .fadeInOutAnimationModifier(
                            animationState = animationState,
                            duration = (
                                (model.transition as? TransitionUiModel.FadeInOutTransition)?.duration?.div(
                                    2,
                                )
                                ) ?: 0,
                        ) {
                            onEventSent(LayoutContract.LayoutEvent.SetCurrentOffer(offerState.targetOfferIndex))
                            animationState = AnimationState.Show
                            coroutineScope.launch {
                                // requestFocus only works a single time so we need to clear focus and
                                // request it again after a delay: the focus Active state is maintained
                                // and not automatically set to Inactive.
                                // See: androidx.compose.ui.focus.FocusTransactions.kt#64
                                focusManager.clearFocus(true)
                                delay(10)
                                focusRequester.requestFocus()
                            }
                        }
                        .animateContentSize()
                        .semantics {
                            contentDescription =
                                ACCESSIBILITY_READOUT_TEXT.format(
                                    offerState.currentOfferIndex + 1,
                                    offerState.lastOfferIndex + 1,
                                )
                        }
                        .focusRequester(focusRequester)
                        .focusable(),
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
