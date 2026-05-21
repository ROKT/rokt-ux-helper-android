package com.rokt.roktux.snapshot

import android.graphics.drawable.ColorDrawable
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
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
            .default(ColorDrawable(android.graphics.Color.RED))
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
