package com.rokt.roktux.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.coroutines.delay

internal class TimerStateTriggerComponent : ComposableComponent<LayoutSchemaUiModel.TimerStateTriggerUiModel> {
    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.TimerStateTriggerUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        LaunchedEffect(key1 = model.customStateKey) {
            delay(timeMillis = model.delay)
            onEventSent(LayoutContract.LayoutEvent.SetCustomState(model.customStateKey, model.value))
        }
    }
}
