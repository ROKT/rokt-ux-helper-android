package com.rokt.roktux.snapshot

import coil3.ColorImage
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.test.FakeImageLoaderEngine
import com.rokt.core.testutils.TestJsonLoader
import com.rokt.roktux.RoktLayout
import com.rokt.roktux.RoktUxConfig
import com.rokt.roktux.imagehandler.ImageLoaderStrategy
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.robolectric.RuntimeEnvironment

abstract class RoktLayoutSnapshotTest : BaseDcuiEspressoTest() {

    protected val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoilApi::class)
    protected fun renderLayout(fileName: String) {
        val experienceResponse = TestJsonLoader.loadJsonFromAssetsDirectory("Snapshot", fileName)
        val roktUxConfig = roktUxConfig()

        composeTestRule.setContent {
            RoktLayout(
                experienceResponse = experienceResponse,
                location = "Location1",
                mainDispatcher = testDispatcher,
                ioDispatcher = testDispatcher,
                roktUxConfig = roktUxConfig,
            )
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun testImageLoader(): ImageLoader {
        val engine = FakeImageLoaderEngine.Builder()
            .default(ColorImage(android.graphics.Color.RED))
            .build()

        return ImageLoader.Builder(RuntimeEnvironment.getApplication())
            .components { add(engine) }
            .build()
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun roktUxConfig(): RoktUxConfig = RoktUxConfig
        .builder()
        .imageHandlingStrategy(ImageLoaderStrategy(testImageLoader()))
        .build()
}
