package com.rokt.roktux.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.utils.interceptTap
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

internal class OverlayComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.OverlayUiModel> {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.OverlayUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        var isClosedByBackdrop by remember { mutableStateOf(false) }
        val container = modifierFactory.createContainerUiProperties(
            containerProperties = model.containerProperties,
            index = breakpointIndex,
            isPressed = isPressed,
        )

        Popup(
            alignment = Alignment.TopStart,
            properties = PopupProperties(
                dismissOnBackPress = true,
                // This is handled by the clickable modifier on the Box
                dismissOnClickOutside = false,
                focusable = true,
            ),
            onDismissRequest = {
                // Avoiding double sending event when using close gestures
                if (!isClosedByBackdrop) {
                    isClosedByBackdrop = true
                    onEventSent(LayoutContract.LayoutEvent.CloseSelected(isDismissed = true))
                }
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        modifierFactory
                            .createModifier(
                                modifierPropertiesList = model.ownModifiers,
                                conditionalTransitionModifier = model.conditionalTransitionModifiers,
                                breakpointIndex = breakpointIndex,
                                isPressed = isPressed,
                                isDarkModeEnabled = isDarkModeEnabled,
                                offerState = offerState,
                            ),
                    )
                    .pointerInput(Unit) {
                        detectTapGestures {
                            if (model.allowBackdropToClose) {
                                isClosedByBackdrop = true
                                onEventSent(LayoutContract.LayoutEvent.CloseSelected(isDismissed = true))
                            }
                        }
                    }
                    .safeDrawingPadding()
                    .animateContentSize()
                    .then(modifier),
                contentAlignment = BiasAlignment(container.arrangementBias, container.alignmentBias),
            ) {
                factory.CreateComposable(
                    model = model.child,
                    modifier = Modifier.pointerInput(Unit) {
                        // This is to avoid sending the click events to parent which may close the overlay
                        interceptTap(
                            pass = PointerEventPass.Main,
                            shouldConsume = true,
                        ) {}
                    },
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
