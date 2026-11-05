package com.rokt.roktux.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.TransitionUiModel
import com.rokt.roktux.utils.AnimationState
import com.rokt.roktux.utils.OfferScopedViewModelStoreOwner
import com.rokt.roktux.utils.fadeInOutAnimationModifier
import com.rokt.roktux.utils.rememberOfferViewModelStoreCache
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
        val onePixel = with(LocalDensity.current) { 1.toDp() }
        val focusManager = LocalFocusManager.current
        val coroutineScope = rememberCoroutineScope()
        val storeCache = rememberOfferViewModelStoreCache()
        LaunchedEffect(key1 = offerState.targetOfferIndex) {
            if (!firstRender) {
                animationState = AnimationState.Hide
            } else {
                firstRender = false
            }
        }
        LaunchedEffect(key1 = offerState.currentOfferIndex) {
            storeCache.retainOnly(setOf(offerState.currentOfferIndex))
        }

        // fadeInOutAnimationModifier/animateContentSize live on this stable, unkeyed Box so their
        // own remembered animation state survives an offer change — keying them (like the content
        // below) would discard that state on every swap, and the fade/resize would never animate.
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
                // A one-pixel row above the offer for the position announcement: composed first so screen readers read
                // it first, and outside the offer, which would otherwise cover it and drop it from the accessibility tree.
                .padding(top = onePixel),
        ) {
            OfferPositionAnnouncement(
                text = ACCESSIBILITY_READOUT_TEXT.format(
                    offerState.currentOfferIndex + 1,
                    offerState.lastOfferIndex + 1,
                ),
                focusRequester = focusRequester,
                modifier = Modifier.offset { IntOffset(0, -1) },
            )
            key(offerState.currentOfferIndex) {
                OfferScopedViewModelStoreOwner(offerIndex = offerState.currentOfferIndex, cache = storeCache) {
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
        }
        LaunchedEffect(key1 = Unit) {
            onEventSent(
                LayoutContract.LayoutEvent.FirstOfferLoaded,
            )
        }
    }
}
