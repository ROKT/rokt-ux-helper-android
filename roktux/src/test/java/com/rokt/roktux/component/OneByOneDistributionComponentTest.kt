package com.rokt.roktux.component

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.TransitionUiModel
import com.rokt.roktux.di.layout.LayoutComponent
import com.rokt.roktux.di.layout.LocalLayoutComponent
import com.rokt.roktux.imagehandler.NetworkStrategy
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Exercises the real [OneByOneDistributionComponent] (no NavHost) with a fake `onEventSent`,
 * asserting the event-firing/timing guarantees that used to depend on NavHost's back-stack
 * lifecycle. Renders with an empty experience response — there's no real creative content to
 * click, so response-option/shouldProgress behavior and per-offer visual state are covered instead
 * by [OfferScopedViewModelStoreOwnerTest] (ViewModelStore identity) and the Roborazzi snapshot
 * tests, which do use real creative fixtures.
 *
 * State changes are made via [composeTestRule]'s `runOnIdle`, not a bare assignment: with
 * `mainClock.autoAdvance = false`, a write from the raw test thread is not reliably observed by
 * the composition before the next manual clock advance.
 */
@RunWith(AndroidJUnit4::class)
class OneByOneDistributionComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun offerState(current: Int, target: Int = current, last: Int = 3) = OfferUiState(
        currentOfferIndex = current,
        lastOfferIndex = last,
        viewableItems = 1,
        targetOfferIndex = target,
        creativeCopy = persistentMapOf(),
        breakpoints = persistentMapOf("default" to 0),
        customState = persistentMapOf(),
    )

    private fun layoutComponent() = LayoutComponent(
        experienceResponse = "",
        parsedExperienceResponse = null,
        location = "",
        startTimeStamp = System.currentTimeMillis(),
        onUxEvent = {},
        onPlatformEvent = {},
        onViewStateChange = {},
        imageLoader = NetworkStrategy().getImageLoader(InstrumentationRegistry.getInstrumentation().targetContext),
        handleUrlByApp = true,
        currentOffer = 0,
        customStates = mapOf(),
        offerCustomStates = mapOf(),
        domainStates = mapOf(),
        edgeToEdgeDisplay = false,
        mainDispatcher = Dispatchers.Main,
        ioDispatcher = Dispatchers.IO,
    )

    private fun setContent(state: () -> OfferUiState, onEventSent: (LayoutContract.LayoutEvent) -> Unit) {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalLayoutComponent provides layoutComponent()) {
                OneByOneDistributionComponent(LayoutUiModelFactory(), ModifierFactory()).Render(
                    model = LayoutSchemaUiModel.OneByOneDistributionUiModel(
                        ownModifiers = null,
                        containerProperties = null,
                        conditionalTransitionModifiers = null,
                        transition = TransitionUiModel.FadeInOutTransition(duration = 200),
                    ),
                    modifier = Modifier,
                    isPressed = false,
                    offerState = state(),
                    isDarkModeEnabled = false,
                    breakpointIndex = 0,
                    onEventSent = onEventSent,
                )
            }
        }
    }

    @Test
    fun `FirstOfferLoaded fires exactly once regardless of subsequent offer changes`() {
        val events = mutableListOf<LayoutContract.LayoutEvent>()
        var state by mutableStateOf(offerState(current = 0))
        composeTestRule.mainClock.autoAdvance = false
        setContent({ state }, { events.add(it) })
        composeTestRule.mainClock.advanceTimeBy(1_000)

        composeTestRule.runOnIdle { state = state.copy(currentOfferIndex = 1, targetOfferIndex = 1) }
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(1_000)
        composeTestRule.runOnIdle { state = state.copy(currentOfferIndex = 2, targetOfferIndex = 2) }
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(1_000)

        assertEquals(1, events.count { it == LayoutContract.LayoutEvent.FirstOfferLoaded })
    }

    @Test
    fun `advancing the offer fires SetCurrentOffer exactly once, only after the fade completes`() {
        val events = mutableListOf<LayoutContract.LayoutEvent>()
        var state by mutableStateOf(offerState(current = 0))
        composeTestRule.mainClock.autoAdvance = false
        setContent({ state }, { events.add(it) })
        composeTestRule.mainClock.advanceTimeBy(1_000)

        composeTestRule.runOnIdle { state = state.copy(targetOfferIndex = 1) }
        composeTestRule.waitForIdle()

        assertTrue(
            "SetCurrentOffer must not fire before the fade-out animation completes",
            events.none { it is LayoutContract.LayoutEvent.SetCurrentOffer },
        )

        composeTestRule.mainClock.advanceTimeBy(1_000)

        assertEquals(1, events.count { it is LayoutContract.LayoutEvent.SetCurrentOffer })
        assertEquals(
            1,
            (events.first { it is LayoutContract.LayoutEvent.SetCurrentOffer } as LayoutContract.LayoutEvent.SetCurrentOffer)
                .currentOffer,
        )
    }

    @Test
    fun `a there-and-back-again offer sequence fires SetCurrentOffer once per transition, no duplicates or skips`() {
        val events = mutableListOf<LayoutContract.LayoutEvent>()
        var state by mutableStateOf(offerState(current = 0))
        composeTestRule.mainClock.autoAdvance = false
        setContent({ state }, { events.add(it) })
        composeTestRule.mainClock.advanceTimeBy(1_000)

        // 0 -> 1
        composeTestRule.runOnIdle { state = state.copy(targetOfferIndex = 1) }
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(1_000)
        composeTestRule.runOnIdle { state = state.copy(currentOfferIndex = 1) }
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(1_000)

        // 1 -> 0
        composeTestRule.runOnIdle { state = state.copy(targetOfferIndex = 0) }
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(1_000)
        composeTestRule.runOnIdle { state = state.copy(currentOfferIndex = 0) }
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(1_000)

        val setCurrentOffers = events.filterIsInstance<LayoutContract.LayoutEvent.SetCurrentOffer>()
        assertEquals(listOf(1, 0), setCurrentOffers.map { it.currentOffer })
    }
}
