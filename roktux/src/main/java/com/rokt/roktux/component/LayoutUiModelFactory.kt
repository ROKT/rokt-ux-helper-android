package com.rokt.roktux.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel.ToggleButtonStateTriggerUiModel
import com.rokt.roktux.component.button.CloseButtonComponent
import com.rokt.roktux.component.button.CreativeResponseComponent
import com.rokt.roktux.component.button.ProgressControlComponent
import com.rokt.roktux.component.button.StaticLinkComponent
import com.rokt.roktux.component.button.ToggleButtonStateTriggerComponent
import com.rokt.roktux.utils.tryCast
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlin.reflect.KClass

@Immutable
internal class LayoutUiModelFactory(
    modifierFactory: ModifierFactory = ModifierFactory(),
) {
    private val composableMap:
        ImmutableMap<KClass<out LayoutSchemaUiModel>, ComposableComponent<out LayoutSchemaUiModel>> =
        persistentMapOf(
            LayoutSchemaUiModel.BasicTextUiModel::class to BasicTextComponent(modifierFactory),
            LayoutSchemaUiModel.RichTextUiModel::class to RichTextComponent(modifierFactory),
            LayoutSchemaUiModel.ColumnUiModel::class to ColumnComponent(this, modifierFactory),
            LayoutSchemaUiModel.RowUiModel::class to RowComponent(this, modifierFactory),
            LayoutSchemaUiModel.BoxUiModel::class to BoxComponent(this, modifierFactory),
            LayoutSchemaUiModel.ProgressIndicatorUiModel::class to ProgressIndicatorComponent(
                this,
                modifierFactory,
            ),
            LayoutSchemaUiModel.MarketingUiModel::class to LayoutVariantMarketingComponent(this),
            LayoutSchemaUiModel.CreativeResponseUiModel::class to CreativeResponseComponent(this, modifierFactory),
            LayoutSchemaUiModel.CloseButtonUiModel::class to CloseButtonComponent(this, modifierFactory),
            LayoutSchemaUiModel.StaticLinkUiModel::class to StaticLinkComponent(this, modifierFactory),
            ToggleButtonStateTriggerUiModel::class to ToggleButtonStateTriggerComponent(
                this,
                modifierFactory,
            ),
            LayoutSchemaUiModel.ProgressControlUiModel::class to ProgressControlComponent(
                this,
                modifierFactory,
            ),
            LayoutSchemaUiModel.OneByOneDistributionUiModel::class to OneByOneDistributionComponent(
                this,
                modifierFactory,
            ),
            LayoutSchemaUiModel.GroupedDistributionUiModel::class to GroupedDistributionComponent(
                this,
                modifierFactory,
            ),
            LayoutSchemaUiModel.CarouselDistributionUiModel::class to CarouselDistributionComponent(
                this,
                modifierFactory,
            ),
            LayoutSchemaUiModel.OverlayUiModel::class to OverlayComponent(
                this,
                modifierFactory,
            ),
            LayoutSchemaUiModel.BottomSheetUiModel::class to BottomSheetComponent(
                this,
                modifierFactory,
            ),
            LayoutSchemaUiModel.ImageUiModel::class to ImageComponent(
                modifierFactory,
            ),
            LayoutSchemaUiModel.IconUiModel::class to IconComponent(modifierFactory),
            LayoutSchemaUiModel.WhenUiModel::class to WhenComponent(
                this,
                modifierFactory,
            ),
        )

    @Composable
    fun <T : LayoutSchemaUiModel> CreateComposable(
        model: T,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        composableMap[model::class].tryCast<ComposableComponent<LayoutSchemaUiModel>> {
            Render(
                model = model,
                modifier = modifier,
                isPressed = isPressed,
                offerState = offerState,
                breakpointIndex = breakpointIndex,
                isDarkModeEnabled = isDarkModeEnabled,
                onEventSent = onEventSent,
            )
        }
    }
}

@Immutable
internal interface ComposableComponent<T : LayoutSchemaUiModel> {
    @Composable
    fun Render(
        model: T,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    )
}
