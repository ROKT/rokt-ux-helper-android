package com.rokt.roktux.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.component.ComposableComponent
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.component.ModifierFactory
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

internal class CreativeResponseComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.CreativeResponseUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.CreativeResponseUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        model.responseOption?.let { responseOptionModel ->
            ButtonComponent(
                model = model,
                factory = factory,
                modifierFactory = modifierFactory,
                modifier = modifier,
                offerState = offerState,
                isDarkModeEnabled = isDarkModeEnabled,
                breakpointIndex = breakpointIndex,
                onEventSent = onEventSent,
            ) {
                onEventSent.invoke(
                    LayoutContract.LayoutEvent.ResponseOptionSelected(
                        offerState.currentOfferIndex,
                        model.openLinks,
                        responseOptionModel.properties,
                    ),
                )
            }
        }
    }
}
