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
 * Visual regression for DCUI [image.scale] on StaticImage and DataImage: `fit`, `fill`, and
 * `crop` drive Compose content scaling; non-`fit` scales also expand to max width. Uses the same
 * in-memory PNG (56×56) inside a fixed 120×80 slot so modes produce visibly different crops.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class ImageScaleSnapshotTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonString = COLUMN_OPEN + STATIC_IMAGE_FIT + COLUMN_CLOSE)
    fun testStaticImageScaleFit() = captureAfterImageLoad()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_OPEN + STATIC_IMAGE_FILL + COLUMN_CLOSE)
    fun testStaticImageScaleFill() = captureAfterImageLoad()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_OPEN + STATIC_IMAGE_CROP + COLUMN_CLOSE)
    fun testStaticImageScaleCrop() = captureAfterImageLoad()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_OPEN + DATA_IMAGE_FIT + COLUMN_CLOSE)
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_creative_image_data_uri.json")
    fun testDataImageScaleFit() = captureAfterImageLoad()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_OPEN + DATA_IMAGE_FILL + COLUMN_CLOSE)
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_creative_image_data_uri.json")
    fun testDataImageScaleFill() = captureAfterImageLoad()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_OPEN + DATA_IMAGE_CROP + COLUMN_CLOSE)
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_creative_image_data_uri.json")
    fun testDataImageScaleCrop() = captureAfterImageLoad()

    private fun captureAfterImageLoad() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
        composeTestRule.waitForIdle()
        Thread.sleep(SNAPSHOT_IMAGE_LOAD_DELAY_MS)
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }

    private companion object {
        const val SNAPSHOT_IMAGE_LOAD_DELAY_MS = 400L

        /** 56×56 PNG from demo layouts — aspect differs from the 120×80 slot so scale modes diverge. */
        private const val DEMO_PNG_DATA_URI =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADgAAAA4CAYAAACohjseAAAACXBIWXMAACxLAAAsSwGlPZapAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAFJSURBVHgB7ZpBDsIwDAQjXsL/P9Gn5CkDCHypBCTEjjcRK1W92PJM1aQ5tJRTgOv9Oh73skiamV+FlWfqCpLNzKdCVpBsZn5TKC3ZzPylUFKyi5nn4mxJVZBslLMcvQ01U/JnVhaQHGZEWNKNDUFJdyaEJMNYEJAMZyBRctrsDMnpM2cOJOutmTGY7HUfCYDAphYGgopcBBBqcp5gqMp5AKIuNwK6jNyAZE/ttSikU3ItOYujZJWTszhIVlk5y4BklZez/CBZo+Qu5Z++bP2KsvMmw86fCXb+0LPzUY2dD9sjoKhLegCiKukJhppkBBAqkpEgZEvOACBLcubg6ZIkPNVpM0lcF+GzEdjZwhgQkAtjQUjOnQlBOTc2hOWGGVlAbkiS/894OnKWbuYvDVJylm7mNw2ScpZu5lODtJylm/nVcKwgZ/nEfAOqCnML3il4nQAAAABJRU5ErkJggg=="

        private const val COLUMN_OPEN =
            """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":160}},"background":{"backgroundColor":{"light":"#E0E0E0","dark":"#303030"}},"container":{"alignItems":"center","justifyContent":"center"}}}]}},"children":["""

        private const val COLUMN_CLOSE = "]}}"

        private const val STATIC_IMAGE_FIT =
            """{"type":"StaticImage","node":{"url":{"light":"$DEMO_PNG_DATA_URI","dark":"$DEMO_PNG_DATA_URI"},"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":120},"height":{"type":"fixed","value":80}},"image":{"scale":"fit"}}}]}}}}"""

        private const val STATIC_IMAGE_FILL =
            """{"type":"StaticImage","node":{"url":{"light":"$DEMO_PNG_DATA_URI","dark":"$DEMO_PNG_DATA_URI"},"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":120},"height":{"type":"fixed","value":80}},"image":{"scale":"fill"}}}]}}}}"""

        private const val STATIC_IMAGE_CROP =
            """{"type":"StaticImage","node":{"url":{"light":"$DEMO_PNG_DATA_URI","dark":"$DEMO_PNG_DATA_URI"},"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":120},"height":{"type":"fixed","value":80}},"image":{"scale":"crop"}}}]}}}}"""

        private const val DATA_IMAGE_FIT =
            """{"type":"DataImage","node":{"imageKey":"creativeImage","styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":120},"height":{"type":"fixed","value":80}},"image":{"scale":"fit"}}}]}}}}"""

        private const val DATA_IMAGE_FILL =
            """{"type":"DataImage","node":{"imageKey":"creativeImage","styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":120},"height":{"type":"fixed","value":80}},"image":{"scale":"fill"}}}]}}}}"""

        private const val DATA_IMAGE_CROP =
            """{"type":"DataImage","node":{"imageKey":"creativeImage","styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":120},"height":{"type":"fixed","value":80}},"image":{"scale":"crop"}}}]}}}}"""
    }
}
