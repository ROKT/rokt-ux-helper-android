package com.rokt.roktux.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.utils.ROKT_ICONS_FONT_FAMILY
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

internal class IconComponent(private val modifierFactory: ModifierFactory) :
    ComposableComponent<LayoutSchemaUiModel.IconUiModel> {

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
                isDarkModeEnabled = isDarkModeEnabled,
                defaultFontFamily = ROKT_ICONS_FONT_FAMILY,
                offerState = offerState,
                onEventSent = onEventSent,
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
                    .then(modifier)
                    .semantics {
                        // The rendered text is an icon-font glyph name, never a label a user should hear.
                        val label = model.alt?.takeUnless { model.accessibilityHidden || it.isBlank() }
                        if (label == null) hideFromAccessibility() else contentDescription = label
                    },
            )
        }
    }
}
