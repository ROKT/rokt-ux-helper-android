package com.rokt.roktux.state

import com.rokt.roktux.validation.ValidationCoordinator

// Typed container for per-layout runtime state owned by LayoutViewModel/MarketingVariantViewModel,
// replacing the previous generic `Any` bag so call sites get compile-time safety.
internal class LayoutRuntimeState(
    val customStateMap: CustomStateMap = CustomStateMap(),
    val validationCoordinator: ValidationCoordinator = ValidationCoordinator(),
) {
    // Convenience constructor for seeding from the public RoktViewState shape,
    // where offer positions arrive as String keys from persisted JSON.
    constructor(
        customStates: Map<String, Int>,
        offerCustomStates: Map<String, Map<String, Int>>,
        validationCoordinator: ValidationCoordinator = ValidationCoordinator(),
    ) : this(
        customStateMap = CustomStateMap(
            initialGlobalStates = customStates,
            initialOfferStates = offerCustomStates.toPositionedOfferStates(),
        ),
        validationCoordinator = validationCoordinator,
    )

    fun setGlobalCustomState(key: String, value: Int) {
        customStateMap.update(position = null, key = key, value = value)
    }

    fun setOfferCustomState(position: Int, key: String, value: Int) {
        customStateMap.update(position = position, key = key, value = value)
    }

    fun replaceGlobalCustomStates(states: Map<String, Int>) {
        customStateMap.replaceGlobalStates(states)
    }

    fun replaceOfferCustomStates(position: Int, states: Map<String, Int>) {
        customStateMap.replaceOfferStates(position, states)
    }

    fun globalCustomStates(): Map<String, Int> = customStateMap.globalStates()

    fun offerCustomStates(position: Int): Map<String, Int> = customStateMap.offerStates(position)

    fun effectiveCustomStates(position: Int): Map<String, Int> = customStateMap.effectiveStates(position)

    fun allOfferCustomStates(): Map<String, Map<String, Int>> = customStateMap.allOfferStates()
}

// Tolerant conversion: non-numeric position keys from persisted state are dropped rather than crashing.
private fun Map<String, Map<String, Int>>.toPositionedOfferStates(): Map<Int, Map<String, Int>> =
    mapNotNull { (position, states) ->
        position.toIntOrNull()?.let { offerPosition -> offerPosition to states }
    }.toMap()
