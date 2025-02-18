package com.rokt.roktux.component.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import com.rokt.modelmapper.uimodel.ButtonUiModel
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.component.ModifierFactory
import com.rokt.roktux.component.findWrappedChild
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ButtonComponent(
    model: ButtonUiModel,
    factory: LayoutUiModelFactory,
    modifierFactory: ModifierFactory,
    modifier: Modifier,
    offerState: OfferUiState,
    isDarkModeEnabled: Boolean,
    breakpointIndex: Int,
    ignoreChildrenForAccessibility: Boolean = false,
    onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    onClicked: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    interactionSource.collectIsFocusedAsState()
    val container = modifierFactory.createContainerUiProperties(
        containerProperties = model.containerProperties,
        index = breakpointIndex,
        isPressed = isPressed,
    )
    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                enabled = true,
                onClick = {
                    onClicked.invoke()
                },
            )
            .then(
                modifierFactory.createModifier(
                    modifierPropertiesList = model.ownModifiers,
                    conditionalTransitionModifier = model.conditionalTransitionModifiers,
                    breakpointIndex = breakpointIndex,
                    isPressed = isPressed,
                    isDarkModeEnabled = isDarkModeEnabled,
                    offerState = offerState,
                ),
            ),
        verticalAlignment = BiasAlignment.Vertical(
            container.alignmentBias,
        ),
        horizontalArrangement = container.horizontalArrangement,
    ) {
        model.children.forEach { child ->
            child?.let {
                val nonWhenChild = findWrappedChild(child)
                var childModifier: Modifier = Modifier
                modifierFactory.createContainerUiProperties(
                    containerProperties = nonWhenChild.containerProperties,
                    index = breakpointIndex,
                    isPressed = isPressed,
                ).also { container ->
                    container.weight?.let {
                        childModifier = childModifier.then(Modifier.weight(it))
                    }
                    container.selfAlignmentBias?.let {
                        childModifier = childModifier.then(Modifier.align(BiasAlignment.Vertical(it)))
                    }
                    if (ignoreChildrenForAccessibility) {
                        childModifier = childModifier.semantics { invisibleToUser() }
                    }
                }
                factory.CreateComposable(
                    model = child,
                    modifier = childModifier,
                    isPressed = isPressed,
                    offerState = offerState,
                    isDarkModeEnabled = isDarkModeEnabled,
                    breakpointIndex = breakpointIndex,
                    onEventSent = onEventSent,
                )
            }
        }
    }
}
