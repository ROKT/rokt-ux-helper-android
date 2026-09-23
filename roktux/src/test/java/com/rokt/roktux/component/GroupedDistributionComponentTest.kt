package com.rokt.roktux.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
}
