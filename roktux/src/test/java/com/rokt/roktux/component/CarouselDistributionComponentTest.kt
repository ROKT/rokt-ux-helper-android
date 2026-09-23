package com.rokt.roktux.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.modelmapper.uimodel.PeekThroughSizeUiModel
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CarouselDistributionComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `getPeekThroughDimension coerces a negative fixed value to non-negative padding`() {
        var padding: PaddingValues? = null

        composeTestRule.setContent {
            padding = getPeekThroughDimension(
                breakpointIndex = 0,
                viewWidth = 1000,
                peekThroughSizeItems = persistentListOf(PeekThroughSizeUiModel.Fixed(-24f)),
                viewableItems = 1,
            )
        }
        composeTestRule.waitForIdle()

        assertNonNegativePadding(requireNotNull(padding))
    }

    @Test
    fun `getPeekThroughDimension coerces a negative percentage value to non-negative padding across breakpoints`() {
        var paddingAtSecondBreakpoint: PaddingValues? = null

        composeTestRule.setContent {
            paddingAtSecondBreakpoint = getPeekThroughDimension(
                breakpointIndex = 1,
                viewWidth = 1000,
                peekThroughSizeItems = persistentListOf(
                    PeekThroughSizeUiModel.Fixed(10f),
                    PeekThroughSizeUiModel.Percentage(-50f),
                ),
                viewableItems = 1,
            )
        }
        composeTestRule.waitForIdle()

        assertNonNegativePadding(requireNotNull(paddingAtSecondBreakpoint))
    }

    private fun assertNonNegativePadding(padding: PaddingValues) {
        val start = padding.calculateStartPadding(LayoutDirection.Ltr)
        val end = padding.calculateEndPadding(LayoutDirection.Ltr)
        assertTrue("expected start padding >= 0 but was $start", start >= 0.dp)
        assertTrue("expected end padding >= 0 but was $end", end >= 0.dp)
    }
}
