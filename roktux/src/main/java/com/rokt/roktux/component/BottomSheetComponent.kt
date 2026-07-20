package com.rokt.roktux.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.waterfall
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
import com.rokt.modelmapper.uimodel.HeightUiModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.ModifierProperties
import com.rokt.modelmapper.uimodel.StateBlock
import com.rokt.roktux.utils.interceptTap
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

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
        val bottomSheetChild = remember(model.child, offerState, breakpointIndex) {
            model.child.expandPercentageHeightIfNeeded(offerState, breakpointIndex)
        }
        var hasUserInteracted by remember { mutableStateOf(false) }
        val sheetProperties = remember { BottomSheetPropertiesCompat.create() }
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
            properties = sheetProperties,
            modifier = modifier,
            contentWindowInsets = {
                if (model.edgeToEdgeDisplay) {
                    WindowInsets.waterfall
                } else {
                    BottomSheetDefaults.windowInsets
                }
            },
        ) {
            BackHandler {
                onEventSent(LayoutContract.LayoutEvent.CloseSelected(isDismissed = true))
            }
            val insetsPadding = remember {
                if (model.edgeToEdgeDisplay) {
                    Modifier.navigationBarsPadding()
                } else {
                    Modifier
                }
            }
            factory.CreateComposable(
                model = bottomSheetChild,
                modifier = Modifier
                    .animateContentSize()
                    .pointerInput(Unit) {
                        interceptTap { hasUserInteracted = true }
                    }
                    .then(insetsPadding),
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

    private fun LayoutSchemaUiModel.ColumnUiModel.expandPercentageHeightIfNeeded(
        offerState: OfferUiState,
        breakpointIndex: Int,
    ): LayoutSchemaUiModel.ColumnUiModel {
        if (!offerState.isBottomSheetExpanded()) {
            return this
        }
        val breakpointModifier = ownModifiers?.getOrNull(breakpointIndex) ?: return this
        if (breakpointModifier.default.height !is HeightUiModel.Percentage) {
            return this
        }
        return copy(
            ownModifiers = ownModifiers.replaceAt(
                index = breakpointIndex,
                value = breakpointModifier.copy(
                    default = breakpointModifier.default.copy(height = HeightUiModel.MatchParent),
                ),
            ),
        )
    }

    private fun OfferUiState.isBottomSheetExpanded(): Boolean = (
        offerCustomStates[currentOfferIndex.toString()]?.get(BottomSheetExpandedStateKey)
            ?: customState[BottomSheetExpandedStateKey]
            ?: DefaultCustomStateValue
        ) == ExpandedCustomStateValue

    private fun ImmutableList<StateBlock<ModifierProperties>>.replaceAt(
        index: Int,
        value: StateBlock<ModifierProperties>,
    ): ImmutableList<StateBlock<ModifierProperties>> = mapIndexed { currentIndex, currentValue ->
        if (currentIndex == index) value else currentValue
    }.toImmutableList()

    private companion object {
        const val BottomSheetExpandedStateKey = "BottomSheetExpandedState"
        const val DefaultCustomStateValue = 0
        const val ExpandedCustomStateValue = 1
    }
}
