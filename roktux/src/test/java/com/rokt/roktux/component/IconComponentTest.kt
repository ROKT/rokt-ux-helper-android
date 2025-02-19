package com.rokt.roktux.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.core.testutils.assertion.assertBackgroundColor
import com.rokt.core.testutils.assertion.assertTextColor
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IconComponentTest : BaseDcuiEspressoTest() {
    @Test
    @DcuiNodeJson(
        jsonString = """
            {
              "type": "DataIcon",
              "node": {
                "iconKey": "icon.1"
              }
            }
    """,
    )
    @DcuiConfig(testInInnerLayout = true, componentTag = "TestTag")
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testDataIconBindsToValidIconKey() {
        composeTestRule.onNodeWithTag("TestTag")
            .assertIsDisplayed()
            .assertTextEquals("Dollar")
    }

    @Test
    @DcuiNodeJson(
        jsonString = """
            {
              "type": "DataIcon",
              "node": {
                "iconKey": "notreal"
              }
            }
    """,
    )
    @DcuiConfig(testInInnerLayout = true, componentTag = "TestTag")
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testDataIconDoesNotBindToInvalidValidIconKey() {
        composeTestRule.onNodeWithTag("TestTag")
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(
        jsonString = """
            {
              "type": "StaticIcon",
              "node": {
                "name": "PercentIcon"
              }
            }
    """,
    )
    fun testStaticIconRendersCorrectly() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertTextEquals("PercentIcon")
    }

    @Test
    @DcuiNodeJson(
        jsonString = """
            {
              "type": "StaticIcon",
              "node": {
                "name": "PercentIcon",
                "styles": {
                  "elements": {
                    "own": [
                      {
                        "default": {
                          "background": {
                            "backgroundColor": {
                              "light": "#bb1010"
                            }
                          }
                        }
                      }
                    ]
                  }
                }
              }
            }
    """,
    )
    fun testStaticIconRendersWithBackgroundColor() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertTextEquals("PercentIcon")
            .assertBackgroundColor("#bb1010")
    }

    @Test
    @DcuiNodeJson(jsonFile = "IconComponent/StaticIcon_with_TextColor.json")
    @DcuiConfig(isDarkModeEnabled = true)
    fun testStaticIconRendersWithDarkTextColor() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertTextEquals("Car")
            .assertTextColor("#0000FF")
    }

    @Test
    @DcuiNodeJson(jsonFile = "IconComponent/StaticIcon_with_TextColor.json")
    @DcuiConfig(isDarkModeEnabled = false)
    fun testStaticIconRendersWithLightTextColor() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertTextEquals("Car")
            .assertTextColor("#FF0000")
    }
}
