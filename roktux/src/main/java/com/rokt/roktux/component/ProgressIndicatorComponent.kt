package com.rokt.roktux.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.data.BindState
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlin.math.ceil

@OptIn(ExperimentalComposeUiApi::class)
internal class ProgressIndicatorComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.ProgressIndicatorUiModel> {

    private val accessibilityReadOutText = "Offer %d of %d"

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.ProgressIndicatorUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val startPosition = remember(model.startPosition) {
            model.startPosition - 1 // startPosition is 1-based
        }
        if (offerState.currentOfferIndex >= startPosition) {
            val container = modifierFactory.createContainerUiProperties(
                containerProperties = model.containerProperties,
                index = breakpointIndex,
                isPressed = isPressed,
            )
            val totalPages = remember(offerState.lastOfferIndex + 1, offerState.viewableItems) {
                ceil((offerState.lastOfferIndex + 1).toDouble() / offerState.viewableItems).toInt()
            }
            val textList: List<String>? = remember(model, totalPages) {
                when (val indicatorText = model.indicatorText) {
                    is BindData.State -> when (indicatorText.state) {
                        BindState.OFFER_POSITION -> {
                            List(totalPages) { index -> (index + 1).toString() }
                        }
                    }

                    is BindData.Value -> List(totalPages) { indicatorText.text }

                    is BindData.Undefined -> null
                }
            }
            if (textList != null) {
                LazyRow(
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
                        .semantics(mergeDescendants = true) {}
                        .clearAndSetSemantics {
                            if (model.accessibilityHidden) {
                                invisibleToUser()
                            } else {
                                contentDescription = accessibilityReadOutText.format(
                                    offerState.currentOfferIndex,
                                    offerState.lastOfferIndex,
                                )
                            }
                        },
                    horizontalArrangement = container.horizontalArrangement,
                    verticalAlignment = BiasAlignment.Vertical(
                        container.alignmentBias,
                    ),
                ) {
                    items(totalPages - (startPosition)) { index ->
                        val indicatorIndex = index + startPosition
                        val indicator = when {
                            indicatorIndex < ceil(offerState.currentOfferIndex.toDouble() / offerState.viewableItems) -> {
                                model.seenIndicator ?: model.indicator
                            }

                            indicatorIndex == ceil(offerState.currentOfferIndex.toDouble() / offerState.viewableItems).toInt() -> {
                                model.activeIndicator ?: model.seenIndicator ?: model.indicator
                            }

                            else -> {
                                model.indicator
                            }
                        }
                        IndicatorItemComponent(
                            model = indicator,
                            baseModel = model.indicator,
                            text = textList[index + startPosition],
                            isPressed = isPressed,
                            isDarkModeEnabled = isDarkModeEnabled,
                            breakpointIndex = breakpointIndex,
                            offerState = offerState,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun IndicatorItemComponent(
        model: LayoutSchemaUiModel.ProgressIndicatorItemUiModel,
        baseModel: LayoutSchemaUiModel.ProgressIndicatorItemUiModel,
        text: String,
        isPressed: Boolean,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        offerState: OfferUiState,
    ) {
        val container = modifierFactory.createContainerUiProperties(
            containerProperties = model.containerProperties,
            index = breakpointIndex,
            isPressed = isPressed,
            baseProperties = baseModel.containerProperties,
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
                    basePropertiesList = baseModel.ownModifiers,
                )
                .semantics(mergeDescendants = true) { },
            contentAlignment = BiasAlignment(container.arrangementBias, container.alignmentBias),
        ) {
            if (text.isNotEmpty()) {
                val textStyleUiState = modifierFactory.createTextStyle(
                    text = text,
                    textStyles = model.textStyles,
                    breakpointIndex = breakpointIndex,
                    isPressed = isPressed,
                    isDarkModeEnabled = false,
                    baseStyles = baseModel.textStyles,
                    offerState = offerState,
                )
                Text(
                    text = textStyleUiState.value,
                    style = textStyleUiState.textStyle,
                )
            }
        }
    }
}
