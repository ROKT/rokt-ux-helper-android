package com.rokt.modelmapper.mappers

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.rokt.modelmapper.uimodel.StateBlock
import com.rokt.modelmapper.uimodel.TextStylingUiProperties
import com.rokt.modelmapper.uimodel.TextUiTransform
import com.rokt.modelmapper.uimodel.ThemeColorUiModel
import com.rokt.network.model.BasicStateStylingBlock
import com.rokt.network.model.FontBaselineAlignment
import com.rokt.network.model.FontJustification
import com.rokt.network.model.InLineTextStyle
import com.rokt.network.model.TextStylingProperties
import com.rokt.network.model.TextTransform
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal fun <T : BasicStateStylingBlock<*>> ImmutableList<T>?.transformTextStyles(
    transformTextStyle: (T) -> BasicStateStylingBlock<TextStylingProperties?>,
): ImmutableList<StateBlock<TextStylingUiProperties>>? {
    val textStylingProperties: ImmutableList<BasicStateStylingBlock<TextStylingProperties?>>? =
        this?.map { transformTextStyle(it) }?.toImmutableList()
    return this?.takeIf { it.isNotEmpty() }?.let {
        transformTextStylesList(
            textStylingProperties,
            this.size,
        )
    }
}

internal fun BasicStateStylingBlock<InLineTextStyle>.toTextStylingProperties(): BasicStateStylingBlock<TextStylingProperties?> {
    return this.toBasicStateStylingBlock {
        TextStylingProperties(
            textColor = it.text.textColor,
            fontSize = it.text.fontSize,
            fontFamily = it.text.fontFamily,
            fontWeight = it.text.fontWeight,
            baselineTextAlign = it.text.baselineTextAlign,
            fontStyle = it.text.fontStyle,
            textTransform = it.text.textTransform,
            letterSpacing = it.text.letterSpacing,
            textDecoration = it.text.textDecoration,
        )
    }
}

private fun transformTextStylesList(
    textStylingProperties: ImmutableList<BasicStateStylingBlock<TextStylingProperties?>>?,
    breakpoints: Int,
): ImmutableList<StateBlock<TextStylingUiProperties>> {
    return List(breakpoints) { i ->
        StateBlock(
            default = transformTextStylingProperties(
                textStylingProperties?.getOrNull(i)?.default,
            ),
            pressed = transformTextStylingProperties(
                textStylingProperties?.getOrNull(i)?.pressed,
            ),
        )
    }.toImmutableList()
}

internal fun transformTextStylingProperties(textStylingProperties: TextStylingProperties?): TextStylingUiProperties =
    textStylingProperties?.run {
        TextStylingUiProperties(
            fontFamily = fontFamily,
            fontSize = fontSize,
            fontWeight = fontWeight?.let(::transformFontWeight),
            fontStyle = fontStyle?.let(::transformFontStyle),
            textColor = textColor?.let { ThemeColorUiModel(it.light, it.dark) },
            lineHeight = lineHeight,
            lineLimit = lineLimit,
            letterSpacing = letterSpacing,
            horizontalTextAlign = horizontalTextAlign?.let(::transformHorizontalTextAlign),
            baselineTextAlign = baselineTextAlign?.let(::transformBaselineTextAlign),
            textDecoration = textDecoration?.let(::transformTextDecoration),
            textTransform = textTransform?.let(::transformTextTransform),
        )
    } ?: TextStylingUiProperties()

private fun transformTextTransform(textTransform: TextTransform): TextUiTransform = when (textTransform) {
    TextTransform.Capitalize -> TextUiTransform.Capitalize
    TextTransform.Uppercase -> TextUiTransform.Uppercase
    TextTransform.Lowercase -> TextUiTransform.Lowercase
    TextTransform.None -> TextUiTransform.None
}

private fun transformTextDecoration(textDecoration: com.rokt.network.model.TextDecoration): TextDecoration =
    when (textDecoration) {
        com.rokt.network.model.TextDecoration.Underline -> TextDecoration.Underline
        com.rokt.network.model.TextDecoration.StrikeThrough -> TextDecoration.LineThrough
        com.rokt.network.model.TextDecoration.None -> TextDecoration.None
    }

private fun transformBaselineTextAlign(baselineTextAlign: FontBaselineAlignment): BaselineShift =
    when (baselineTextAlign) {
        FontBaselineAlignment.Super -> BaselineShift.Superscript
        FontBaselineAlignment.Sub -> BaselineShift.Subscript
        FontBaselineAlignment.Baseline -> BaselineShift.None
    }

private fun transformHorizontalTextAlign(fontJustification: FontJustification): TextAlign = when (fontJustification) {
    FontJustification.Left -> TextAlign.Left
    FontJustification.Right -> TextAlign.Right
    FontJustification.Center -> TextAlign.Center
    FontJustification.Start -> TextAlign.Start
    FontJustification.End -> TextAlign.End
    FontJustification.Justify -> TextAlign.Justify
}

private fun transformFontStyle(fontStyle: com.rokt.network.model.FontStyle): FontStyle = when (fontStyle) {
    com.rokt.network.model.FontStyle.Normal -> FontStyle.Normal
    com.rokt.network.model.FontStyle.Italic -> FontStyle.Italic
}

private fun transformFontWeight(fontWeight: com.rokt.network.model.FontWeight): Int = when (fontWeight) {
    com.rokt.network.model.FontWeight.W100 -> FontWeight.W100.weight
    com.rokt.network.model.FontWeight.W200 -> FontWeight.W200.weight
    com.rokt.network.model.FontWeight.W300 -> FontWeight.W300.weight
    com.rokt.network.model.FontWeight.W400 -> FontWeight.W400.weight
    com.rokt.network.model.FontWeight.W500 -> FontWeight.W500.weight
    com.rokt.network.model.FontWeight.W600 -> FontWeight.W600.weight
    com.rokt.network.model.FontWeight.W700 -> FontWeight.W700.weight
    com.rokt.network.model.FontWeight.W800 -> FontWeight.W800.weight
    com.rokt.network.model.FontWeight.W900 -> FontWeight.W900.weight
}
