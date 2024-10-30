package com.rokt.roktux.component

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.core.testutils.annotations.DcuiBreakpoint
import com.core.testutils.annotations.DcuiConfig
import com.core.testutils.annotations.DcuiNodeComponentState
import com.core.testutils.annotations.DcuiNodeJson
import com.core.testutils.annotations.TestPseudoState
import com.core.testutils.annotations.WindowSize
import com.core.testutils.assertion.assertBackgroundColor
import com.core.testutils.assertion.assertBaselineTextAlign
import com.core.testutils.assertion.assertFontSize
import com.core.testutils.assertion.assertFontStyle
import com.core.testutils.assertion.assertFontWeight
import com.core.testutils.assertion.assertHorizontalTextAlign
import com.core.testutils.assertion.assertLetterSpacing
import com.core.testutils.assertion.assertLineHeight
import com.core.testutils.assertion.assertLinkBaselineTextAlign
import com.core.testutils.assertion.assertLinkFontSize
import com.core.testutils.assertion.assertLinkFontStyle
import com.core.testutils.assertion.assertLinkFontWeight
import com.core.testutils.assertion.assertLinkLetterSpacing
import com.core.testutils.assertion.assertLinkTextColor
import com.core.testutils.assertion.assertLinkTextDecoration
import com.core.testutils.assertion.assertPaddingValues
import com.core.testutils.assertion.assertTextColor
import com.core.testutils.assertion.assertTextDecoration
import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.viewmodel.layout.LayoutContract
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TextComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(
        jsonString = """
            {
              "type": "BasicText",
              "node": {
                "value": "Your <u>purchase</u> has unlocked a free trial with Apple TV+."
              }
            }
    """,
    )
    @DcuiConfig(componentTag = "TestTag")
    fun testBasicTextComponentRendering() {
        composeTestRule.onNodeWithTag("TestTag")
            .assertIsDisplayed()
            .assertTextEquals("Your <u>purchase</u> has unlocked a free trial with Apple TV+.")
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/BasicText_with_Padding.json")
    fun testBasicTextWithPadding() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .assertPaddingValues(top = 10, end = 20, bottom = 15, start = 25)
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/BasicText_with_BackgroundColor.json")
    fun testBasicTextComponentWithBackgroundColor() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertBackgroundColor("#d51a1a")
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/BasicText_with_FixedDimension.json")
    fun testBasicTextComponentWithFixedDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertTextEquals("ORDER NUMBER: UK171359906")
            .assertHeightIsEqualTo(100.dp)
            .assertWidthIsEqualTo(200.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/BasicText_with_FixedDimension.json")
    @DcuiConfig(windowSize = WindowSize(500, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testBasicTextComponentBreakpointWithFixedDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertTextEquals("ORDER NUMBER: UK171359906")
            .assertHeightIsEqualTo(300.dp)
            .assertWidthIsEqualTo(200.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/BasicText_with_States_Breakpoint.json")
    @DcuiConfig(
        windowSize = WindowSize(500, 500),
        isDarkModeEnabled = true,
        pseudoState = TestPseudoState(isPressed = true),
    )
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testBasicTextComponentWithStatesAndBreakpoint() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .assertTextEquals("ORDER NUMBER: UK171359906")
            .assertHeightIsEqualTo(190.dp)
            .assertWidthIsEqualTo(270.dp)
            .assertPaddingValues(top = 10, end = 20, bottom = 15, start = 25)
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/BasicText_with_Margin.json")
    fun testBasicTextWithMargin() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 10, end = 20, bottom = 15, start = 25)
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/BasicText_with_Margin.json")
    @DcuiConfig(windowSize = WindowSize(800, 500), isDarkModeEnabled = true)
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500), DcuiBreakpoint("Desktop", 800)])
    fun testBasicTextBreakpointWithMargin() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 2, end = 4, bottom = 8, start = 16)
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/BasicText_with_BackgroundColor.json")
    @DcuiConfig(windowSize = WindowSize(500, 500))
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testBasicTextComponentBreakpointWithBackgroundColor() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertBackgroundColor("#ababab")
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/RichText_with_FontStyles.json")
    @DcuiConfig(componentTag = "TestTag")
    fun testRichTextComponentRenderingWithFontProperties() {
        composeTestRule.onNodeWithTag("TestTag")
            .assertIsDisplayed()
            .assertFontSize(16)
            .assertFontWeight(400)
            .assertLineHeight(19)
            .assertLetterSpacing(4)
            .assertHorizontalTextAlign(TextAlign.Right)
            .assertBaselineTextAlign(BaselineShift.Superscript)
            .assertTextDecoration(TextDecoration.Underline)
            .assertFontStyle(FontStyle.Italic)
            .assertTextColor("#FF3700B3")
            .assertTextEquals("ORDER NUMBER: UK171359906")
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/RichText_with_FontStyles.json")
    @DcuiConfig(componentTag = "TestTag", windowSize = WindowSize(500, 500))
    @DcuiNodeComponentState(breakpoints = [DcuiBreakpoint("Landscape", 500)])
    fun testRichTextComponentBreakpointRenderingWithFontProperties() {
        composeTestRule.onNodeWithTag("TestTag")
            .assertIsDisplayed()
            .assertFontSize(20)
            .assertFontWeight(400)
            .assertLineHeight(20)
            .assertLetterSpacing(5)
            .assertHorizontalTextAlign(TextAlign.Right)
            .assertBaselineTextAlign(BaselineShift.Superscript)
            .assertTextDecoration(TextDecoration.Underline)
            .assertFontStyle(FontStyle.Italic)
            .assertTextColor("#FFABABAB")
            .assertTextEquals("ORDER NUMBER: UK171359906")
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/RichText_with_FontStyles.json")
    @DcuiConfig(componentTag = "TestTag", pseudoState = TestPseudoState(isPressed = true))
    fun testRichTextComponentRenderingInPressedStateWithFontProperties() {
        composeTestRule.onNodeWithTag("TestTag")
            .assertIsDisplayed()
            .assertFontSize(18)
            .assertFontWeight(500)
            .assertLineHeight(20)
            .assertLetterSpacing(5)
            .assertHorizontalTextAlign(TextAlign.Left)
            .assertBaselineTextAlign(BaselineShift.Subscript)
            .assertTextDecoration(TextDecoration.LineThrough)
            .assertFontStyle(FontStyle.Italic)
            .assertTextColor("#FF4400B3")
            .assertTextEquals("ORDER NUMBER: UK171359906")
    }

    @Test
    @DcuiNodeJson(
        jsonString = """
            {
              "type": "BasicText",
              "node": {
                "value": "Offers %^STATE.IndicatorPosition^% of %^STATE.TotalOffers^%"
              }
            }
        """,
    )
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 6)
    fun testBasicTextStateValueWithEvenOffersAndTwoViewableItems() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertTextEquals("Offers 2 of 6")
    }

    @Test
    @DcuiNodeJson(
        jsonString = """
            {
              "type": "BasicText",
              "node": {
                "value": "%^STATE.TotalOffers^%"
              }
            }
        """,
    )
    @DcuiNodeComponentState(totalOffer = 6)
    fun testBasicTextStateValueOnlyTotalOffers() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertTextEquals("6")
    }

    @Test
    @DcuiNodeJson(
        jsonString = """
        {
          "type": "RichText",
          "node": {
            "value": "<a href='https://rokt.com'>Click here</a>"
          }
        }
    """,
    )
    fun testRichTextComponentWithHyperLink() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertTextEquals("Click here")
            .assertIsDisplayed()
            .performTouchInput { click(position = centerLeft) }

        Assert.assertTrue(
            getCapturedEvents().stream()
                .anyMatch { e ->
                    e.equals(
                        LayoutContract.LayoutEvent.UrlSelected(
                            "https://rokt.com",
                            OpenLinks.Externally,
                        ),
                    )
                },
        )
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/RichText_with_link_styles.json")
    fun testRichTextComponentLinkStyleWithHyperLink() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertTextEquals("CLICK HERE")
            .assertLinkFontSize(16)
            .assertLinkTextColor("#FF3700B3")
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/RichText_with_link_styles_with_states.json")
    @DcuiConfig(componentTag = "TestTag", pseudoState = TestPseudoState(isPressed = true))
    fun testRichTextComponentRenderingLinkStyleInPressedState() {
        composeTestRule.onNodeWithTag("TestTag")
            .assertIsDisplayed()
            .assertLinkFontSize(12)
            .assertLinkFontWeight(100)
            .assertLinkLetterSpacing(11)
            .assertLinkBaselineTextAlign(BaselineShift.None)
            .assertLinkTextDecoration(TextDecoration.LineThrough)
            .assertLinkFontStyle(FontStyle.Italic)
            .assertLinkTextColor("#FF4900B1")
            .assertTextEquals("Click here")
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/RichText_with_link_capitalize_transform.json")
    fun testRichTextComponentLinkTransformCapitalize() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertTextEquals("Click Here")
    }

    @Test
    @DcuiNodeJson(jsonFile = "TextComponent/RichText_with_link_lowercase_transform.json")
    fun testRichTextComponentLinkTransformLowerCase() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertTextEquals("click here")
    }

    @Test
    @DcuiNodeJson(
        jsonString = """
            {
              "type": "BasicText",
              "node": {
                "value": "Current offer position is %^STATE.IndicatorPosition^%"
              }
            }
        """,
    )
    @DcuiNodeComponentState(currentOffer = 2, totalOffer = 6)
    fun testBasicTextStateValueCurrentOffer() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertTextEquals("Current offer position is 3")
    }

    @Test
    @DcuiNodeJson(
        jsonString = """
            {
              "type": "BasicText",
              "node": {
                "value": "%^STATE.IndicatorPosition^%"
              }
            }
        """,
    )
    @DcuiNodeComponentState(currentOffer = 2, totalOffer = 6)
    fun testBasicTextStateValueOnlyCurrentOffer() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertTextEquals("3")
    }

    @Test
    @DcuiNodeJson(
        jsonString = """
            {
              "type": "BasicText",
              "node": {
                "value": "Offers %^STATE.IndicatorPosition^% of %^STATE.TotalOffers^%"
              }
            }
        """,
    )
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 5)
    fun testBasicTextStateValueWithTotalAndCurrentOffer() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertTextEquals("Offers 1 of 5")
    }

    @Test
    @DcuiNodeJson(
        jsonString = """
            {
              "type": "BasicText",
              "node": {
                "value": "Offers %^STATE.IndicatorPosition^% of %^STATE.TotalOffers^%"
              }
            }
        """,
    )
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 6, viewableItems = [2])
    fun testBasicTextStateValueWithOddOffersAndTwoViewableItems() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertTextEquals("Offers 1 of 3")
    }
}
