package com.rokt.roktux.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
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

internal class CatalogStackedCollectionComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.CatalogStackedCollectionUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.CatalogStackedCollectionUiModel,
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

        Column(
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
                        Modifier.width(IntrinsicSize.Min)
                    } else {
                        Modifier
                    },
                ),
            horizontalAlignment = BiasAlignment.Horizontal(
                if (container.alignmentBias ==
                    AlignmentUiModel.Stretch.bias
                ) {
                    AlignmentUiModel.Start.bias
                } else {
                    container.alignmentBias
                },
            ),
            verticalArrangement = container.verticalArrangement,
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
                                childModifier = childModifier.then(Modifier.align(BiasAlignment.Horizontal(it)))
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
