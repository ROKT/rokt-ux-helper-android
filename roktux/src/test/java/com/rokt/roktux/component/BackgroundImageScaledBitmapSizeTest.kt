package com.rokt.roktux.component

import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.times
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toIntSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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

    @Test
    fun `clamping scales both dimensions by the same factor instead of distorting aspect ratio`() {
        // Only the height (100000) is outside the safe range here; the width (1000) is not.
        // Coercing each axis independently would leave width at 1000 and crush height to 4096,
        // stretching the image roughly 24x wider than tall instead of preserving its ~1:100 ratio.
        val targetSize = Size(width = 1000f, height = 100000f)

        val scaledBitmapSize = backgroundImageScaledBitmapSize(targetSize)

        assertTrue(
            "expected width to be scaled down from the original 1000, but was ${scaledBitmapSize.width}",
            scaledBitmapSize.width < 1000,
        )
        assertTrue(
            "expected a bounded width, but was ${scaledBitmapSize.width}",
            scaledBitmapSize.width <= 4096,
        )
        assertTrue(
            "expected a bounded height, but was ${scaledBitmapSize.height}",
            scaledBitmapSize.height <= 4096,
        )

        val originalRatio = targetSize.height / targetSize.width
        val scaledRatio = scaledBitmapSize.height.toFloat() / scaledBitmapSize.width.toFloat()
        assertEquals(
            "expected the ~1:100 aspect ratio to be preserved after clamping",
            originalRatio,
            scaledRatio,
            originalRatio * 0.05f,
        )
    }

    @Test
    fun `a target already within the safe range is left unscaled`() {
        val targetSize = Size(width = 200f, height = 100f)

        val scaledBitmapSize = backgroundImageScaledBitmapSize(targetSize)

        assertEquals(IntSize(width = 200, height = 100), scaledBitmapSize)
    }

    @Test
    fun `offset is derived from the same clamped size that is actually drawn`() {
        // A target size that gets clamped (see the aspect-ratio test above). If the offset were
        // computed from the raw, unclamped target size instead, it would place the bitmap as if
        // it were still 1000x100000, even though the bitmap actually drawn is much smaller -
        // pushing it outside the visible container.
        val targetSize = Size(width = 1000f, height = 100000f)
        val containerSize = IntSize(width = 2000, height = 2000)
        val alignment = Alignment.BottomEnd
        val layoutDirection = LayoutDirection.Ltr
        val clampedSize = backgroundImageScaledBitmapSize(targetSize)

        val offset = backgroundImageOffset(
            scaledBitmapSize = clampedSize,
            containerSize = containerSize,
            alignment = alignment,
            layoutDirection = layoutDirection,
        )

        val expectedOffset = alignment.align(clampedSize, containerSize, layoutDirection)
        assertEquals(expectedOffset, offset)

        // Sanity check that this test actually exercises the fix: an offset computed from the
        // raw, unclamped target size would be a different (wrong) value.
        val unclampedOffset = alignment.align(targetSize.toIntSize(), containerSize, layoutDirection)
        assertNotEquals(unclampedOffset, offset)
    }
}
