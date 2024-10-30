package com.core.testutils.assertion

import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

fun SemanticsNodeInteraction.assertFontSize(fontSize: Int) = assert(hasFontSize(fontSize))

fun SemanticsNodeInteraction.assertFontWeight(fontWeight: Int) = assert(hasFontWeight(fontWeight))

fun SemanticsNodeInteraction.assertLineHeight(lineHeight: Int) = assert(hasLineHeight(lineHeight))

fun SemanticsNodeInteraction.assertTextColor(textColor: String) = assert(hasTextColor(textColor))

fun SemanticsNodeInteraction.assertLetterSpacing(letterSpacing: Int) = assert(hasLetterSpacing(letterSpacing))

fun SemanticsNodeInteraction.assertHorizontalTextAlign(textAlign: TextAlign) = assert(hasTextAlign(textAlign))

fun SemanticsNodeInteraction.assertFontStyle(fontStyle: FontStyle) = assert(hasFontStyle(fontStyle))

fun SemanticsNodeInteraction.assertLinkFontSize(fontSize: Int) = assert(hasLinkFontSize(fontSize))

fun SemanticsNodeInteraction.assertLinkFontWeight(fontWeight: Int) = assert(hasLinkFontWeight(fontWeight))

fun SemanticsNodeInteraction.assertLinkTextColor(textColor: String) = assert(hasLinkTextColor(textColor))

fun SemanticsNodeInteraction.assertLinkLetterSpacing(letterSpacing: Int) = assert(hasLinkLetterSpacing(letterSpacing))

fun SemanticsNodeInteraction.assertLinkFontStyle(fontStyle: FontStyle) = assert(hasLinkFontStyle(fontStyle))

fun SemanticsNodeInteraction.assertTextDecoration(textDecoration: TextDecoration) =
    assert(hasTextDecoration(textDecoration))

fun SemanticsNodeInteraction.assertBaselineTextAlign(baselineShift: BaselineShift) =
    assert(hasBaselineShift(baselineShift))

fun SemanticsNodeInteraction.assertLinkTextDecoration(textDecoration: TextDecoration) =
    assert(hasLinkTextDecoration(textDecoration))

fun SemanticsNodeInteraction.assertLinkBaselineTextAlign(baselineShift: BaselineShift) =
    assert(hasLinkBaselineShift(baselineShift))

private fun hasLinkFontSize(fontSize: Int): SemanticsMatcher = SemanticsMatcher("FontSize = $fontSize") { node ->
    val targetFontSize = getAnnotatedSpanUrlTextStyle(node)?.fontSize
    validateAndPrintError("FontSize", expected = fontSize.sp, target = targetFontSize)
}

private fun hasFontSize(fontSize: Int): SemanticsMatcher = SemanticsMatcher("FontSize = $fontSize") { node ->
    val targetFontSize = getTextStyle(node)?.fontSize
    validateAndPrintError("FontSize", expected = fontSize.sp, target = targetFontSize)
}

private fun hasFontWeight(fontWeight: Int) = SemanticsMatcher("FontWeight = $fontWeight") { node ->
    val targetFontWeight = getTextStyle(node)?.fontWeight
    validateAndPrintError("FontWeight", expected = FontWeight(fontWeight), target = targetFontWeight)
}

private fun hasLinkFontWeight(fontWeight: Int) = SemanticsMatcher("FontWeight = $fontWeight") { node ->
    val targetFontWeight = getAnnotatedSpanUrlTextStyle(node)?.fontWeight
    validateAndPrintError("FontWeight", expected = FontWeight(fontWeight), target = targetFontWeight)
}

private fun hasLineHeight(lineHeight: Int) = SemanticsMatcher("LineHeight = $lineHeight") { node ->
    val targetLineHeight = getTextStyle(node)?.lineHeight
    validateAndPrintError("LineHeight", expected = lineHeight.sp, target = targetLineHeight)
}

private fun hasTextColor(textColor: String) = SemanticsMatcher("TextColor = $textColor") { node ->
    val targetTextColor = getTextStyle(node)?.color
    validateAndPrintError(
        "TextColor",
        expected = Color.parseColor(textColor),
        target = targetTextColor?.toArgb(),
    )
}

private fun hasLinkTextColor(textColor: String) = SemanticsMatcher("TextColor = $textColor") { node ->
    val targetTextColor = getAnnotatedSpanUrlTextStyle(node)?.color
    validateAndPrintError(
        "TextColor",
        expected = Color.parseColor(textColor),
        target = targetTextColor?.toArgb(),
    )
}

private fun hasLetterSpacing(letterSpacing: Int) = SemanticsMatcher("LetterSpacing = $letterSpacing") { node ->
    val targetLetterSpacing = getTextStyle(node)?.letterSpacing
    validateAndPrintError("LetterSpacing", expected = letterSpacing.sp, target = targetLetterSpacing)
}

private fun hasLinkLetterSpacing(letterSpacing: Int) = SemanticsMatcher("LetterSpacing = $letterSpacing") { node ->
    val targetLetterSpacing = getAnnotatedSpanUrlTextStyle(node)?.letterSpacing
    validateAndPrintError("LetterSpacing", expected = letterSpacing.sp, target = targetLetterSpacing)
}

private fun hasTextAlign(textAlign: TextAlign) = SemanticsMatcher("TextAlign = $textAlign") { node ->
    val targetTextAlign = getTextStyle(node)?.textAlign
    validateAndPrintError("TextAlign", expected = textAlign, target = targetTextAlign)
}

private fun hasBaselineShift(baselineShift: BaselineShift) =
    SemanticsMatcher("BaselineShift = $baselineShift") { node ->
        val targetBaselineShift = getTextStyle(node)?.baselineShift
        validateAndPrintError("BaselineShift", expected = baselineShift, target = targetBaselineShift)
    }

private fun hasLinkBaselineShift(baselineShift: BaselineShift) =
    SemanticsMatcher("BaselineShift = $baselineShift") { node ->
        val targetBaselineShift = getAnnotatedSpanUrlTextStyle(node)?.baselineShift
        validateAndPrintError("BaselineShift", expected = baselineShift, target = targetBaselineShift)
    }

private fun hasTextDecoration(textDecoration: TextDecoration) =
    SemanticsMatcher("TextDecoration = $textDecoration") { node ->
        val targetTextDecoration = getTextStyle(node)?.textDecoration
        validateAndPrintError("TextDecoration", expected = textDecoration, target = targetTextDecoration)
    }

private fun hasLinkTextDecoration(textDecoration: TextDecoration) =
    SemanticsMatcher("TextDecoration = $textDecoration") { node ->
        val targetTextDecoration = getAnnotatedSpanUrlTextStyle(node)?.textDecoration
        validateAndPrintError("TextDecoration", expected = textDecoration, target = targetTextDecoration)
    }

private fun hasFontStyle(fontStyle: FontStyle) = SemanticsMatcher("FontStyle = $fontStyle") { node ->
    val targetFontStyle = getTextStyle(node)?.fontStyle
    validateAndPrintError("FontStyle", expected = fontStyle, target = targetFontStyle)
}

private fun hasLinkFontStyle(fontStyle: FontStyle) = SemanticsMatcher("FontStyle = $fontStyle") { node ->
    val targetFontStyle = getAnnotatedSpanUrlTextStyle(node)?.fontStyle
    validateAndPrintError("FontStyle", expected = fontStyle, target = targetFontStyle)
}

private fun getTextStyle(node: SemanticsNode): TextStyle? {
    val textLayoutResults = mutableListOf<TextLayoutResult>()
    node.config.getOrNull(SemanticsActions.GetTextLayoutResult)?.action?.invoke(textLayoutResults)
    return textLayoutResults.getOrNull(0)?.layoutInput?.style
}

private fun getAnnotatedSpanUrlTextStyle(node: SemanticsNode): SpanStyle? {
    val textLayoutResults = mutableListOf<TextLayoutResult>()
    node.config.getOrNull(SemanticsActions.GetTextLayoutResult)?.action?.invoke(textLayoutResults)
    return textLayoutResults.getOrNull(0)?.layoutInput?.text?.spanStyles?.getOrNull(0)?.item
}

private fun <T> validateAndPrintError(property: String, expected: T, target: T?): Boolean {
    val result = expected == target
    if (!result) println("Expected $property $expected. Target $property $target")
    return result
}
