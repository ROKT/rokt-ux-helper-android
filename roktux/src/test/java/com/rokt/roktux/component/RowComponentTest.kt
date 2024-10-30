package com.rokt.roktux.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.core.testutils.annotations.DcuiBreakpoint
import com.core.testutils.annotations.DcuiConfig
import com.core.testutils.annotations.DcuiNodeComponentState
import com.core.testutils.annotations.DcuiNodeJson
import com.core.testutils.annotations.DcuiOfferJson
import com.core.testutils.annotations.TestPseudoState
import com.core.testutils.annotations.WindowSize
import com.core.testutils.assertion.assertBackgroundColor
import com.core.testutils.assertion.assertHeightFit
import com.core.testutils.assertion.assertHeightWrapContent
import com.core.testutils.assertion.assertOffsetValues
import com.core.testutils.assertion.assertPaddingValues
import com.core.testutils.assertion.assertWidthFit
import com.core.testutils.assertion.assertWidthWrapContent
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RowComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_FixedDimension.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testRowComponentWithFixedDimension() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertHeightIsEqualTo(180.dp)
            .assertWidthIsEqualTo(150.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_FitDimension.json")
    fun testRowComponentWithFitDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertWidthFit().assertHeightFit()
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_WrapContentDimension.json")
    fun testRowComponentWithWrapContentDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertHeightWrapContent()
            .assertWidthWrapContent()
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_MinDimension.json")
    fun testRowComponentWithMinDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithText("A").assertWidthIsEqualTo(75.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_WeightedChildren.json")
    fun testRowComponentWithWeightedChildren() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        composeTestRule.onNodeWithText("AB").assertWidthIsEqualTo(50.dp)
        composeTestRule.onNodeWithText("CD").assertWidthIsEqualTo(100.dp)
        composeTestRule.onNodeWithText("EF").assertWidthIsEqualTo(50.dp)
    }

    @Test
    @DcuiNodeJson(
        jsonString = """
        {
          "type": "Row",
          "node": {
            "styles": {
              "elements": {
                "own": [
                  {
                    "default": {

                    }
                  }
                ]
              }
            }, "children": [

            ]
          }
        }
        """,
    )
    fun testRowComponentDefaultWidthIsFillMaxWidth() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertWidthFit()
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_TopAlignment.json")
    fun testRowComponentWithTopAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_CenterAlignment.json")
    fun testRowComponentWithCenterAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertNotEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
        assertEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_BottomAlignment.json")
    fun testRowComponentWithBottomAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertNotEquals(childRect.top, parentRect.top)
        assertEquals(childRect.bottom, parentRect.bottom)
        assertEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_StretchAlignment.json")
    fun testRowComponentWithStretchAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
        val childRect1 = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot
        val childRect2 = composeTestRule.onNodeWithText("Long Offer with multiple test in it to verify the height")
            .fetchSemanticsNode().boundsInRoot
        assertEquals(childRect1.height, childRect2.height)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_StretchAlignment_child.json")
    fun testRowComponentWithStretchAlignmentChild() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
        val childRect1 = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot
        val childRect2 = composeTestRule.onNodeWithText("Long Offer with multiple test in it to verify the height")
            .fetchSemanticsNode().boundsInRoot
        assertEquals(childRect1.height, childRect2.height)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_Padding.json")
    fun testRowComponentWithPadding() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 20, bottom = 15, start = 25)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_Single_Dimension_Padding.json")
    fun testRowComponentWithSingleDimensionPadding() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 10, bottom = 10, start = 10)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_Two_Dimension_Padding.json")
    fun testRowComponentWithTwoDimensionPadding() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 20, bottom = 10, start = 20)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_Three_Dimension_Padding.json")
    fun testRowComponentWithThreeDimensionPadding() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 20, bottom = 30, start = 20)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_Margin.json")
    fun testRowComponentWithMargin() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 20, bottom = 15, start = 25)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_Single_Dimension_Margin.json")
    fun testRowComponentWithSingleDimensionMargin() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 10, bottom = 10, start = 10)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_Two_Dimension_Margin.json")
    fun testRowComponentWithTwoDimensionMargin() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 20, bottom = 10, start = 20)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_Three_Dimension_Margin.json")
    fun testRowComponentWithThreeDimensionMargin() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 20, bottom = 30, start = 20)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_Offset.json")
    fun testRowComponentWithOffset() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertOffsetValues(x = 20, y = 25)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_BackgroundColor.json")
    fun testRowComponentWithBackgroundColor() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertBackgroundColor("#d51a1a")
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_BackgroundColor.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testRowComponentWithBackgroundColorInDarkMode() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertBackgroundColor("#ababad")
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_JustifyContent_Center.json")
    fun testRowComponentWithJustifyContentsCenter() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
        assertNotEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_JustifyContent_Start.json")
    fun testRowComponentWithJustifyContentsStart() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_JustifyContent_End.json")
    fun testRowComponentWithJustifyContentsEnd() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topRight, parentRect.topRight)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_AlignItems_Center.json")
    fun testRowComponentWithAlignItemsCenter() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.left, parentRect.left)
        assertNotEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_AlignItems_Start.json")
    fun testRowComponentWithAlignItemsStart() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, parentRect.topLeft)
        assertNotEquals(childRect.right, parentRect.right)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_AlignItems_End.json")
    fun testRowComponentWithAlignItemsEnd() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.bottomLeft, parentRect.bottomLeft)
        assertNotEquals(childRect.right, parentRect.right)
        assertNotEquals(childRect.top, parentRect.top)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_FixedDimension.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testRowComponentBreakpointWithFixedDimension() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertHeightIsEqualTo(180.dp)
            .assertWidthIsEqualTo(200.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_FitDimension.json")
    @DcuiConfig(breakpointIndex = 1)
    fun testRowComponentBreakpointWithFitDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertWidthFit().assertHeightFit()
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_WrapContentDimension.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testRowComponentBreakpointWithDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertHeightFit()
            .assertWidthWrapContent()
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_different_breakpoint_alignment.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testRowComponentBreakpointWithDifferentAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertNotEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
        assertEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_different_JustifyContent_Breakpoint.json")
    @DcuiConfig(windowSize = WindowSize(800, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500), DcuiBreakpoint("Desktop", 800)])
    fun testRowComponentBreakpointWithDifferentJustifyContents() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
        assertNotEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_breakpoint_AlignItems_Center.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testRowComponentBreakpointWithAlignItemsCenter() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.left, parentRect.left)
        assertNotEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_Breakpoint_Padding.json")
    @DcuiConfig(windowSize = WindowSize(800, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500), DcuiBreakpoint("Desktop", 800)])
    fun testRowComponentBreakpointWithPadding() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 2, end = 4, bottom = 8, start = 16)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_Breakpoint_Margin.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testRowComponentBreakpointWithMargin() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 2, end = 4, bottom = 8, start = 16)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_BackgroundColor.json")
    @DcuiConfig(windowSize = WindowSize(500, 500))
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testRowComponentBreakpointWithBackgroundColor() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertBackgroundColor("#ababab")
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_CenterAlignment.json")
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
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_JustifyContent_Center.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testRowComponentBreakpointWithJustifyContentsStart() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test Offer").fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_Gap.json")
    fun testRowComponentGapProperty() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
            .assertWidthIsEqualTo(80.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_PressedState.json")
    @DcuiConfig(pseudoState = TestPseudoState())
    fun testRowBackgroundColorWithStateDefault() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertBackgroundColor("#ff0000")
    }

    @Test
    @DcuiNodeJson(jsonFile = "RowComponent/Row_with_PressedState.json")
    @DcuiConfig(pseudoState = TestPseudoState(isPressed = true))
    fun testRowBackgroundColorWithStatePressed() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertBackgroundColor("#00ff00")
    }
}
