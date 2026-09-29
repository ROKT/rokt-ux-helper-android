package com.rokt.roktux.snapshot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual regression for DataImage in dark mode: a creative image without a dark variant is
 * hidden rather than falling back to its light variant, while an image with a dark variant still
 * renders. The light-mode case shows the same image is otherwise displayed.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class DataImageDarkModeSnapshotTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonString = COLUMN_WITH_DATA_IMAGE)
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_creative_image_data_uri_without_dark.json")
    fun testDataImageWithoutDarkVariantInLightMode() = captureAfterImageLoad()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_WITH_DATA_IMAGE)
    @DcuiConfig(testInInnerLayout = true, isDarkModeEnabled = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_creative_image_data_uri_without_dark.json")
    fun testDataImageWithoutDarkVariantInDarkMode() = captureAfterImageLoad()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_WITH_DATA_IMAGE)
    @DcuiConfig(testInInnerLayout = true, isDarkModeEnabled = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_creative_image_data_uri.json")
    fun testDataImageWithDarkVariantInDarkMode() = captureAfterImageLoad()

    private fun captureAfterImageLoad() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
        composeTestRule.waitForIdle()
        Thread.sleep(SNAPSHOT_IMAGE_LOAD_DELAY_MS)
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }

    private companion object {
        const val SNAPSHOT_IMAGE_LOAD_DELAY_MS = 400L

        private const val COLUMN_WITH_DATA_IMAGE =
            """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":160}},"background":{"backgroundColor":{"light":"#E0E0E0","dark":"#303030"}},"container":{"alignItems":"center","justifyContent":"center"}}}]}},"children":[{"type":"DataImage","node":{"imageKey":"creativeImage","styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":120},"height":{"type":"fixed","value":80}},"image":{"scale":"fit"}}}]}}}}]}}"""
    }
}
