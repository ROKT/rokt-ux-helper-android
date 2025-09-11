package com.rokt.roktux.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiBreakpoint
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeComponentState
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.core.testutils.annotations.WindowSize
import com.rokt.core.testutils.assertion.assertAlpha
import com.rokt.core.testutils.assertion.assertBackgroundColor
import com.rokt.core.testutils.assertion.assertHeightFit
import com.rokt.core.testutils.assertion.assertHeightWrapContent
import com.rokt.core.testutils.assertion.assertOffsetValues
import com.rokt.core.testutils.assertion.assertPaddingValues
import com.rokt.core.testutils.assertion.assertWidthFit
import com.rokt.core.testutils.assertion.assertWidthWrapContent
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BoxComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_FixedDimension.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testBoxComponentWithFixedDimension() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertHeightIsEqualTo(180.dp)
            .assertWidthIsEqualTo(150.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_FitDimension.json")
    fun testBoxComponentWithFitDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertWidthFit().assertHeightFit()
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_WrapContentDimension.json")
    fun testBoxComponentWithWrapContentDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertHeightWrapContent()
            .assertWidthWrapContent()
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_MinDimension.json")
    fun testBoxComponentWithMinDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).onChildAt(0).assertIsDisplayed().assertWidthIsEqualTo(75.dp)
            .assertHeightIsEqualTo(75.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_TopStartAlignment.json")
    fun testBoxComponentWithTopStartAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_CenterStartAlignment.json")
    fun testBoxComponentWithCenterStartAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertNotEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
        assertEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_BottomStartAlignment.json")
    fun testBoxComponentWithBottomStartAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertNotEquals(childRect.top, parentRect.top)
        assertEquals(childRect.bottom, parentRect.bottom)
        assertEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_Padding.json")
    fun testBoxComponentWithPadding() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 20, bottom = 15, start = 25)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_Margin.json")
    fun testBoxComponentWithMargin() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 20, bottom = 15, start = 25)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_Offset.json")
    fun testBoxComponentWithOffset() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertOffsetValues(x = 20, y = 25)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_BackgroundColor.json")
    fun testBoxComponentWithBackgroundColor() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertBackgroundColor("#d51a1a")
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_JustifyContent_Center.json")
    fun testBoxComponentWithJustifyContentsCenter() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
        assertNotEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_JustifyContent_Start.json")
    fun testRowComponentWithJustifyContentsStart() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_JustifyContent_End.json")
    fun testRowComponentWithJustifyContentsEnd() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topRight, parentRect.topRight)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_AlignItems_Center.json")
    fun testBoxComponentWithAlignItemsCenter() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.left, parentRect.left)
        assertNotEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_AlignItems_Start.json")
    fun testBoxComponentWithAlignItemsStart() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, parentRect.topLeft)
        assertNotEquals(childRect.right, parentRect.right)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_AlignItems_End.json")
    fun testBoxComponentWithAlignItemsEnd() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.bottomLeft, parentRect.bottomLeft)
        assertNotEquals(childRect.right, parentRect.right)
        assertNotEquals(childRect.top, parentRect.top)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_FixedDimension.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testBoxComponentBreakpointWithFixedDimension() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertHeightIsEqualTo(180.dp)
            .assertWidthIsEqualTo(200.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_FitDimension.json")
    @DcuiConfig(breakpointIndex = 1)
    fun testBoxComponentBreakpointWithFitDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertWidthFit().assertHeightFit()
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_WrapContentDimension.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testBoxComponentBreakpointWithDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertHeightFit()
            .assertWidthWrapContent()
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_different_breakpoint_alignment.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testRowComponentBreakpointWithDifferentAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertNotEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
        assertNotEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_different_JustifyContent_Breakpoint.json")
    @DcuiConfig(windowSize = WindowSize(800, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500), DcuiBreakpoint("Desktop", 800)])
    fun testBoxComponentBreakpointWithDifferentJustifyContents() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
        assertNotEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_breakpoint_AlignItems_Center.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testBoxComponentBreakpointWithAlignItemsCenter() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.left, parentRect.left)
        assertNotEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_BackgroundColor.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testBoxComponentBreakpointWithBackgroundColor() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertBackgroundColor("#ababab")
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_CenterAlignment.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testRowComponentBreakpointWithTopAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_JustifyContent_Center.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testBoxComponentBreakpointWithJustifyContentsStart() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "BoxComponent/Box_with_Opacity.json")
    fun testBoxComponentWithOpacity() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertAlpha(0.4f)
    }
}
