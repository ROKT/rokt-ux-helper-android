package com.rokt.roktux.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import com.rokt.modelmapper.uimodel.AlignmentUiModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

internal class RowComponent(private val factory: LayoutUiModelFactory, private val modifierFactory: ModifierFactory) :
    ComposableComponent<LayoutSchemaUiModel.RowUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.RowUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val container = modifierFactory.createContainerUiProperties(
            containerProperties = model.containerProperties,
            index = breakpointIndex,
            isPressed = isPressed,
        )
        var anyStretchChild by remember { mutableStateOf(false) }
        Row(
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
                .then(
                    if (container.alignmentBias == AlignmentUiModel.Stretch.bias || anyStretchChild) {
                        Modifier.height(IntrinsicSize.Min)
                    } else {
                        Modifier
                    },
                )
                .then(
                    if (model.isScrollable) {
                        Modifier.horizontalScroll(rememberScrollState())
                    } else {
                        Modifier
                    },
                ),
            verticalAlignment = BiasAlignment.Vertical(
                if (container.alignmentBias ==
                    AlignmentUiModel.Stretch.bias
                ) {
                    AlignmentUiModel.Start.bias
                } else {
                    container.alignmentBias
                },
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
                            if (it == AlignmentUiModel.Stretch.bias) {
                                anyStretchChild = true
                            } else {
                                childModifier = childModifier.then(Modifier.align(BiasAlignment.Vertical(it)))
                            }
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
}
