package com.rokt.roktux.component

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.core.testutils.annotations.DcuiBreakpoint
import com.core.testutils.annotations.DcuiBreakpoints
import com.core.testutils.annotations.DcuiConfig
import com.core.testutils.annotations.DcuiNodeJson
import com.core.testutils.annotations.DcuiOfferJson
import com.core.testutils.annotations.WindowSize
import com.core.testutils.assertion.assertHeightFit
import com.core.testutils.assertion.assertWidthFit
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Assert.assertNotEquals
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreativeResponseComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "CreativeResponseComponent/ResponseButton_with_Children.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testResponseButtonClickable() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }

    @Test
    @DcuiNodeJson(jsonFile = "CreativeResponseComponent/ResponseButton_with_Children.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testResponseButtonWithChildren() {
        val component = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .assertIsDisplayed()

        component.onChildAt(0).assertTextEquals("Test1")
        component.onChildAt(1).assertTextEquals("Test2")
        component.onChildAt(2).assertTextEquals("Test3")
    }

    @Test
    @DcuiNodeJson(jsonFile = "CreativeResponseComponent/ResponseButton_with_FixedDimension.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testResponseButtonWithFixedDimension() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHeightIsEqualTo(180.dp)
            .assertWidthIsEqualTo(150.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "CreativeResponseComponent/ResponseButton_with_FitDimension.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testResponseButtonWithFitDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertWidthFit()
            .assertHeightFit()
    }

    @Test
    @DcuiNodeJson(jsonFile = "CreativeResponseComponent/ResponseButton_with_Children_AlignSelf.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testResponseButtonWithChildrenAlignSelf() {
        val parentRect =
            composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot

        assertNotEquals(childRect.left, parentRect.left)
        assertNotEquals(childRect.right, parentRect.right)
        assertNotEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "CreativeResponseComponent/ResponseButton_with_Children.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_invalid_key.json")
    fun testResponseButtonWithInvalidKey() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertDoesNotExist()
    }

    @Test
    @Ignore("Until when node is implemented")
    @DcuiNodeJson(jsonFile = "CreativeResponseComponent/ResponseButton_with_When.json")
    @DcuiConfig(testInInnerLayout = true, windowSize = WindowSize(400, 500))
    @DcuiBreakpoints([DcuiBreakpoint("Landscape", 500)])
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testResponseButtonWithWhenNodeBelowLandscape() {
        composeTestRule.onNodeWithText("This is below landscape")
            .assertIsDisplayed()
    }

    @Test
    @Ignore("Until when node is implemented")
    @DcuiNodeJson(jsonFile = "CreativeResponseComponent/ResponseButton_with_When.json")
    @DcuiConfig(testInInnerLayout = true, windowSize = WindowSize(501, 500))
    @DcuiBreakpoints([DcuiBreakpoint("Landscape", 500)])
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testResponseButtonWithWhenNodeAboveLandscape() {
        composeTestRule.onNodeWithText("This is above landscape")
            .assertIsDisplayed()
    }
}
