package com.rokt.roktux.snapshot

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureScreenRoboImage
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "xxhdpi",
)
class EmbeddedSnapshotTest : RoktLayoutSnapshotTest() {

    @OptIn(ExperimentalRoborazziApi::class)
    @Test
    fun testEmbeddedOneByOne() = runTest(testDispatcher) {
        renderLayout("EmbeddedOneByOne.json")
        composeTestRule.waitForIdle()

        captureScreenRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }

    @OptIn(ExperimentalRoborazziApi::class)
    @Test
    fun testEmbeddedCompact() = runTest(testDispatcher) {
        renderLayout("EmbeddedCompact.json")
        composeTestRule.waitForIdle()

        captureScreenRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }

    @OptIn(ExperimentalRoborazziApi::class)
    @Test
    fun testEmbeddedCarousel() = runTest(testDispatcher) {
        renderLayout("EmbeddedCarousel.json")
        composeTestRule.waitForIdle()

        captureScreenRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }
}
