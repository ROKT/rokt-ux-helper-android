package com.rokt.roktux.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.core.testutils.assertion.assertBackgroundColor
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataImageCarouselComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "DataImageCarouselComponent/DataImageCarousel_Basic_Properties.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_image_carousel_key.json")
    fun testDataImageCarouselComponentBasicProperties() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertHeightIsEqualTo(180.dp)
            .assertWidthIsEqualTo(150.dp).assertBackgroundColor("#d51a1a")
    }

    @Test
    @DcuiNodeJson(jsonFile = "DataImageCarouselComponent/DataImageCarousel_Default_Alignment.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_image_carousel_key.json")
    fun testDataImageCarouselComponentWithTopStartAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .onChildren()[1].fetchSemanticsNode().boundsInRoot

        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "DataImageCarouselComponent/DataImageCarousel_Center_Start_Alignment.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_image_carousel_key.json")
    fun testDataImageCarouselComponentWithCenterStartAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = false)
            .onChildren()[1].fetchSemanticsNode().boundsInRoot
        assertEquals(childRect.top, parentRect.top)
        assertNotEquals(childRect.bottom, parentRect.bottom)
        assertEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "DataImageCarouselComponent/DataImageCarousel_Center_Bottom_Alignment.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_image_carousel_key.json")
    fun testDataImageCarouselComponentWithCenterBottomAlignment() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = false)
            .onChildren()[1].fetchSemanticsNode().boundsInRoot
        assertNotEquals(childRect.top, parentRect.top)
        assertEquals(childRect.bottom, parentRect.bottom)
        assertEquals(childRect.left, parentRect.left)
    }

    @Test
    @DcuiNodeJson(jsonFile = "DataImageCarouselComponent/DataImageCarousel_Indicator_Top_Alignment.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_image_carousel_key.json")
    fun testDataImageCarouselIndicatorSelfAlignmentTop() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = false)
            .onChildren()[1].fetchSemanticsNode().boundsInRoot
        assertEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "DataImageCarouselComponent/DataImageCarousel_Indicator_Center_Alignment.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_image_carousel_key.json")
    fun testDataImageCarouselIndicatorSelfAlignmentCenter() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = false)
            .onChildren()[1].fetchSemanticsNode().boundsInRoot
        assertNotEquals(childRect.topLeft, Offset.Zero)
        assertNotEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "DataImageCarouselComponent/DataImageCarousel_Indicator_End_Alignment.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_image_carousel_key.json")
    fun testDataImageCarouselIndicatorSelfAlignmentBottom() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()

        val parentRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot
        val childRect = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = false)
            .onChildren()[1].fetchSemanticsNode().boundsInRoot
        assertNotEquals(childRect.topLeft, Offset.Zero)
        assertEquals(childRect.bottom, parentRect.bottom)
    }

    @Test
    @DcuiNodeJson(jsonFile = "DataImageCarouselComponent/DataImageCarousel_Fallback_ImageKey.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_image_carousel_key.json")
    fun testDataImageCarouselComponentWithFallbackImageKey() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed().assertHeightIsEqualTo(180.dp)
            .assertWidthIsEqualTo(150.dp).assertBackgroundColor("#d51a1a")
    }
}
