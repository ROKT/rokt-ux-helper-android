package com.rokt.roktux.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.utils.ROKT_ICONS_FONT_FAMILY
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

internal class IconComponent(
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.IconUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.IconUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        if (model.value.isNotEmpty()) {
            val textStyleUiState = modifierFactory.createTextStyle(
                text = model.value,
                textStyles = model.textStyles,
                breakpointIndex = breakpointIndex,
                isPressed = isPressed,
                isDarkModeEnabled = false,
                defaultFontFamily = ROKT_ICONS_FONT_FAMILY,
                offerState = offerState,
            )
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
                // Apply textStyles and other properties as needed
            )
        }
    }
}
