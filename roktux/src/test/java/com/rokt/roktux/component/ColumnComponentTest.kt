package com.rokt.roktux.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiBreakpoint
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeComponentState
import com.rokt.core.testutils.annotations.DcuiNodeJson
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
    @DcuiNodeJson(jsonFile = "ColumnComponent/Column_with_Opacity.json")
    fun testColumnComponentWithOpacity() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertAlpha(0.7f)
    }
}
