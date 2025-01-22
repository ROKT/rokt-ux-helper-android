package com.rokt.roktux.component.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.component.ComposableComponent
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.component.ModifierFactory
import com.rokt.roktux.component.extensions.timertrigger.CountDownTimerComponent
import com.rokt.roktux.component.extensions.timertrigger.CountdownTimerModel
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

internal class EscapeHatchComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.EscapeHatchUiModel> {

    private val escapeHatchExtensionComponents = persistentMapOf(
        "count-down-timer" to CountDownTimerComponent(factory, modifierFactory),
    )

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.EscapeHatchUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val json = Json { ignoreUnknownKeys = true }
        val extensionData = json.decodeFromString<ExtensionData>(model.data)
        val extensionComponent = escapeHatchExtensionComponents[extensionData.name]

        if (extensionComponent != null) {
            val extensionBodyModel = json.decodeFromString(
                extensionComponent.getSerializer(),
                extensionData.body,
            )
            extensionComponent.Render(
                extensionBodyModel,
                extensionData,
                modifier,
                isPressed,
                offerState,
                isDarkModeEnabled,
                breakpointIndex,
                onEventSent,
            )
        } else {
            model.backupElement?.let { child ->
                val childModifier = Modifier
                modifierFactory.createContainerUiProperties(
                    containerProperties = child.containerProperties,
                    index = breakpointIndex,
                    isPressed = isPressed,
                )
                factory.CreateComposable(
                    model = child,
                    modifier = childModifier,
                    isPressed = isPressed,
                    breakpointIndex = breakpointIndex,
                    offerState = offerState,
                    isDarkModeEnabled = isDarkModeEnabled,
                    onEventSent = onEventSent,
                )
            }
        }
    }
}

@Immutable
internal interface ExtensionComposableComponent<T> {
    @Composable
    fun Render(
        model: T,
        data: ExtensionData,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    )

    fun getSerializer(): KSerializer<T>
}
