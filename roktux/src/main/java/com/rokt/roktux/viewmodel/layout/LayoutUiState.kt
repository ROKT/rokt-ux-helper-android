package com.rokt.roktux.viewmodel.layout

import androidx.compose.runtime.Immutable
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import kotlinx.collections.immutable.ImmutableMap

internal data class LayoutUiState(val model: LayoutSchemaUiModel, val offerUiState: OfferUiState)

@Immutable
internal data class OfferUiState(
    val currentOfferIndex: Int,
    val lastOfferIndex: Int,
    val viewableItems: Int,
    val targetOfferIndex: Int = currentOfferIndex,
    val creativeCopy: ImmutableMap<String, String>,
    val breakpoints: ImmutableMap<String, Int>,
    val customState: ImmutableMap<String, Int>,
)
