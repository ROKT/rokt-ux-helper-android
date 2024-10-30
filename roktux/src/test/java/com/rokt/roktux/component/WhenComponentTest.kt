package com.rokt.roktux.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.core.testutils.annotations.DcuiBreakpoint
import com.core.testutils.annotations.DcuiConfig
import com.core.testutils.annotations.DcuiCreativeCopy
import com.core.testutils.annotations.DcuiNodeComponentState
import com.core.testutils.annotations.DcuiNodeJson
import com.core.testutils.annotations.DcuiOfferJson
import com.core.testutils.annotations.WindowSize
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhenComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Breakpoint_IsCondition.json")
    @DcuiConfig(windowSize = WindowSize(500, 500))
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testWhenNodeBreakpointIsConditionSuccess() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Breakpoint_IsCondition.json")
    @DcuiConfig(windowSize = WindowSize(400, 500))
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testWhenNodeBreakpointIsConditionFail() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Breakpoint_IsNotCondition.json")
    @DcuiConfig(windowSize = WindowSize(400, 500))
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testWhenNodeBreakpointIsNotConditionSuccess() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Breakpoint_IsNotCondition.json")
    @DcuiConfig(windowSize = WindowSize(500, 500))
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testWhenNodeBreakpointIsNotConditionFail() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Breakpoint_IsBelowCondition.json")
    @DcuiConfig(windowSize = WindowSize(499, 500))
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testWhenNodeBreakpointIsBelowConditionSuccess() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Breakpoint_IsBelowCondition.json")
    @DcuiConfig(windowSize = WindowSize(501, 500))
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testWhenNodeBreakpointIsBelowConditionFail() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Breakpoint_IsAboveCondition.json")
    @DcuiConfig(windowSize = WindowSize(601, 500))
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500), DcuiBreakpoint("Desktop", 600)])
    fun testWhenNodeBreakpointIsAboveConditionSuccess() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Breakpoint_IsAboveCondition.json")
    @DcuiConfig(windowSize = WindowSize(499, 500))
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testWhenNodeBreakpointIsAboveConditionFail() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Progression_IsAboveCondition.json")
    @DcuiNodeComponentState(currentOffer = 2, totalOffer = 2)
    fun testWhenNodeProgressionIsAboveConditionSuccess() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Progression_IsAboveCondition.json")
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 2)
    fun testWhenNodeProgressionIsAboveConditionFail() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Progression_IsCondition.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2)
    fun testWhenNodeProgressionIsConditionSuccess() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Progression_IsCondition.json")
    @DcuiNodeComponentState(currentOffer = 3, totalOffer = 2)
    fun testWhenNodeProgressionIsConditionFail() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Progression_IsCondition_NegativeNumber.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2)
    fun testWhenNodeProgressionIsConditionWithNegativeNumberSuccess() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Progression_IsCondition_NegativeNumber.json")
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 2)
    fun testWhenNodeProgressionIsConditionWithNegativeNumberFail() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Position_IsAboveCondition.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    @DcuiNodeComponentState(currentOffer = 2, totalOffer = 2)
    fun testWhenNodePositionIsAboveConditionInnerLayoutSuccess() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Position_IsAboveCondition.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 2)
    fun testWhenNodePositionIsAboveConditionInnerLayoutFail() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Position_IsCondition_NegativeNumber.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2)
    fun testWhenNodePositionIsConditionWithNegativeNumberInnerLayoutSuccess() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Position_IsCondition_NegativeNumber.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 2)
    fun testWhenNodePositionIsConditionWithNegativeNumberInnerLayoutFail() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_DarkMode_IsConditionWithTrueValue.json")
    @DcuiConfig(isDarkModeEnabled = true)
    fun testWhenNodeWithEnabledDarkModeAndIsConditionWithTrueValue() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_DarkMode_IsConditionWithTrueValue.json")
    @DcuiConfig(isDarkModeEnabled = false)
    fun testWhenNodeWithDisabledDarkModeAndIsConditionWithTrueValue() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_DarkMode_IsConditionWithFalseValue.json")
    @DcuiConfig(isDarkModeEnabled = true)
    fun testWhenNodeWithEnabledDarkModeAndIsConditionWithFalseValue() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_DarkMode_IsConditionWithFalseValue.json")
    @DcuiConfig(isDarkModeEnabled = false)
    fun testWhenNodeWithDisabledDarkModeAndIsConditionWithFalseValue() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_DarkMode_IsNotConditionWithTrueValue.json")
    @DcuiConfig(isDarkModeEnabled = false)
    fun testWhenNodeWithDisabledDarkModeAndIsNotConditionWithTrueValue() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_DarkMode_IsNotConditionWithTrueValue.json")
    @DcuiConfig(isDarkModeEnabled = true)
    fun testWhenNodeWithEnabledDarkModeAndIsNotConditionWithTrueValue() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_DarkMode_IsNotConditionWithFalseValue.json")
    @DcuiConfig(isDarkModeEnabled = false)
    fun testWhenNodeWithDisabledDarkModeAndIsNotConditionWithFalseValue() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_DarkMode_IsNotConditionWithFalseValue.json")
    @DcuiConfig(isDarkModeEnabled = true)
    fun testWhenNodeWithEnabledDarkModeAndIsNotConditionWithFalseValue() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_Enter_Animation_And_DarkMode_IsCondition.json")
    @DcuiConfig(isDarkModeEnabled = true)
    fun testWhenNodeWithEnterAnimation() {
        composeTestRule.mainClock.autoAdvance = false
        // Test node is present at the start
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_CreativeCopy_ExistsCondition.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    @DcuiNodeComponentState(creativeCopy = [DcuiCreativeCopy(key = "creative.test", value = "test")])
    fun testWhenNodeWithExistingCreativeCopyAndExistsCondition() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_CreativeCopy_ExistsCondition.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    @DcuiNodeComponentState(creativeCopy = [DcuiCreativeCopy(key = "something", value = "test")])
    fun testWhenNodeWithNonExistingCreativeCopyAndExistsCondition() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_CreativeCopy_NotExistsCondition.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    @DcuiNodeComponentState(creativeCopy = [DcuiCreativeCopy(key = "creative.test", value = "test")])
    fun testWhenNodeWithExistingCreativeCopyAndNotExistsCondition() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_CreativeCopy_NotExistsCondition.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    @DcuiNodeComponentState(creativeCopy = [DcuiCreativeCopy(key = "something", value = "test")])
    fun testWhenNodeWithNonExistingCreativeCopyAndNotExistsCondition() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_StaticBooleanTrue_And_Transition.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testWhenNodeWithStaticBooleanTrueAndTransition() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "WhenComponent/When_with_StaticBooleanFalse_And_Transition.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testWhenNodeWithStaticBooleanFalseAndTransition() {
        composeTestRule.onNodeWithText("Test Offer")
            .assertDoesNotExist()
    }
}
