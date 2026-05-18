package com.rokt.roktux.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.component.ComposableComponent
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.component.ModifierFactory
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

internal class CatalogDevicePayButtonComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.CatalogDevicePayButtonUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.CatalogDevicePayButtonUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val onClick = {
            onEventSent(
                LayoutContract.LayoutEvent.CartItemDevicePaySelected(
                    offerId = offerState.currentOfferIndex,
                    catalogItemModel = model.catalogItemModel,
                    paymentProvider = model.paymentProvider,
                    transactionData = model.transactionData,
                    validatorFieldKeys = model.validatorFieldKeys,
                ),
            )
        }

        ButtonComponent(
            model = model,
            factory = factory,
            modifierFactory = modifierFactory,
            modifier = modifier,
            offerState = offerState,
            isDarkModeEnabled = isDarkModeEnabled,
            breakpointIndex = breakpointIndex,
            ignoreChildrenForAccessibility = true,
            onEventSent = onEventSent,
            onClicked = onClick,
        )
    }
}
