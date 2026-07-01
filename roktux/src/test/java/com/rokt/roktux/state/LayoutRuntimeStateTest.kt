package com.rokt.roktux.state

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LayoutRuntimeStateTest {

    @Test
    fun `initialises custom state from public view state maps`() {
        val runtimeState = LayoutRuntimeState(
            customStates = mapOf("ReadMoreState" to 1),
            offerCustomStates = mapOf(
                "2" to mapOf("DataImageCarousel.hero" to 3),
                "not-an-offer-position" to mapOf("ignored" to 4),
            ),
            domainStates = mapOf("checkout" to 2),
        )

        assertThat(runtimeState.globalCustomStates()).containsEntry("ReadMoreState", 1)
        assertThat(runtimeState.offerCustomStates(2)).containsEntry("DataImageCarousel.hero", 3)
        assertThat(runtimeState.allOfferCustomStates()).containsOnlyKeys("2")
        assertThat(runtimeState.domainStates()).containsEntry("checkout", 2)
    }

    @Test
    fun `updates domain state values`() {
        val runtimeState = LayoutRuntimeState()

        runtimeState.setDomainState("offerComplete", 1)
        runtimeState.setDomainState("layoutMinimized", 0)

        assertThat(runtimeState.domainStates()).containsEntry("offerComplete", 1)
        assertThat(runtimeState.domainStates()).containsEntry("layoutMinimized", 0)
    }
}
