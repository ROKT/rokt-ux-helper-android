package com.rokt.roktux.viewmodel.layout

import androidx.compose.runtime.Immutable
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap

internal data class LayoutUiState(val model: LayoutSchemaUiModel, val offerUiState: OfferUiState)

@Immutable
internal data class OfferUiState(
    val currentOfferIndex: Int,

    // This represents the index of the last offer e.g. the number of offers / slots - 1
    val lastOfferIndex: Int,

    // How many offers to be displayed on screen at once
    val viewableItems: Int,

    val targetOfferIndex: Int = currentOfferIndex,
    val creativeCopy: ImmutableMap<String, String>,
    val breakpoints: ImmutableMap<String, Int>,
    val customState: ImmutableMap<String, Int>,
    val offerCustomStates: ImmutableMap<String, ImmutableMap<String, Int>> =
        emptyMap<String, ImmutableMap<String, Int>>().toImmutableMap(),
)
