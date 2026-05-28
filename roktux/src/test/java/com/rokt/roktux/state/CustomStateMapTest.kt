package com.rokt.roktux.state

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CustomStateMapTest {

    @Test
    fun `initialises global and offer states with typed keys`() {
        val customStateMap = CustomStateMap(
            initialGlobalStates = mapOf("ReadMoreState" to 1),
            initialOfferStates = mapOf(2 to mapOf("DataImageCarousel.hero" to 3)),
        )

        assertThat(customStateMap[CustomStateKey(position = null, key = "ReadMoreState")]).isEqualTo(1)
        assertThat(customStateMap[CustomStateKey(position = 2, key = "DataImageCarousel.hero")]).isEqualTo(3)
        assertThat(customStateMap.globalStates()).containsEntry("ReadMoreState", 1)
        assertThat(customStateMap.offerStates(2)).containsEntry("DataImageCarousel.hero", 3)
    }

    @Test
    fun `offer state overrides global state when resolving effective values`() {
        val customStateMap = CustomStateMap(
            initialGlobalStates = mapOf("shared" to 1, "globalOnly" to 4),
            initialOfferStates = mapOf(1 to mapOf("shared" to 2, "localOnly" to 3)),
        )

        assertThat(customStateMap.valueOrDefault(position = 1, key = "shared")).isEqualTo(2)
        assertThat(customStateMap.valueOrDefault(position = 2, key = "shared")).isEqualTo(1)
        assertThat(customStateMap.effectiveStates(1)).containsEntry("shared", 2)
        assertThat(customStateMap.effectiveStates(1)).containsEntry("globalOnly", 4)
        assertThat(customStateMap.effectiveStates(1)).containsEntry("localOnly", 3)
    }

    @Test
    fun `missing state resolves to zero`() {
        val customStateMap = CustomStateMap()

        assertThat(customStateMap.value(position = 0, key = "missing")).isNull()
        assertThat(customStateMap.valueOrDefault(position = 0, key = "missing")).isEqualTo(0)
    }

    @Test
    fun `toggle defaults missing state to enabled and flips existing enabled value`() {
        val customStateMap = CustomStateMap()

        assertThat(customStateMap.toggle(position = 0, key = "ToggleButtonState")).isEqualTo(1)
        assertThat(customStateMap[CustomStateKey(position = 0, key = "ToggleButtonState")]).isEqualTo(1)

        assertThat(customStateMap.toggle(position = 0, key = "ToggleButtonState")).isEqualTo(0)
        assertThat(customStateMap[CustomStateKey(position = 0, key = "ToggleButtonState")]).isEqualTo(0)
    }

    @Test
    fun `replaceOfferStates preserves global and other offer states`() {
        val customStateMap = CustomStateMap(
            initialGlobalStates = mapOf("global" to 1),
            initialOfferStates = mapOf(
                0 to mapOf("first" to 1),
                1 to mapOf("second" to 2),
            ),
        )

        customStateMap.replaceOfferStates(position = 0, states = mapOf("replacement" to 9))

        assertThat(customStateMap.globalStates()).containsEntry("global", 1)
        assertThat(customStateMap.offerStates(0)).containsOnlyKeys("replacement")
        assertThat(customStateMap.offerStates(0)).containsEntry("replacement", 9)
        assertThat(customStateMap.offerStates(1)).containsEntry("second", 2)
        assertThat(customStateMap.allOfferStates()).containsEntry("0", mapOf("replacement" to 9))
        assertThat(customStateMap.allOfferStates()).containsEntry("1", mapOf("second" to 2))
    }
}
