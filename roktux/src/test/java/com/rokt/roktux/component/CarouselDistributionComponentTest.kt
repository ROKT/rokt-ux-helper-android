package com.rokt.roktux.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiNodeComponentState
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.modelmapper.uimodel.PeekThroughSizeUiModel
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CarouselDistributionComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "CarouselDistributionComponent/ScrollableRow_with_CarouselDistribution.json")
    fun `carousel renders inside a scrollable row`() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "CarouselDistributionComponent/ScrollableRow_with_CarouselDistribution.json")
    @DcuiNodeComponentState(totalOffer = 4)
    fun `the page position announcement does not wrap the carousel pages`() {
        composeTestRule.onNode(hasContentDescription("Page 1 of 4")).onChildren().assertCountEquals(0)
    }

    @Test
    @DcuiNodeJson(jsonFile = "CarouselDistributionComponent/ScrollableRow_with_CarouselDistribution.json")
    @DcuiNodeComponentState(totalOffer = 4)
    fun `the page position announcement sits above the pages, so it is read first`() {
        val announcement = composeTestRule.onNode(hasContentDescription("Page 1 of 4")).getUnclippedBoundsInRoot()
        val pager = composeTestRule.onNode(hasScrollToIndexAction()).getUnclippedBoundsInRoot()
        assertTrue("announcement must end above the pages", announcement.bottom <= pager.top)
    }

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
