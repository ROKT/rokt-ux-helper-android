package com.rokt.roktux.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.rokt.modelmapper.hmap.get
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl.Companion.KEY_IS_POSITIVE
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.component.ComposableComponent
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.component.ModifierFactory
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

internal class CreativeResponseComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.CreativeResponseUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.CreativeResponseUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        model.responseOption?.let { responseOptionModel ->
            ButtonComponent(
                model = model,
                factory = factory,
                modifierFactory = modifierFactory,
                modifier = modifier
                    .testTag(
                        if (responseOptionModel.properties.get<Boolean>(KEY_IS_POSITIVE) ==
                            true
                        ) {
                            "positive_button"
                        } else {
                            "negative_button"
                        },
                    ),
                offerState = offerState,
                isDarkModeEnabled = isDarkModeEnabled,
                breakpointIndex = breakpointIndex,
                onEventSent = onEventSent,
            ) {
                onEventSent.invoke(
                    LayoutContract.LayoutEvent.ResponseOptionSelected(
                        offerState.currentOfferIndex,
                        model.openLinks,
                        responseOptionModel.properties,
                    ),
                )
            }
        }
    }
}
