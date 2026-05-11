package com.rokt.roktux.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// position = null means a global (layout-wide) state; non-null is scoped to that offer index.
internal data class CustomStateKey(val position: Int?, val key: String)

// Offer-position state overrides global state on lookup, missing values resolve to 0,
// and toggle defaults from missing to 1.
internal class CustomStateMap(
    initialGlobalStates: Map<String, Int> = emptyMap(),
    initialOfferStates: Map<Int, Map<String, Int>> = emptyMap(),
) {
    private val _state = MutableStateFlow(
        initialCustomStateEntries(
            initialGlobalStates = initialGlobalStates,
            initialOfferStates = initialOfferStates,
        ),
    )
    val state: StateFlow<Map<CustomStateKey, Int>> = _state.asStateFlow()

    fun update(key: CustomStateKey, value: Int) {
        _state.update { currentState -> currentState + (key to value) }
    }

    fun update(position: Int?, key: String, value: Int) {
        update(CustomStateKey(position, key), value)
    }

    fun replaceGlobalStates(states: Map<String, Int>) {
        replace(position = null, states = states)
    }

    fun replaceOfferStates(position: Int, states: Map<String, Int>) {
        replace(position = position, states = states)
    }

    operator fun get(key: CustomStateKey): Int? = state.value[key]

    // Per-offer entry takes precedence; falls back to the global entry when no per-offer value exists.
    fun value(position: Int?, key: String): Int? {
        val currentState = state.value
        return if (position != null) {
            currentState[CustomStateKey(position, key)] ?: currentState[CustomStateKey(null, key)]
        } else {
            currentState[CustomStateKey(null, key)]
        }
    }

    fun valueOrDefault(position: Int?, key: String): Int = value(position, key) ?: DEFAULT_CUSTOM_STATE_VALUE

    fun toggle(position: Int?, key: String): Int {
        val nextValue = if (valueOrDefault(position, key) == ENABLED_CUSTOM_STATE_VALUE) {
            DEFAULT_CUSTOM_STATE_VALUE
        } else {
            ENABLED_CUSTOM_STATE_VALUE
        }
        update(position, key, nextValue)
        return nextValue
    }

    fun globalStates(): Map<String, Int> = statesForPosition(position = null)

    fun offerStates(position: Int): Map<String, Int> = statesForPosition(position = position)

    // Flattened view for a given offer: per-offer keys overwrite global keys via Map `+` semantics.
    fun effectiveStates(position: Int): Map<String, Int> = globalStates() + offerStates(position)

    // Public-facing shape: Int positions become String keys to match RoktViewState.offerCustomStates.
    fun allOfferStates(): Map<String, Map<String, Int>> = state.value.entries
        .mapNotNull { (key, value) ->
            key.position?.let { position ->
                OfferState(position = position, key = key.key, value = value)
            }
        }
        .groupBy { offerState -> offerState.position }
        .toSortedMap()
        .mapKeys { (position, _) -> position.toString() }
        .mapValues { (_, offerStates) ->
            offerStates.associate { offerState -> offerState.key to offerState.value }
        }

    private data class OfferState(val position: Int, val key: String, val value: Int)

    // Replaces all entries for the given scope only; entries for the other scope are preserved.
    private fun replace(position: Int?, states: Map<String, Int>) {
        _state.update { currentState ->
            currentState
                .filterKeys { key -> key.position != position } +
                states.mapKeys { (key, _) -> CustomStateKey(position, key) }
        }
    }

    private fun statesForPosition(position: Int?): Map<String, Int> = state.value.entries
        .filter { (key, _) -> key.position == position }
        .associate { (key, value) -> key.key to value }

    companion object {
        private const val DEFAULT_CUSTOM_STATE_VALUE = 0
        private const val ENABLED_CUSTOM_STATE_VALUE = 1
    }
}

private fun initialCustomStateEntries(
    initialGlobalStates: Map<String, Int>,
    initialOfferStates: Map<Int, Map<String, Int>>,
): Map<CustomStateKey, Int> = buildMap {
    putAll(initialGlobalStates.toCustomStateEntries(position = null))
    initialOfferStates.forEach { (position, states) ->
        putAll(states.toCustomStateEntries(position = position))
    }
}

private fun Map<String, Int>.toCustomStateEntries(position: Int?): Map<CustomStateKey, Int> =
    mapKeys { (key, _) -> CustomStateKey(position = position, key = key) }
