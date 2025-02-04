package com.rokt.roktux.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
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
import com.rokt.core.testutils.assertion.assertHeightWrapContent
import com.rokt.core.testutils.assertion.assertWidthWrapContent
import com.rokt.core.testutils.assertion.hasBackgroundColor
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgressIndicatorComponentTest : BaseDcuiEspressoTest() {
    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_with_FixedDimension.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2)
    fun testProgressIndicatorComponentWithFixedDimension() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHeightIsEqualTo(180.dp)
            .assertWidthIsEqualTo(150.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_with_FixedDimension.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2, breakpoints = [DcuiBreakpoint("Landscape", 500)])
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    fun testProgressIndicatorComponentBreakpointWithFixedDimension() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHeightIsEqualTo(180.dp)
            .assertWidthIsEqualTo(200.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_with_WrapContentDimension.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2)
    fun testProgressIndicatorComponentWithFitDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertWidthWrapContent()
            .assertHeightWrapContent()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_with_WrapContentDimension.json")
    @DcuiConfig(breakpointIndex = 1)
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2)
    fun testProgressIndicatorComponentBreakpointWithFitDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertWidthWrapContent()
            .assertHeightWrapContent()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_with_JustifyContentStart.json")
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 2)
    fun testProgressIndicatorComponentWithJustifyContentStart() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0].fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_with_JustifyContentCenter.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2)
    fun testProgressIndicatorComponentWithJustifyContentCenter() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0].fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
        assertNotEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_with_JustifyContentEnd.json")
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 2)
    fun testProgressIndicatorComponentWithJustifyContentEnd() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#000000", 10.dp))[0].fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topRight, parentRect.topRight)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_with_breakpoint_JustifyContent.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2, breakpoints = [DcuiBreakpoint("Landscape", 500)])
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    fun testProgressIndicatorComponentBreakpointWithJustifyContent() {
        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0].fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
        assertNotEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_active_inactive.json")
    @DcuiNodeComponentState(currentOffer = 2, totalOffer = 3)
    fun testProgressIndicatorActiveInActiveNodesState() {
        val zeroIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#000000", 10.dp))[0]
        val firstIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0]
        val secondIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#000000", 10.dp))[1]

        zeroIndexIndicator.assertIsDisplayed()
        firstIndexIndicator.assertIsDisplayed()
        secondIndexIndicator.assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_active_inactive.json")
    @DcuiNodeComponentState(currentOffer = 2, totalOffer = 3, breakpoints = [DcuiBreakpoint("Landscape", 500)])
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    fun testProgressIndicatorActiveInActiveNodesStateWithBreakpoint() {
        val zeroIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#000000", 10.dp))[0]
        val firstIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#ababab", 10.dp))[0]
        val secondIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#000000", 10.dp))[1]

        zeroIndexIndicator.assertIsDisplayed()
        firstIndexIndicator.assertIsDisplayed()
        secondIndexIndicator.assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_startPosition2_active_inactive.json")
    @DcuiNodeComponentState(currentOffer = 2, totalOffer = 3)
    fun testProgressIndicatorWithStartPosition2ActiveInActiveNodesState() {
        val zeroIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#000000", 10.dp))[0]
        val firstIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0]
        val secondIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#000000", 10.dp))[1]

        zeroIndexIndicator.assertIsDisplayed()
        firstIndexIndicator.assertIsDisplayed()
        secondIndexIndicator.assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_seen_unseen.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 3)
    fun testProgressIndicatorSeenUnseenNodesState() {
        val zeroIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0]
        val firstIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[1]
        val secondIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#000000", 10.dp))[0]

        zeroIndexIndicator.assertIsDisplayed()
        firstIndexIndicator.assertIsDisplayed()
        secondIndexIndicator.assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_seen_unseen.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 3, breakpoints = [DcuiBreakpoint("Landscape", 500)])
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    fun testProgressIndicatorSeenUnseenNodesStateWithBreakpoint() {
        val zeroIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#ababab", 10.dp))[0]
        val firstIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#ababab", 10.dp))[1]
        val secondIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#000000", 10.dp))[0]

        zeroIndexIndicator.assertIsDisplayed()
        firstIndexIndicator.assertIsDisplayed()
        secondIndexIndicator.assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_startPosition2_seen_unseen.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 3)
    fun testProgressIndicatorWithStartPosition2SeenUnseenNodesState() {
        val zeroIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0]
        val firstIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[1]
        val secondIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#000000", 10.dp))[0]

        zeroIndexIndicator.assertIsDisplayed()
        firstIndexIndicator.assertDoesNotExist()
        secondIndexIndicator.assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_active_seen.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 3)
    fun testProgressIndicatorActiveAndSeenNodesState() {
        val zeroIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0]
        val firstIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#ababab", 10.dp))[0]
        val secondIndexIndicator = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#000000", 10.dp))[0]

        zeroIndexIndicator.assertIsDisplayed()
        firstIndexIndicator.assertIsDisplayed()
        secondIndexIndicator.assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_with_breakpoint_Indicator_Dimension.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2, breakpoints = [DcuiBreakpoint("Landscape", 500)])
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    fun testIndicatorComponentWithBreakpointDimension() {
        val activeIndicatorItem = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0]
        val indicatorItem = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#000000", 10.dp))[0]
        activeIndicatorItem.assertWidthIsEqualTo(15.dp).assertHeightIsEqualTo(10.dp)
        indicatorItem.assertWidthIsEqualTo(10.dp).assertHeightIsEqualTo(20.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 3)
    fun testProgressIndicatorTextValues() {
        composeTestRule.onNodeWithText("1", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("2", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("3", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_randomText.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 3)
    fun testProgressIndicatorRandomTextValues() {
        composeTestRule.onAllNodesWithText("Random", useUnmergedTree = true)[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Random", useUnmergedTree = true)[1].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Random", useUnmergedTree = true)[2].assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/Indicator_Item_with_JustifyContentStart_AlignStart.json")
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 2)
    fun testIndicatorComponentWithJustifyContentStartAndAlignStart() {
        val indicatorItem = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0].fetchSemanticsNode().boundsInRoot
        val indicatorItemText = composeTestRule.onNodeWithText(
            "1",
            useUnmergedTree = true,
        ).fetchSemanticsNode().boundsInRoot
        assertEquals(indicatorItem.topLeft, indicatorItemText.topLeft)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/Indicator_Item_with_JustifyContentStart_AlignCenter.json")
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 2)
    fun testIndicatorComponentWithJustifyContentStartAndAlignCenter() {
        val indicatorItem = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0].fetchSemanticsNode().boundsInRoot
        val indicatorItemText =
            composeTestRule.onNodeWithText("1", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(indicatorItem.left, indicatorItemText.left)
        assertNotEquals(indicatorItem.top, indicatorItemText.top)
        assertNotEquals(indicatorItem.bottom, indicatorItemText.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/Indicator_Item_with_JustifyContentStart_AlignEnd.json")
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 2)
    fun testIndicatorComponentWithJustifyContentStartAndAlignEnd() {
        val indicatorItem = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0].fetchSemanticsNode().boundsInRoot
        val indicatorItemText =
            composeTestRule.onNodeWithText("1", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(indicatorItem.left, indicatorItemText.left)
        assertEquals(indicatorItem.bottom, indicatorItemText.bottom)
        assertNotEquals(indicatorItem.top, indicatorItemText.top)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/Indicator_Item_with_Breakpoint_Alignment.json")
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 2, breakpoints = [DcuiBreakpoint("Landscape", 500)])
    @DcuiConfig(windowSize = WindowSize(600, 500))
    fun testIndicatorComponentWithBreakpointJustifyContentStartAndAlignEnd() {
        val indicatorItem = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()
            .filter(hasBackgroundColor("#d51a1a", 10.dp))[0].fetchSemanticsNode().boundsInRoot
        val indicatorItemText =
            composeTestRule.onNodeWithText("1", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(indicatorItem.left, indicatorItemText.left)
        assertNotEquals(indicatorItem.top, indicatorItemText.top)
        assertNotEquals(indicatorItem.bottom, indicatorItemText.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_startPosition2.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 3)
    fun testProgressIndicatorStartPosition2WithCurrentOffer0TextValues() {
        composeTestRule.onNodeWithText("1").assertDoesNotExist()
        composeTestRule.onNodeWithText("2").assertDoesNotExist()
        composeTestRule.onNodeWithText("3").assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator_startPosition2.json")
    @DcuiNodeComponentState(currentOffer = 2, totalOffer = 3)
    fun testProgressIndicatorStartPosition2TextValues() {
        composeTestRule.onNodeWithText("1", useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("2", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("3", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 5, viewableItems = [2])
    fun testProgressIndicatorTextValuesWithOddTotalOffersAndTwoViewableItems() {
        composeTestRule.onNodeWithText("1", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("2", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("3", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressIndicatorComponent/ProgressIndicator.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 6, viewableItems = [2])
    fun testProgressIndicatorTextValuesWithEvenTotalOffersAndTwoViewableItems() {
        composeTestRule.onNodeWithText("1", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("2", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("3", useUnmergedTree = true).assertIsDisplayed()
    }
}
