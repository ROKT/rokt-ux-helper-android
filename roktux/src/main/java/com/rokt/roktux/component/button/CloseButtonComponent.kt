package com.rokt.roktux.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.component.ComposableComponent
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.component.ModifierFactory
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

private const val CLOSE_BUTTON_DESCRIPTION = "Close"

internal class CloseButtonComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.CloseButtonUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.CloseButtonUiModel,
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
            modifier = modifier.semantics { contentDescription = CLOSE_BUTTON_DESCRIPTION },
            offerState = offerState,
            isDarkModeEnabled = isDarkModeEnabled,
            breakpointIndex = breakpointIndex,
            ignoreChildrenForAccessibility = true,
            onEventSent = onEventSent,
        ) {
            onEventSent.invoke(
                LayoutContract.LayoutEvent.CloseSelected(isDismissed = false),
            )
        }
    }
}
