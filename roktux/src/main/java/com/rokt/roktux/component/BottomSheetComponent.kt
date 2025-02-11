package com.rokt.roktux.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.utils.interceptTap
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

@OptIn(ExperimentalMaterial3Api::class)
internal class BottomSheetComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.BottomSheetUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.BottomSheetUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val scrimColor = modifierFactory.createBackground(
            modifierProperties = model.ownModifiers,
            index = breakpointIndex,
            isPressed = isPressed,
            isDarkModeEnabled = false,
        )
        val modalBottomSheetState =
            rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = {
                    if (!model.allowBackdropToClose) {
                        return@rememberModalBottomSheetState it != SheetValue.Hidden
                    }
                    true
                },
            )

        val bottomSheetShape =
            model.child.ownModifiers?.getOrNull(breakpointIndex)?.default?.let {
                modifierFactory.createBackgroundShape(it)
            } ?: RectangleShape
        var hasUserInteracted by remember { mutableStateOf(false) }
        ModalBottomSheet(
            onDismissRequest = {
                if (!hasUserInteracted) {
                    onEventSent(LayoutContract.LayoutEvent.UserInteracted)
                }
                onEventSent(LayoutContract.LayoutEvent.CloseSelected(isDismissed = true))
            },
            shape = bottomSheetShape,
            sheetState = modalBottomSheetState,
            scrimColor = scrimColor ?: BottomSheetDefaults.ScrimColor,
            dragHandle = {},
            containerColor = Color.Transparent,
            properties = ModalBottomSheetProperties(
                shouldDismissOnBackPress = false,
            ),
            modifier = modifier,
        ) {
            BackHandler {
                onEventSent(LayoutContract.LayoutEvent.CloseSelected(isDismissed = true))
            }
            factory.CreateComposable(
                model = model.child,
                modifier = Modifier
                    .animateContentSize()
                    .pointerInput(Unit) {
                        interceptTap { hasUserInteracted = true }
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
