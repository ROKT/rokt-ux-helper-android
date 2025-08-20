package com.rokt.roktux.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rokt.modelmapper.uimodel.HeightUiModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.StateBlock
import com.rokt.roktux.di.layout.LocalLayoutComponent
import com.rokt.roktux.di.variants.marketing.MarketingComponent
import com.rokt.roktux.utils.componentVisibilityChange
import com.rokt.roktux.utils.onUserInteractionDetected
import com.rokt.roktux.viewmodel.base.BaseContract
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import com.rokt.roktux.viewmodel.variants.MarketingVariantContract
import com.rokt.roktux.viewmodel.variants.MarketingViewModel
import kotlinx.collections.immutable.toImmutableList
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
                    // If so remove fit height from first child
                    val updatedState = if (model.preRenderMeasure && state.value.uiModel.ownModifiers?.get(0)?.default?.height == HeightUiModel.MatchParent) {
                        state.value.copy(uiModel = createDeepCopy(state.value.uiModel))
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

    /**
     * Creates a deep copy of the UI model with modified height for the first modifier
     * This ensures we don't modify the original model and affect global state
     * Now handles all LayoutSchemaUiModel types with proper type checking
     */
    private fun createDeepCopy(originalModel: LayoutSchemaUiModel): LayoutSchemaUiModel {
        // Create modified modifiers with WrapContent height for the first modifier
        val modifiedModifiers = originalModel.ownModifiers?.mapIndexed { index, modifier ->
            if (index == 0) {
                StateBlock(
                    default = modifier.default.copy(
                        height = HeightUiModel.WrapContent
                    ),
                    pressed = modifier.pressed
                )
            } else {
                modifier
            }
        }?.toImmutableList()

        return when (originalModel) {
            is LayoutSchemaUiModel.MarketingUiModel -> originalModel.copy(ownModifiers = modifiedModifiers)
            is LayoutSchemaUiModel.BasicTextUiModel -> originalModel.copy(ownModifiers = modifiedModifiers)
            is LayoutSchemaUiModel.RichTextUiModel -> originalModel.copy(ownModifiers = modifiedModifiers)
            is LayoutSchemaUiModel.ColumnUiModel -> originalModel.copy(ownModifiers = modifiedModifiers)
            is LayoutSchemaUiModel.RowUiModel -> originalModel.copy(ownModifiers = modifiedModifiers)
            is LayoutSchemaUiModel.BoxUiModel -> originalModel.copy(ownModifiers = modifiedModifiers)
            is LayoutSchemaUiModel.CatalogStackedCollectionUiModel -> originalModel.copy(ownModifiers = modifiedModifiers)
            is LayoutSchemaUiModel.ProgressIndicatorUiModel -> originalModel.copy(ownModifiers = modifiedModifiers)
            is LayoutSchemaUiModel.ProgressIndicatorItemUiModel -> originalModel.copy(ownModifiers = modifiedModifiers)
            else -> originalModel // For any types that aren't data classes yet
        }
    }
}
