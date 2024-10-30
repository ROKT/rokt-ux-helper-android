package com.rokt.roktux.component

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.core.testutils.annotations.DcuiConfig
import com.core.testutils.annotations.DcuiNodeJson
import com.core.testutils.annotations.DcuiOfferJson
import com.core.testutils.assertion.assertBackgroundColor
import com.core.testutils.assertion.assertHeightFit
import com.core.testutils.assertion.assertPaddingValues
import com.core.testutils.assertion.assertWidthFit
import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.viewmodel.layout.LayoutContract
import org.junit.Assert
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StaticLinkTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "StaticLinkComponent/StaticLink_with_Children.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testStaticLinkClickable() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performTouchInput { click(position = centerLeft) }

        Assert.assertTrue(
            getCapturedEvents().stream()
                .anyMatch { e -> e.equals(LayoutContract.LayoutEvent.UrlSelected("someUrl", OpenLinks.Internally)) },
        )
    }

    @Test
    @DcuiNodeJson(jsonFile = "StaticLinkComponent/StaticLink_with_Children.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testStaticLinkWithChildren() {
        val component = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .assertIsDisplayed()

        component.onChildAt(0).assertTextEquals("Test1")
        component.onChildAt(1).assertTextEquals("Test2")
        component.onChildAt(2).assertTextEquals("Test3")
    }

    @Test
    @DcuiNodeJson(jsonFile = "StaticLinkComponent/StaticLink_with_FixedDimension.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testStaticLinkWithFixedDimension() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHeightIsEqualTo(180.dp)
            .assertWidthIsEqualTo(150.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "StaticLinkComponent/StaticLink_with_FitDimension.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testStaticLinkWithFitDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertWidthFit()
            .assertHeightFit()
    }

    @Test
    @DcuiNodeJson(jsonFile = "StaticLinkComponent/StaticLink_with_Children_AlignSelf.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testStaticLinkWithChildrenAlignSelf() {
        val parentRect =
            composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithText("Test", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot

        assertNotEquals(childRect.left, parentRect.left)
        assertNotEquals(childRect.right, parentRect.right)
        assertNotEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "StaticLinkComponent/StaticLink_with_Style_Properties.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testStaticLinkProperties() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertBackgroundColor("#d51a1a")
            .assertPaddingValues(top = 10, end = 20, bottom = 10, start = 20)
            .assertHeightIsEqualTo(200.dp)
    }
}
