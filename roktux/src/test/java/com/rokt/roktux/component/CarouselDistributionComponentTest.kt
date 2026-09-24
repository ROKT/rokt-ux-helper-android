package com.rokt.roktux.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.modelmapper.uimodel.PeekThroughSizeUiModel
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
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

    @Test
    fun `calculateAvailableWidthForContent coerces to zero when a 100 percent peek-through leaves no room for content`() {
        // A 100 percent peek-through is in range (Percentage is clamped to 0..100 at mapping time),
        // but it makes the padding on each side equal to the full view width, so combined padding
        // meets the view width with nothing left for a page's own content.
        val viewWidth = 800
        val peekThroughPaddingPerSide = viewWidth * (100f / 100)
        val totalHorizontalPadding = peekThroughPaddingPerSide * 2

        val availableWidthForContent = calculateAvailableWidthForContent(
            maxWidth = viewWidth,
            totalHorizontalPaddingPx = totalHorizontalPadding,
            totalPageSpacingPx = 0f,
            viewableItems = 1,
        )

        assertTrue(
            "expected available width >= 0 but was $availableWidthForContent",
            availableWidthForContent >= 0f,
        )
    }

    @Test
    fun `calculateAvailableWidthForContent coerces to zero when a large fixed peek-through leaves no room for content`() {
        // A fixed peek-through size has no upper bound at mapping time, so an arbitrarily large
        // value can dwarf the space actually available to render in.
        val fixedPeekThroughSize = 100_000f
        val totalHorizontalPadding = fixedPeekThroughSize * 2

        val availableWidthForContent = calculateAvailableWidthForContent(
            maxWidth = 800,
            totalHorizontalPaddingPx = totalHorizontalPadding,
            totalPageSpacingPx = 0f,
            viewableItems = 1,
        )

        assertTrue(
            "expected available width >= 0 but was $availableWidthForContent",
            availableWidthForContent >= 0f,
        )
    }

    @Test
    fun `calculateAvailableWidthForContent returns the expected width for a normal peek-through configuration`() {
        // A normal, non-degenerate peek-through (20 percent of an 800px-wide view) still leaves
        // plenty of room for content, and the new clamp must not alter that result.
        val viewWidth = 800
        val peekThroughPaddingPerSide = viewWidth * (20f / 100)
        val totalHorizontalPadding = peekThroughPaddingPerSide * 2

        val availableWidthForContent = calculateAvailableWidthForContent(
            maxWidth = viewWidth,
            totalHorizontalPaddingPx = totalHorizontalPadding,
            totalPageSpacingPx = 0f,
            viewableItems = 1,
        )

        assertEquals(480f, availableWidthForContent, 0f)
    }

    @Test
    fun `full peek-through pipeline never yields a negative available width for a 100 percent peek-through`() {
        var availableWidthForContent = 0f

        composeTestRule.setContent {
            val density = LocalDensity.current
            val viewWidth = 800
            val padding = getPeekThroughDimension(
                breakpointIndex = 0,
                viewWidth = viewWidth,
                peekThroughSizeItems = persistentListOf(PeekThroughSizeUiModel.Percentage(100f)),
                viewableItems = 1,
            )
            val totalHorizontalPadding = with(density) {
                (
                    padding.calculateStartPadding(LayoutDirection.Ltr) +
                        padding.calculateEndPadding(LayoutDirection.Ltr)
                    ).toPx()
            }
            availableWidthForContent = calculateAvailableWidthForContent(
                maxWidth = viewWidth,
                totalHorizontalPaddingPx = totalHorizontalPadding,
                totalPageSpacingPx = 0f,
                viewableItems = 1,
            )
        }
        composeTestRule.waitForIdle()

        assertTrue(
            "expected available width >= 0 but was $availableWidthForContent",
            availableWidthForContent >= 0f,
        )
    }

    @Test
    fun `full peek-through pipeline never yields a negative available width for a very large fixed value`() {
        var availableWidthForContent = 0f

        composeTestRule.setContent {
            val density = LocalDensity.current
            val viewWidth = 800
            val padding = getPeekThroughDimension(
                breakpointIndex = 0,
                viewWidth = viewWidth,
                peekThroughSizeItems = persistentListOf(PeekThroughSizeUiModel.Fixed(100_000f)),
                viewableItems = 1,
            )
            val totalHorizontalPadding = with(density) {
                (
                    padding.calculateStartPadding(LayoutDirection.Ltr) +
                        padding.calculateEndPadding(LayoutDirection.Ltr)
                    ).toPx()
            }
            availableWidthForContent = calculateAvailableWidthForContent(
                maxWidth = viewWidth,
                totalHorizontalPaddingPx = totalHorizontalPadding,
                totalPageSpacingPx = 0f,
                viewableItems = 1,
            )
        }
        composeTestRule.waitForIdle()

        assertTrue(
            "expected available width >= 0 but was $availableWidthForContent",
            availableWidthForContent >= 0f,
        )
    }
}
