package com.rokt.roktux.component

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.times
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Background images can come from a remote source at any aspect ratio, and the node they are
 * drawn into can be any size. [backgroundImageScaledBitmapSize] must always return a size that is
 * safe to pass to `Bitmap.createScaledBitmap`: both dimensions positive, and neither dimension
 * large enough to allocate an unreasonable amount of memory.
 */
class BackgroundImageScaledBitmapSizeTest {

    @Test
    fun `fit scale with a very thin tall source against a small destination does not collapse a dimension to zero`() {
        // A 2x1000 source scaled to fit inside a 100x50 destination: the limiting axis is height
        // (50 / 1000 = 0.05), so the width collapses to 2 * 0.05 = 0.1px, which truncates to 0.
        val srcSize = Size(width = 2f, height = 1000f)
        val dstSize = Size(width = 100f, height = 50f)
        val targetSize = srcSize.times(ContentScale.Fit.computeScaleFactor(srcSize, dstSize))

        val scaledBitmapSize = backgroundImageScaledBitmapSize(targetSize)

        assertTrue(
            "expected a positive width, but was ${scaledBitmapSize.width}",
            scaledBitmapSize.width >= 1,
        )
        assertTrue(
            "expected a positive height, but was ${scaledBitmapSize.height}",
            scaledBitmapSize.height >= 1,
        )
    }

    @Test
    fun `fill scale with a very thin tall source against a wide destination does not balloon in size`() {
        // A 1x100 source scaled to fill a 1000x50 destination: the limiting axis is width
        // (1000 / 1 = 1000), so the height balloons to 100 * 1000 = 100000px.
        val srcSize = Size(width = 1f, height = 100f)
        val dstSize = Size(width = 1000f, height = 50f)
        val targetSize = srcSize.times(ContentScale.Crop.computeScaleFactor(srcSize, dstSize))

        val scaledBitmapSize = backgroundImageScaledBitmapSize(targetSize)

        val maxReasonableDimensionPx = 4096
        assertTrue(
            "expected a bounded width, but was ${scaledBitmapSize.width}",
            scaledBitmapSize.width <= maxReasonableDimensionPx,
        )
        assertTrue(
            "expected a bounded height, but was ${scaledBitmapSize.height}",
            scaledBitmapSize.height <= maxReasonableDimensionPx,
        )
    }
}
