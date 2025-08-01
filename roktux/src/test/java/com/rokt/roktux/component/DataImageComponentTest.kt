package com.rokt.roktux.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class DataImageComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "DataImageComponent/DataImage_with_InvalidImageKey.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testDataImageComponentWithUnavailableKey() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsNotDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "DataImageComponent/DataImage_with_ValidImageKey.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testDataImageComponentWithValidKey() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
    }

    @Test
    @DcuiNodeJson(jsonFile = "DataImageComponent/DataImage_with_FallbackImageKey.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_valid_key.json")
    fun testDataImageComponentWithFallbackKey() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
    }
}
