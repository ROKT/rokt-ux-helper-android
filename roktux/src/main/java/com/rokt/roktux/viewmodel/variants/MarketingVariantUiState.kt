package com.rokt.roktux.viewmodel.variants

import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import kotlinx.collections.immutable.ImmutableMap

internal data class MarketingVariantUiState(
    val uiModel: LayoutSchemaUiModel,
    val creativeCopy: ImmutableMap<String, String>,
)
