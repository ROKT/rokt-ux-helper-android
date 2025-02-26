package com.rokt.roktux.component

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

        val insetsPadding = remember {
            if (!model.edgeToEdgeDisplay) {
                Modifier.statusBarsPadding()
            } else {
                Modifier
            }
        }
        var hasUserInteracted by remember { mutableStateOf(false) }

        // This box is draw edge to edge on the screen as Popup does not draw edge to edge
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(insetsPadding)
                .then(
                    Modifier.background(
                        modifierFactory
                            .createBackground(
                                modifierProperties = model.ownModifiers,
                                index = breakpointIndex,
                                isPressed = isPressed,
                                isDarkModeEnabled = isDarkModeEnabled,
                            ) ?: Color.Transparent,
                    ),
                )
                .then(modifier),
        ) {
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
                    Log.d("Sahil", "onDismissRequest $isClosedByBackdrop")
                    if (!isClosedByBackdrop) {
                        isClosedByBackdrop = true
                        onEventSent(LayoutContract.LayoutEvent.CloseSelected(isDismissed = true))
                    }
                },
            ) {
                // This box is to intercept the click events outside the popup as popup handles the click events outside the popup
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures {
                                if (model.allowBackdropToClose) {
                                    isClosedByBackdrop = true
                                    onEventSent(LayoutContract.LayoutEvent.CloseSelected(isDismissed = true))
                                }
                            }
                        }
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
                            ) {
                                hasUserInteracted = true
                            }
                        },
                        isPressed = isPressed,
                        offerState = offerState,
                        isDarkModeEnabled = isDarkModeEnabled,
                        breakpointIndex = breakpointIndex,
                        onEventSent = onEventSent,
                    )

                    if (hasUserInteracted) {
                        LaunchedEffect(Unit) {
                            onEventSent(LayoutContract.LayoutEvent.UserInteracted)
                        }
                    }
                }
            }
        }
    }
}
