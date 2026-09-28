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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Exercises the real [GroupedDistributionComponent] (no NavHost) with a fake `onEventSent`. See
 * [OneByOneDistributionComponentTest]'s kdoc for why response-option/shouldProgress behavior and
 * per-offer visual state are covered elsewhere instead of here, and why state changes go through
 * `runOnIdle` rather than a bare assignment.
 */
@RunWith(AndroidJUnit4::class)
class GroupedDistributionComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `getViewableItems clamps a negative breakpoint index to the first entry instead of throwing`() {
        // A malformed breakpoints payload can otherwise resolve to a negative breakpoint index.
        // getViewableItems must never index out of bounds regardless of what it is handed.
        var result = -1
        composeTestRule.setContent {
            result = getViewableItems(
                breakpointIndex = -1,
                viewableItemsList = persistentListOf(2, 3, 4),
                lastOfferIndex = 10,
            )
        }
        composeTestRule.waitForIdle()

        // Index -1 clamps to index 0, i.e. the same result as breakpointIndex = 0.
        assertEquals(2, result)
    }

    @Test
    fun `getViewableItems clamps an out-of-range positive breakpoint index to the last entry`() {
        var result = -1
        composeTestRule.setContent {
            result = getViewableItems(
                breakpointIndex = 10,
                viewableItemsList = persistentListOf(2, 3, 4),
                lastOfferIndex = 10,
            )
        }
        composeTestRule.waitForIdle()

        assertEquals(4, result)
    }

    private fun offerState(current: Int, target: Int = current, last: Int = 3, viewableItems: Int = 1) = OfferUiState(
        currentOfferIndex = current,
        lastOfferIndex = last,
        viewableItems = viewableItems,
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

    private fun setContent(
        state: () -> OfferUiState,
        viewableItemsPerBreakpoint: List<Int> = emptyList(),
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalLayoutComponent provides layoutComponent()) {
                GroupedDistributionComponent(LayoutUiModelFactory(), ModifierFactory()).Render(
                    model = LayoutSchemaUiModel.GroupedDistributionUiModel(
                        ownModifiers = null,
                        containerProperties = null,
                        conditionalTransitionModifiers = null,
                        viewableItems = viewableItemsPerBreakpoint.toPersistentList(),
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
        setContent({ state }, onEventSent = { events.add(it) })
        composeTestRule.mainClock.advanceTimeBy(1_000)

        composeTestRule.runOnIdle { state = state.copy(currentOfferIndex = 1, targetOfferIndex = 1) }
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(1_000)

        assertEquals(1, events.count { it == LayoutContract.LayoutEvent.FirstOfferLoaded })
    }

    @Test
    fun `ViewableItemsChanged fires once for the initial value and again only when it actually changes`() {
        val events = mutableListOf<LayoutContract.LayoutEvent>()
        var breakpointIndexState by mutableStateOf(0)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalLayoutComponent provides layoutComponent()) {
                GroupedDistributionComponent(LayoutUiModelFactory(), ModifierFactory()).Render(
                    model = LayoutSchemaUiModel.GroupedDistributionUiModel(
                        ownModifiers = null,
                        containerProperties = null,
                        conditionalTransitionModifiers = null,
                        viewableItems = persistentListOf(1, 2),
                        transition = TransitionUiModel.FadeInOutTransition(duration = 0),
                    ),
                    modifier = Modifier,
                    isPressed = false,
                    offerState = offerState(current = 0, last = 5),
                    isDarkModeEnabled = false,
                    breakpointIndex = breakpointIndexState,
                    onEventSent = { events.add(it) },
                )
            }
        }
        composeTestRule.waitForIdle()
        assertEquals(
            listOf(1),
            events.filterIsInstance<LayoutContract.LayoutEvent.ViewableItemsChanged>().map { it.viewableItems },
        )

        // Recompose without changing the resolved viewableItems value (still breakpoint 0) - must not re-fire.
        composeTestRule.runOnIdle { breakpointIndexState = 0 }
        composeTestRule.waitForIdle()
        assertEquals(1, events.count { it is LayoutContract.LayoutEvent.ViewableItemsChanged })

        // Switch to the breakpoint whose resolved value actually differs (index 1 -> viewableItems 2).
        composeTestRule.runOnIdle { breakpointIndexState = 1 }
        composeTestRule.waitForIdle()
        assertEquals(
            listOf(1, 2),
            events.filterIsInstance<LayoutContract.LayoutEvent.ViewableItemsChanged>().map { it.viewableItems },
        )
    }

    @Test
    fun `advancing the offer fires SetCurrentOffer exactly once, only after the fade completes`() {
        val events = mutableListOf<LayoutContract.LayoutEvent>()
        var state by mutableStateOf(offerState(current = 0))
        composeTestRule.mainClock.autoAdvance = false
        setContent({ state }, onEventSent = { events.add(it) })
        composeTestRule.mainClock.advanceTimeBy(1_000)

        composeTestRule.runOnIdle { state = state.copy(targetOfferIndex = 1) }
        composeTestRule.waitForIdle()

        assertTrue(
            "SetCurrentOffer must not fire before the fade-out animation completes",
            events.none { it is LayoutContract.LayoutEvent.SetCurrentOffer },
        )

        composeTestRule.mainClock.advanceTimeBy(1_000)

        assertEquals(1, events.count { it is LayoutContract.LayoutEvent.SetCurrentOffer })
    }
}
