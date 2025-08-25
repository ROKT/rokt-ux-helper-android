package com.rokt.roktux.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rokt.modelmapper.uimodel.HeightUiModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.di.layout.LocalLayoutComponent
import com.rokt.roktux.di.variants.marketing.MarketingComponent
import com.rokt.roktux.extensions.transformHeightForAllMatching
import com.rokt.roktux.utils.componentVisibilityChange
import com.rokt.roktux.utils.onUserInteractionDetected
import com.rokt.roktux.viewmodel.base.BaseContract
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import com.rokt.roktux.viewmodel.variants.MarketingVariantContract
import com.rokt.roktux.viewmodel.variants.MarketingViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

internal class LayoutVariantMarketingComponent(private val factory: LayoutUiModelFactory) :
    ComposableComponent<LayoutSchemaUiModel.MarketingUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.MarketingUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val layoutComponent = LocalLayoutComponent.current
        val component: MarketingComponent = remember {
            MarketingComponent(
                component = layoutComponent,
                currentOffer = offerState.currentOfferIndex,
                customStates = offerState.offerCustomStates[offerState.currentOfferIndex.toString()] ?: emptyMap(),
            )
        }
        val viewModel = viewModel<MarketingViewModel>(
            factory = component[MarketingViewModel.MarketingViewModelFactory::class.java],
            key = offerState.currentOfferIndex.toString(),
        )
        val viewState by viewModel.viewState.collectAsStateWithLifecycle()
        when (val state = viewState) {
            BaseContract.BaseViewState.Empty -> {
            }

            is BaseContract.BaseViewState.Error -> {
            }

            is BaseContract.BaseViewState.Success -> {
                Column(
                    modifier = modifier.semantics {
                        isTraversalGroup = true
                    },
                ) {
                    // At this point we will know the distribution type and if it is a pre-render measure
                    // If so remove fit height from all children that have MatchParent height
                    val updatedState = if (model.preRenderMeasure &&
                        state.value.uiModel.ownModifiers?.any {
                            it.default.height == HeightUiModel.MatchParent
                        } == true
                    ) {
                        state.value.copy(
                            uiModel = state.value.uiModel.transformHeightForAllMatching(
                                condition = { stateBlock -> stateBlock.default.height == HeightUiModel.MatchParent },
                                newHeight = HeightUiModel.WrapContent,
                            ),
                        )
                    } else {
                        state.value
                    }

                    factory.CreateComposable(
                        model = updatedState.uiModel,
                        modifier = Modifier.componentVisibilityChange(
                            { viewId, visibilityInfo ->
                                viewModel.setEvent(
                                    LayoutContract.LayoutEvent.OfferVisibilityChanged(
                                        viewId,
                                        visibilityInfo.visible &&
                                            !visibilityInfo.obscured &&
                                            !visibilityInfo.incorrectlySized,
                                    ),
                                )
                            },
                            viewModel.currentOffer,
                        ).onUserInteractionDetected {
                            viewModel.setEvent(LayoutContract.LayoutEvent.UserInteracted)
                        },
                        isPressed = isPressed,
                        offerState = offerState.copy(
                            creativeCopy = state.value.creativeCopy,
                            customState = state.value.customState,
                        ),
                        isDarkModeEnabled = isDarkModeEnabled,
                        breakpointIndex = breakpointIndex,
                    ) { event ->
                        viewModel.setEvent(event)
                    }
                }
            }
        }

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.onEach { effect ->
                when (effect) {
                    is MarketingVariantContract.LayoutVariantEffect.SetSignalViewed -> {
                        onEventSent.invoke(
                            LayoutContract.LayoutEvent.SignalViewed(
                                effect.offerId,
                            ),
                        )
                    }

                    is MarketingVariantContract.LayoutVariantEffect.PropagateEvent -> {
                        onEventSent.invoke(effect.event)
                    }
                }
            }.collect()
        }
    }
}
