package com.rokt.core.testutils.rule

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Test rule to capture the screenshot of a Compose test on failure (and on success when
 * `@ScreenshotConfig(captureOnSuccess = true)` is set).
 *
 * Uses Roborazzi's `captureRoboImage` rather than Compose's `captureToImage` because the latter
 * requires a real Android `Window` / `Surface` (PixelCopy + forceRedraw) and times out after 2s
 * under Robolectric, masking the underlying test failure with a noisy `ComposeTimeoutException`.
 *
 * Images are written to the module's `build/outputs/roborazzi/` directory so they are picked up
 * by the existing roborazzi artifact upload in CI.
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
        val fileName = description?.let { "${it.className}.${it.methodName}" }
            ?: System.currentTimeMillis().toString()
        runCatching {
            composeTestRule.onRoot().captureRoboImage(filePath = "build/outputs/roborazzi/$fileName.png")
        }.onFailure { error ->
            // Don't let capture failures shadow the test's real assertion error. Robolectric in
            // LEGACY graphics mode can still struggle to render certain trees; log and move on.
            println(
                "TestScreenshotCaptureRule: failed to capture $fileName (${error.javaClass.simpleName}: ${error.message})",
            )
        }
    }
}
