package com.rokt.roktux.component

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.core.testutils.annotations.DcuiConfig
import com.core.testutils.annotations.DcuiNodeJson
import com.core.testutils.assertion.assertBackgroundColor
import com.core.testutils.assertion.assertHeightFit
import com.core.testutils.assertion.assertHeightWithPercentage
import com.core.testutils.assertion.assertOffsetValues
import com.core.testutils.assertion.assertPaddingValues
import com.core.testutils.assertion.assertWidthFit
import com.core.testutils.assertion.assertWidthWithPercentage
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_FixedDimension.json")
    fun testImageComponentWithFixedDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHeightIsEqualTo(100.dp)
            .assertWidthIsEqualTo(200.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_FitDimension.json")
    fun testImageComponentWithFitDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertWidthFit()
            .assertHeightFit()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_PercentageDimension.json")
    fun testImageComponentWithPercentageDimensions() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertWidthWithPercentage(80)
            .assertHeightWithPercentage(30)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_Padding.json")
    fun testImageComponentWithPadding() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 5, end = 10, bottom = 15, start = 20)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_Margin.json")
    fun testImageComponentWithMargin() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertPaddingValues(top = 5, end = 10, bottom = 15, start = 20)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_Offset.json")
    fun testImageComponentWithOffset() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertOffsetValues(x = 10, y = 20)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_BackgroundColor.json")
    fun testImageComponentWithBackgroundColor() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertBackgroundColor("#1a2b3c")
    }

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_AltText.json")
    fun testImageComponentAltText() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertContentDescriptionEquals("alternative text")
    }

    @Ignore("Until dark mode")
    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_BackgroundColor.json")
    @DcuiConfig(isDarkModeEnabled = true)
    fun testImageComponentWithBackgroundColorInDarkMode() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertBackgroundColor("#FF3700B4")
    }

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_validPNG_DataUri.json")
    fun testImageComponentWithValidPNGDataUri() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHeightIsEqualTo(100.dp)
            .assertWidthIsEqualTo(100.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_validJPEG_DataUri.json")
    fun testImageComponentWithValidJPEGDataUri() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHeightIsEqualTo(100.dp)
            .assertWidthIsEqualTo(100.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_validSVG_DataUri.json")
    fun testImageComponentWithValidSVGDataUri() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHeightIsEqualTo(100.dp)
            .assertWidthIsEqualTo(100.dp)
    }

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_invalid_DataUri.json")
    @Ignore("Temporarily ignoring")
    fun testImageComponentWithInvalidDataUri() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ImageComponent/Image_with_validSVG_DataUri.json")
    @DcuiConfig(isDarkModeEnabled = true)
    @Ignore("Temporarily ignoring")
    fun testImageComponentWithInvalidDataUriInDarkMode() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertDoesNotExist()
    }
}
