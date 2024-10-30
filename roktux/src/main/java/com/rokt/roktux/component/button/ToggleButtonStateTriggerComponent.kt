package com.rokt.roktux.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.component.ComposableComponent
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.component.ModifierFactory
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

internal class ToggleButtonStateTriggerComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.ToggleButtonStateTriggerUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.ToggleButtonStateTriggerUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
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
            val newState = if (offerState.customState.getOrDefault(model.customStateKey, 0) == 0) {
                1
            } else {
                0
            }
            onEventSent(LayoutContract.LayoutEvent.SetCustomState(model.customStateKey, newState))
        }
    }
}
