package com.rokt.roktux.component

import com.rokt.modelmapper.uimodel.TextUiTransform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.util.Locale
import kotlin.random.Random

class RichTextSpanTransformTest {

    @Test
    fun `transformSpanRanges matches transforming each range in turn`() {
        val random = Random(42)
        val alphabet = "abcXYZ  "
        repeat(2_000) {
            val text = buildString { repeat(random.nextInt(0, 40)) { append(alphabet[random.nextInt(alphabet.length)]) } }
            val count = random.nextInt(0, 8)
            val starts = IntArray(count)
            val ends = IntArray(count)
            for (index in 0 until count) {
                val start = random.nextInt(0, text.length + 1)
                starts[index] = start
                ends[index] = random.nextInt(start, text.length + 1)
            }
            for (transform in TextUiTransform.entries) {
                assertEquals(
                    "text='$text' starts=${starts.toList()} ends=${ends.toList()} transform=$transform",
                    rangeByRange(text, starts, ends, transform),
                    transformSpanRanges(text, starts, ends, transform),
                )
            }
        }
    }

    @Test
    fun `transformSpanRanges returns the text unchanged when there is nothing to transform`() {
        val text = "some text"
        assertSame(text, transformSpanRanges(text, intArrayOf(0), intArrayOf(4), TextUiTransform.None))
        assertSame(text, transformSpanRanges(text, intArrayOf(0), intArrayOf(4), null))
        assertSame(text, transformSpanRanges(text, IntArray(0), IntArray(0), TextUiTransform.Uppercase))
    }

    @Test
    fun `transformSpanRanges ignores ranges outside the text`() {
        assertEquals("ABcd", transformSpanRanges("abcd", intArrayOf(-3, 9), intArrayOf(2, 12), TextUiTransform.Uppercase))
    }

    @Test(timeout = 10_000)
    fun `transformSpanRanges stays fast for text with many small spans`() {
        // About 1.4 MB of text with 100,000 single-character spans. Rebuilding the whole string
        // once per span would copy about 140 GB of characters.
        val spanCount = 100_000
        val text = "a".repeat(spanCount) + "b".repeat(1_400_000 - spanCount)
        val starts = IntArray(spanCount) { it }
        val ends = IntArray(spanCount) { it + 1 }
        for (transform in listOf(TextUiTransform.Uppercase, TextUiTransform.Lowercase, TextUiTransform.Capitalize)) {
            val result = transformSpanRanges(text, starts, ends, transform)
            assertEquals(text.length, result.length)
        }
        assertEquals("A".repeat(spanCount), transformSpanRanges(text, starts, ends, TextUiTransform.Uppercase).take(spanCount))
    }

    // The previous algorithm: rebuild the whole string once per span.
    private fun rangeByRange(text: String, starts: IntArray, ends: IntArray, transform: TextUiTransform): String {
        var result = text
        for (index in starts.indices) {
            val start = starts[index]
            val end = ends[index]
            result = result.replaceRange(start, end, apply(result.substring(start, end), transform))
        }
        return result
    }

    private fun apply(text: String, transform: TextUiTransform): String = when (transform) {
        TextUiTransform.Capitalize -> text.split(" ").joinToString(" ") { value ->
            value.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        TextUiTransform.Lowercase -> text.lowercase()

        TextUiTransform.Uppercase -> text.uppercase()

        TextUiTransform.None -> text
    }
}
