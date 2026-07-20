package com.rokt.roktux.snapshot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual regression coverage for StaticImage data URI loading across the image formats Coil
 * supports in UX Helper: SVG, PNG, and JPEG.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class StaticImageDataUriSnapshotTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "Snapshot/StaticImage_data_uri_svg.json")
    fun testStaticImageSvgDataUri() = captureAfterImageLoad()

    @Test
    @DcuiNodeJson(jsonFile = "Snapshot/StaticImage_data_uri_png.json")
    fun testStaticImagePngDataUri() = captureAfterImageLoad()

    @Test
    @DcuiNodeJson(jsonFile = "Snapshot/StaticImage_data_uri_jpeg.json")
    fun testStaticImageJpegDataUri() = captureAfterImageLoad()

    private fun captureAfterImageLoad() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
        composeTestRule.waitForIdle()
        Thread.sleep(SNAPSHOT_IMAGE_LOAD_DELAY_MS)
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }

    private companion object {
        const val SNAPSHOT_IMAGE_LOAD_DELAY_MS = 400L
    }
}
