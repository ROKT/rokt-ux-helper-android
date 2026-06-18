package com.rokt.roktux.snapshot

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import coil3.ImageLoader
import coil3.asImage
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

    private fun testImageLoader(): ImageLoader {
        val engine = FakeImageLoaderEngine.Builder()
            .default(ColorDrawable(Color.RED).asImage())
            .build()

        return ImageLoader.Builder(RuntimeEnvironment.getApplication())
            .components { add(engine) }
            .build()
    }

    private fun roktUxConfig(): RoktUxConfig = RoktUxConfig
        .builder()
        .imageHandlingStrategy(ImageLoaderStrategy(testImageLoader()))
        .build()
}
