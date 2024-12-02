package com.rokt.roktux.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.utils.getValue
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

internal class BasicTextComponent(private val modifierFactory: ModifierFactory) :
    ComposableComponent<LayoutSchemaUiModel.BasicTextUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.BasicTextUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val value = model.value.getValue(offerState, offerState.viewableItems) ?: return
        val textStyleUiState = modifierFactory.createTextStyle(
            text = value,
            textStyles = model.textStyles,
            breakpointIndex = breakpointIndex,
            isPressed = isPressed,
            isDarkModeEnabled = false,
            conditionalTransitionTextStyling = model.conditionalTransitionTextStyling,
            offerState = offerState,
        )
        if (value.isNotEmpty()) {
            Text(
                text = textStyleUiState.value,
                style = textStyleUiState.textStyle,
                modifier = modifierFactory
                    .createModifier(
                        modifierPropertiesList = model.ownModifiers,
                        conditionalTransitionModifier = model.conditionalTransitionModifiers,
                        breakpointIndex = breakpointIndex,
                        isPressed = isPressed,
                        isDarkModeEnabled = isDarkModeEnabled,
                        offerState = offerState,
                    )
                    .then(modifier),
                maxLines = textStyleUiState.lineLimit,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
