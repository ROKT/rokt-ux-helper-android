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
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun `getPeekThroughDimension clamps negative breakpoint index`() {
        var result: PaddingValues? = null
        composeTestRule.setContent { result = getPeekThroughDimension(-1, 1000, persistentListOf(PeekThroughSizeUiModel.Fixed(10f), PeekThroughSizeUiModel.Fixed(20f)), 1) }
        composeTestRule.waitForIdle()
        assertEquals(10f, result!!.calculateStartPadding(LayoutDirection.Ltr).value)
    }

    @Test
    fun `getPeekThroughDimension clamps positive breakpoint index`() {
        var result: PaddingValues? = null
        composeTestRule.setContent { result = getPeekThroughDimension(10, 1000, persistentListOf(PeekThroughSizeUiModel.Fixed(10f), PeekThroughSizeUiModel.Fixed(20f)), 1) }
        composeTestRule.waitForIdle()
        assertEquals(20f, result!!.calculateStartPadding(LayoutDirection.Ltr).value)
    }

    @Test
    fun `negative peek through values produce non-negative padding`() {
        var fixed: PaddingValues? = null
        var percentage: PaddingValues? = null
        composeTestRule.setContent {
            fixed = getPeekThroughDimension(0, 1000, persistentListOf(PeekThroughSizeUiModel.Fixed(-24f)), 1)
            percentage = getPeekThroughDimension(1, 1000, persistentListOf(PeekThroughSizeUiModel.Fixed(10f), PeekThroughSizeUiModel.Percentage(-50f)), 1)
        }
        composeTestRule.waitForIdle()
        assertNonNegativePadding(requireNotNull(fixed))
        assertNonNegativePadding(requireNotNull(percentage))
    }

    @Test
    fun `available content width never becomes negative`() {
        assertTrue(calculateAvailableWidthForContent(800, 1600f, 0f, 1) >= 0f)
        assertTrue(calculateAvailableWidthForContent(800, 200_000f, 0f, 1) >= 0f)
        assertEquals(480f, calculateAvailableWidthForContent(800, 320f, 0f, 1), 0f)
    }

    @Test
    fun `100 percent peek through never yields negative width`() = assertFullPeekThroughWidthIsNonNegative(PeekThroughSizeUiModel.Percentage(100f))

    @Test
    fun `very large fixed peek through never yields negative width`() = assertFullPeekThroughWidthIsNonNegative(PeekThroughSizeUiModel.Fixed(100_000f))

    private fun assertFullPeekThroughWidthIsNonNegative(size: PeekThroughSizeUiModel) {
        var width = 0f
        composeTestRule.setContent {
            val density = LocalDensity.current
            val padding = getPeekThroughDimension(0, 800, persistentListOf(size), 1)
            val total = with(density) { (padding.calculateStartPadding(LayoutDirection.Ltr) + padding.calculateEndPadding(LayoutDirection.Ltr)).toPx() }
            width = calculateAvailableWidthForContent(800, total, 0f, 1)
        }
        composeTestRule.waitForIdle()
        assertTrue(width >= 0f)
    }

    private fun assertNonNegativePadding(padding: PaddingValues) {
        assertTrue(padding.calculateStartPadding(LayoutDirection.Ltr) >= 0.dp)
        assertTrue(padding.calculateEndPadding(LayoutDirection.Ltr) >= 0.dp)
    }
}
