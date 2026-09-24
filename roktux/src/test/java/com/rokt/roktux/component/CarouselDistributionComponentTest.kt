package com.rokt.roktux.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.modelmapper.uimodel.PeekThroughSizeUiModel
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CarouselDistributionComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `getPeekThroughDimension clamps a negative breakpoint index to the first entry instead of throwing`() {
        // A malformed breakpoints payload can otherwise resolve to a negative breakpoint index.
        // getPeekThroughDimension must never index out of bounds regardless of what it is handed.
        var result: PaddingValues? = null
        composeTestRule.setContent {
            result = getPeekThroughDimension(
                breakpointIndex = -1,
                viewWidth = 1000,
                peekThroughSizeItems = persistentListOf(
                    PeekThroughSizeUiModel.Fixed(10f),
                    PeekThroughSizeUiModel.Fixed(20f),
                ),
                viewableItems = 1,
            )
        }
        composeTestRule.waitForIdle()

        // Index -1 clamps to index 0, i.e. the same result as breakpointIndex = 0.
        assertEquals(10f, result!!.calculateStartPadding(LayoutDirection.Ltr).value)
    }

    @Test
    fun `getPeekThroughDimension clamps an out-of-range positive breakpoint index to the last entry`() {
        var result: PaddingValues? = null
        composeTestRule.setContent {
            result = getPeekThroughDimension(
                breakpointIndex = 10,
                viewWidth = 1000,
                peekThroughSizeItems = persistentListOf(
                    PeekThroughSizeUiModel.Fixed(10f),
                    PeekThroughSizeUiModel.Fixed(20f),
                ),
                viewableItems = 1,
            )
        }
        composeTestRule.waitForIdle()

        assertEquals(20f, result!!.calculateStartPadding(LayoutDirection.Ltr).value)
    }
}
