package com.rokt.roktux.component.button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.ProgressUiDirection
import com.rokt.roktux.component.ComposableComponent
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.component.ModifierFactory
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlin.math.ceil

internal class ProgressControlComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.ProgressControlUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.ProgressControlUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val currentPage = remember(offerState.currentOfferIndex, offerState.viewableItems) {
            ceil(offerState.currentOfferIndex.toDouble() / offerState.viewableItems).toInt()
        }
        val totalPages = remember(offerState.lastOfferIndex, offerState.viewableItems) {
            ceil(offerState.lastOfferIndex.toDouble() / offerState.viewableItems).toInt()
        }
        ButtonComponent(
            model = model,
            factory = factory,
            modifierFactory = modifierFactory,
            modifier = modifier,
            offerState = offerState,
            isDarkModeEnabled = isDarkModeEnabled,
            breakpointIndex = breakpointIndex,
            ignoreChildrenForAccessibility = true,
            onEventSent = onEventSent,
        ) {
            if (model.progressionDirection == ProgressUiDirection.Forward) {
                if (currentPage < totalPages) {
                    onEventSent(
                        LayoutContract.LayoutEvent.LayoutVariantNavigated(
                            offerState.currentOfferIndex + offerState.viewableItems,
                        ),
                    )
                }
            } else {
                if (currentPage > 0) {
                    onEventSent(
                        LayoutContract.LayoutEvent.LayoutVariantNavigated(
                            offerState.currentOfferIndex - offerState.viewableItems,
                        ),
                    )
                }
            }
        }
    }
}
