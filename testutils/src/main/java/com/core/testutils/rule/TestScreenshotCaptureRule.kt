package com.core.testutils.rule

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.FileOutputStream

/**
 * Test rule to capture the screenshot of a Compose test.
 * The screenshot of the root node will be captured and saved in the device files directory.
 * Screenshot of failed test will be captured by default.
 * Enable `@ScreenshotConfig(captureOnSuccess = true)` annotation in order to capture screenshot of success as well
 */
class TestScreenshotCaptureRule(private val composeTestRule: ComposeContentTestRule) : TestWatcher() {

    override fun failed(e: Throwable?, description: Description?) {
        captureScreenshot(description)
    }

    override fun succeeded(description: Description?) {
        if (description?.getAnnotation(ScreenshotConfig::class.java)?.captureOnSuccess == true) {
            captureScreenshot(description)
        }
    }

    private fun captureScreenshot(description: Description?) {
        val fileName = description?.let { "${it.className}.${it.methodName}" } ?: System.currentTimeMillis().toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentUiStateImage = composeTestRule.onRoot().captureToImage().asAndroidBitmap()
            currentUiStateImage.save(fileName)
        }
    }
}

private fun Bitmap.save(file: String) {
    val path = InstrumentationRegistry.getInstrumentation().targetContext.filesDir.canonicalPath
    FileOutputStream("$path/$file.png").use { out ->
        compress(Bitmap.CompressFormat.PNG, 100, out)
    }
}
