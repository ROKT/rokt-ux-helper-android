package com.rokt.roktux.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
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
import com.rokt.modelmapper.utils.DEFAULT_VIEWABLE_ITEMS
import com.rokt.roktux.utils.AnimationState
import com.rokt.roktux.utils.OfferScopedViewModelStoreOwner
import com.rokt.roktux.utils.fadeInOutAnimationModifier
import com.rokt.roktux.utils.rememberOfferViewModelStoreCache
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

private const val ACCESSIBILITY_READOUT_TEXT = "Page %d of %d"

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
        var animationState by remember { mutableStateOf(AnimationState.Show) }
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current
        val coroutineScope = rememberCoroutineScope()
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
        val storeCache = rememberOfferViewModelStoreCache()
        // Keyed on currentOfferIndex only, not viewableItems: a breakpoint-driven change in how many
        // offers are visible at once must never evict an offer's ViewModelStore on its own, or a
        // shrink-then-regrow (e.g. rotation) would silently duplicate signals like SignalViewed.
        LaunchedEffect(key1 = offerState.currentOfferIndex) {
            storeCache.retainOnly(
                (offerState.currentOfferIndex until offerState.currentOfferIndex + viewableItems).toSet(),
            )
        }

        Column(
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
                        getAccessibilityDescription(offerState)
                }
                .focusRequester(focusRequester)
                .focusable(),
        ) {
            for (offerIndexOffset in 0 until viewableItems) {
                val offerIndex = offerState.currentOfferIndex + offerIndexOffset
                key(offerIndex) {
                    OfferScopedViewModelStoreOwner(offerIndex = offerIndex, cache = storeCache) {
                        factory.CreateComposable(
                            model = LayoutSchemaUiModel.MarketingUiModel(),
                            modifier = modifier,
                            isPressed = isPressed,
                            offerState = offerState.copy(
                                currentOfferIndex = offerIndex,
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
            // Coerce into range rather than only clamping the upper bound, so an unexpected
            // negative breakpoint index (e.g. from a malformed breakpoints payload) can never
            // index out of bounds.
            val viewableItemsBreakpointIndex = breakpointIndex.coerceIn(0, viewableItemsList.size - 1)
            viewableItemsList[viewableItemsBreakpointIndex].coerceIn(DEFAULT_VIEWABLE_ITEMS, lastOfferIndex + 1)
        }
    }

private fun getAccessibilityDescription(offerState: OfferUiState): String = ACCESSIBILITY_READOUT_TEXT.format(
    ceil((offerState.currentOfferIndex + 1).toDouble() / offerState.viewableItems).toInt(),
    ceil((offerState.lastOfferIndex + 1).toDouble() / offerState.viewableItems).toInt(),
)
