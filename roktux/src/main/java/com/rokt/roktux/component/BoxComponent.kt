package com.rokt.roktux.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

internal class BoxComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.BoxUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.BoxUiModel,
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
        Box(
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
            contentAlignment = BiasAlignment(container.arrangementBias, container.alignmentBias),
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
                        container.selfAlignmentBias?.let {
                            childModifier = childModifier.then(Modifier.align(BiasAlignment(it, it)))
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
