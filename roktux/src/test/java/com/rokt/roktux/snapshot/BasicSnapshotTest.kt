package com.rokt.roktux.snapshot

import android.graphics.drawable.ColorDrawable
import android.os.Looper
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureScreenRoboImage
import com.rokt.core.testutils.TestJsonLoader
import com.rokt.roktux.RoktLayout
import com.rokt.roktux.RoktUxConfig
import com.rokt.roktux.imagehandler.ImageLoaderStrategy
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "xxhdpi",
)
class BasicSnapshotTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalRoborazziApi::class, ExperimentalCoilApi::class)
    @Test
    fun testOverlay() = runTest(testDispatcher) {
        val experienceResponse = TestJsonLoader.loadJsonFromAssetsDirectory("Snapshot", "Overlay.json")

        val engine = FakeImageLoaderEngine.Builder()
            .intercept(true, ColorDrawable(android.graphics.Color.RED))
            .build()

        val imageLoader = ImageLoader.Builder(RuntimeEnvironment.getApplication())
            .components { add(engine) }
            .dispatcher(testDispatcher)
            .build()

        composeTestRule.setContent {
            Text("Some random text")
            RoktLayout(
                experienceResponse = experienceResponse,
                location = "Location1",
                mainDispatcher = testDispatcher,
                ioDispatcher = testDispatcher,
                roktUxConfig = RoktUxConfig
                    .builder()
                    .imageHandlingStrategy(ImageLoaderStrategy(imageLoader))
                    .build(),
            )
        }

        composeTestRule.waitForIdle()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        captureScreenRoboImage()
    }
}
