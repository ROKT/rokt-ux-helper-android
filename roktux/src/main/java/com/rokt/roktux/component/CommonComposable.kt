package com.rokt.roktux.component

import androidx.compose.runtime.Composable
import com.rokt.modelmapper.uimodel.ConditionalStyleState
import com.rokt.modelmapper.uimodel.WhenUiPredicate
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun evaluateState(
    predicates: ImmutableList<WhenUiPredicate>?,
    breakpointIndex: Int,
    isDarkModeEnabled: Boolean,
    offerState: OfferUiState,
): ConditionalStyleState? = predicates.takeIf { it?.isNotEmpty() == true }?.let {
    if (evaluateWhenPredicates(
            predicates = it,
            breakpointIndex = breakpointIndex,
            isDarkModeEnabled = isDarkModeEnabled,
            offerState = offerState,
        )
    ) {
        ConditionalStyleState.Transition
    } else {
        ConditionalStyleState.Normal
    }
}
