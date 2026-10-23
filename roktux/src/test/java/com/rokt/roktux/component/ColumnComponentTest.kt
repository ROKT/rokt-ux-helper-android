package com.rokt.roktux.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.width
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.TestJsonLoader
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiBreakpoint
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeComponentState
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.core.testutils.annotations.WindowSize
import com.rokt.core.testutils.assertion.assertBackgroundColor
import com.rokt.core.testutils.assertion.assertHeightFit
import com.rokt.core.testutils.assertion.assertHeightWrapContent
import com.rokt.core.testutils.assertion.assertOffsetValues
import com.rokt.core.testutils.assertion.assertPaddingValues
import com.rokt.core.testutils.assertion.assertWidthFit
import com.rokt.core.testutils.assertion.assertWidthWrapContent
import com.rokt.roktux.RoktLayout
import com.rokt.roktux.RoktUxConfig
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class ColumnComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_FitDimension.json")
    fun testColumnComponentWithFitDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertWidthFit()
            .assertHeightFit()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_WrapContentDimension.json")
    fun testColumnComponentWithWrapContentDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHeightWrapContent()
            .assertWidthWrapContent()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_WeightedChildren.json")
    fun testColumnComponentWithWeightedChildren() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("AB").assertHeightIsEqualTo(50.dp)
        composeTestRule.onNodeWithText("CD").assertHeightIsEqualTo(100.dp)
        composeTestRule.onNodeWithText("EF").assertHeightIsEqualTo(50.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_StartAlignment.json")
    fun testColumnComponentWithStartAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_CenterAlignment.json")
    fun testColumnComponentWithCenterAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
        assertNotEquals(childRect.right, parentRect.right)
        assertNotEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_BottomAlignment.json")
    fun testColumnComponentWithBottomAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.top, parentRect.top)
        assertEquals(childRect.right, parentRect.right)
        assertNotEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_StretchAlignment.json")
    fun testColumnComponentWithStretchAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
        val childRect1 = composeTestRule.onNodeWithText("Child1").fetchSemanticsNode().boundsInRoot
        val childRect2 = composeTestRule.onNodeWithText("Child2").fetchSemanticsNode().boundsInRoot
        assertEquals(childRect1.width, childRect2.width)
        assertEquals(200.dp, childRect1.width.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_StretchAlignment_child.json")
    fun testColumnComponentWithStretchAlignmentChild() {
        val childRect1 = composeTestRule.onNodeWithText("Child1").fetchSemanticsNode().boundsInRoot
        val childRect2 = composeTestRule.onNodeWithText("Child2").fetchSemanticsNode().boundsInRoot
        assertEquals(childRect1.width, childRect2.width)
        assertEquals(200.dp, childRect1.width.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_StretchAlignment_LargeChildPadding.json")
    fun testColumnComponentWithStretchAlignmentAndLargeChildPadding() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_StretchAlignment_LargeRowGap.json")
    fun testColumnComponentWithStretchAlignmentAndLargeRowGap() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_StretchAlignment_ProgressIndicator.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2)
    fun testColumnComponentWithStretchAlignmentRendersProgressIndicator() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_StretchAlignment_DataImageCarousel.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_image_carousel_key.json")
    fun testColumnComponentWithStretchAlignmentRendersDataImageCarousel() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_StretchAlignment_DataImageCarousel.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_image_carousel_key.json")
    fun testColumnComponentWithStretchAlignmentKeepsSiblingWidthAboveDataImageCarousel() {
        // Stretch sizing is skipped above a carousel, so the text wraps instead of filling the column.
        val columnWidth = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).getUnclippedBoundsInRoot().width
        val textWidth = composeTestRule.onNodeWithText("Child1").getUnclippedBoundsInRoot().width
        assertTrue("text width $textWidth should be less than column width $columnWidth", textWidth < columnWidth)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/CatalogStackedCollection_with_StretchAlignment_CatalogImageGallery.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_images.json")
    fun testCatalogStackedCollectionWithStretchAlignmentRendersCatalogImageGallery() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
    }

    @Test
    fun testColumnComponentStretchesSiblingAboveOneByOneDistributionWithoutCarousel() {
        renderExperience("EmbeddedStretchSiblingAboveOneByOne.json")
        // No offer layout holds a carousel, so the stretched text fills the column, which is as wide as the offer.
        composeTestRule.onNodeWithContentDescription("Offer 1 of 2").assertWidthIsEqualTo(240.dp)
        composeTestRule.onNodeWithText("Stretched sibling").assertWidthIsEqualTo(240.dp)
    }

    @Test
    fun testColumnComponentRendersStretchSiblingAboveOneByOneDistributionWithCarousel() {
        renderExperience("EmbeddedStretchSiblingAboveOneByOneWithCarousel.json")
        // An offer layout holds a carousel, so stretch skips the intrinsic query the carousel cannot answer.
        composeTestRule.onNodeWithText("Stretched sibling").assertIsDisplayed()
        composeTestRule.onNodeWithText("Offer with a carousel").assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_Padding.json")
    fun testColumnComponentWithPadding() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 20, bottom = 15, start = 25)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_Margin.json")
    fun testColumnComponentWithMargin() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 20, bottom = 15, start = 25)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_Offset.json")
    fun testColumnComponentWithOffset() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertOffsetValues(x = 20, y = 25)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_BackgroundColor.json")
    fun testColumnComponentWithBackgroundColor() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertBackgroundColor("#d51a1a")
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_BackgroundColor.json")
    @DcuiConfig(isDarkModeEnabled = true)
    fun testColumnComponentWithBackgroundColorInDarkMode() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertBackgroundColor("#ababab")
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_JustifyContent_Center.json")
    fun testColumnComponentWithJustifyContentsCenter() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.left, parentRect.left)
        assertNotEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_JustifyContent_Start.json")
    fun testColumnComponentWithJustifyContentsStart() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_JustifyContent_End.json")
    fun testColumnComponentWithJustifyContentsEnd() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.bottomLeft, parentRect.bottomLeft)
        assertNotEquals(childRect.top, parentRect.top)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_JustifyContent_Center.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testColumnComponentWithBreakpointAndJustifyContentsStart() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_AlignItems_Center.json")
    fun testColumnComponentWithAlignItemsCenter() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.left, parentRect.left)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_AlignItems_Start.json")
    fun testColumnComponentWithAlignItemsStart() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, parentRect.topLeft)
        assertNotEquals(childRect.right, parentRect.right)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_AlignItems_End.json")
    fun testColumnComponentWithAlignItemsEnd() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topRight, parentRect.topRight)
        assertNotEquals(childRect.left, parentRect.left)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_AlignItems_Center.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testColumnComponentWithBreakpointAndAlignItemsStart() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, parentRect.topLeft)
        assertNotEquals(childRect.right, parentRect.right)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_MinWidthAndMinHeight.json")
    fun testColumnComponentWithMinWidthAndMinHeight() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertWidthIsEqualTo(10.dp)
            .assertHeightIsEqualTo(5.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_Gap.json")
    fun testRowComponentGapProperty() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
            .assertHeightIsEqualTo(80.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/ScrollableColumn_with_ScrollableColumn.json")
    fun testScrollableColumnComponentRendersNestedScrollableColumn() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithText("Child1").assertIsDisplayed()
        // The child is taller than the window, so the inner column takes the window height, which the code reads
        // from LocalWindowInfo.containerSize. That size comes from the activity's current window metrics.
        val windowHeightPx = (composeTestRule as AndroidComposeTestRule<*, *>).activity
            .windowManager.currentWindowMetrics.bounds.height()
        val innerHeightPx = composeTestRule
            .onNode(hasScrollAction() and hasAnyAncestor(hasTestTag(DCUI_COMPONENT_TAG)))
            .fetchSemanticsNode().size.height
        assertEquals(windowHeightPx, innerHeightPx)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/ScrollableColumn_with_FixedHeightScrollableColumn.json")
    fun testScrollableColumnComponentKeepsChildFixedHeightTallerThanWindow() {
        composeTestRule.onNode(hasScrollAction() and hasAnyAncestor(hasTestTag(DCUI_COMPONENT_TAG)))
            .assertHeightIsEqualTo(1000.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/ScrollableColumn_with_Column_ScrollableColumn.json")
    fun testScrollableColumnComponentRendersScrollableColumnInsideColumn() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithText("Child1").assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_OversizedFixedDimensionChild.json")
    fun testColumnComponentRendersChildWithOversizedFixedDimension() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
        val columnWidth = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).getUnclippedBoundsInRoot().width
        val textWidth = composeTestRule.onNodeWithText("Child1").getUnclippedBoundsInRoot().width
        assertTrue("text width $textWidth should be at most column width $columnWidth", textWidth <= columnWidth)
    }

    @Test
    @Config(qualifiers = "xxxhdpi")
    @DcuiNodeJson(jsonFile = "ColumnComponent/ScrollableColumn_with_ScrollableRow_OversizedFixedDimensionChild.json")
    fun testScrollableColumnComponentRendersChildWithOversizedFixedDimensionInScrollableRow() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/ScrollableColumn_with_OversizedMinHeightChild.json")
    fun testScrollableColumnComponentRendersChildWithOversizedMinHeight() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ColumnComponent/ScrollableColumn_with_OversizedMaxDimensionChild.json")
    fun testScrollableColumnComponentRendersChildWithOversizedMaxDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
    }

    // Renders a full experience through RoktLayout, so the outer layout's slots are mapped and provided.
    private fun renderExperience(fileName: String) {
        val experienceResponse = TestJsonLoader.loadJsonFromAssetsDirectory("Snapshot", fileName)
        val dispatcher = UnconfinedTestDispatcher()
        composeTestRule.setContent {
            RoktLayout(
                experienceResponse = experienceResponse,
                location = "Location1",
                roktUxConfig = RoktUxConfig.builder().build(),
                mainDispatcher = dispatcher,
                ioDispatcher = dispatcher,
            )
        }
    }
}
