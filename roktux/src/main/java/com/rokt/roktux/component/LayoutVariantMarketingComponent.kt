package com.rokt.roktux.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.di.layout.LocalLayoutComponent
import com.rokt.roktux.di.variants.marketing.MarketingComponent
import com.rokt.roktux.utils.componentVisibilityChange
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
            MarketingComponent(layoutComponent, offerState.currentOfferIndex)
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
                Column {
                    factory.CreateComposable(
                        model = state.value.uiModel,
                        modifier = Modifier.componentVisibilityChange(
                            { viewId, visible ->
                                viewModel.setEvent(
                                    MarketingVariantContract.LayoutVariantEvent.OfferVisibilityChanged(
                                        viewId,
                                        visible,
                                    ),
                                )
                            },
                            viewModel.currentOffer,
                        ),
                        isPressed = isPressed,
                        offerState = offerState.copy(creativeCopy = state.value.creativeCopy),
                        isDarkModeEnabled = isDarkModeEnabled,
                        breakpointIndex = breakpointIndex,
                    ) { event ->
                        onEventSent.invoke(event)
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
                }
            }.collect()
        }
    }
}
