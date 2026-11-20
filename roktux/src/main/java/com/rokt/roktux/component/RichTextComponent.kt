package com.rokt.roktux.component

import android.graphics.Typeface
import android.text.style.BackgroundColorSpan
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.TextUiTransform
import com.rokt.roktux.component.button.LocalIsButtonContent
import com.rokt.roktux.utils.getValue
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import java.util.Locale

internal class RichTextComponent(private val modifierFactory: ModifierFactory) :
    ComposableComponent<LayoutSchemaUiModel.RichTextUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.RichTextUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val value = model.value.getValue(offerState, offerState.viewableItems) ?: return
        val textStyleUiState = modifierFactory.createTextStyle(
            text = value,
            textStyles = model.textStyles,
            breakpointIndex = breakpointIndex,
            isPressed = isPressed,
            isDarkModeEnabled = isDarkModeEnabled,
            conditionalTransitionTextStyling = model.conditionalTransitionTextStyling,
            offerState = offerState,
            isRichText = true,
            onEventSent = onEventSent,
        )
        val linkStyleUiState = modifierFactory.createTextStyle(
            text = value,
            textStyles = model.linkStyles,
            breakpointIndex = breakpointIndex,
            isPressed = isPressed,
            isDarkModeEnabled = isDarkModeEnabled,
            baseStyles = model.textStyles,
            offerState = offerState,
            isRichText = true,
            onEventSent = onEventSent,
        )

        if (value.isNotEmpty()) {
            val html = textStyleUiState.value
            val fontSize = textStyleUiState.textStyle.fontSize
            val urlSpanStyle = SpanStyle(
                color = linkStyleUiState.textStyle.color,
                fontSize = linkStyleUiState.textStyle.fontSize,
                fontFamily = linkStyleUiState.textStyle.fontFamily,
                fontWeight = linkStyleUiState.textStyle.fontWeight,
                baselineShift = linkStyleUiState.textStyle.baselineShift,
                fontStyle = linkStyleUiState.textStyle.fontStyle,
                letterSpacing = linkStyleUiState.textStyle.letterSpacing,
                textDecoration = linkStyleUiState.textStyle.textDecoration,
            )
            val textTransform = textStyleUiState.textTransform
            val linkTextTransform = linkStyleUiState.textTransform
            val openLinks = model.openLinks
            val currentOnEventSent by rememberUpdatedState(onEventSent)
            // Parsing the HTML is the costly part, so it runs again only when its inputs change,
            // not on every recomposition (for example a press state change).
            val content = remember(html, fontSize, urlSpanStyle, textTransform, linkTextTransform, openLinks) {
                html.asHTML(
                    fontSize = fontSize,
                    urlSpanStyle = urlSpanStyle,
                    textTransform = textTransform,
                    linkTextTransform = linkTextTransform,
                ) { url ->
                    currentOnEventSent(LayoutContract.LayoutEvent.UrlSelected(url, openLinks))
                }
            }
            Text(
                text = content,
                style = textStyleUiState.textStyle,
                modifier = modifierFactory
                    .createModifier(
                        modifierPropertiesList = model.ownModifiers,
                        conditionalTransitionModifier = model.conditionalTransitionModifiers,
                        breakpointIndex = breakpointIndex,
                        isPressed = isPressed,
                        isDarkModeEnabled = isDarkModeEnabled,
                        offerState = offerState,
                    )
                    .then(modifier)
                    // Keep linked paragraphs in reading order while letting button labels merge into their button.
                    .semantics(
                        mergeDescendants =
                        content.hasLinkAnnotations(0, content.length) && !LocalIsButtonContent.current,
                    ) {},
                maxLines = textStyleUiState.lineLimit,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun String.asHTML(
    fontSize: TextUnit,
    urlSpanStyle: SpanStyle,
    textTransform: TextUiTransform,
    linkTextTransform: TextUiTransform,
    onClick: (url: String) -> Unit,
) = buildAnnotatedString {
    val spanned = HtmlCompat.fromHtml(this@asHTML, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val spans = spanned.getSpans<Any>(0, spanned.length).filter { it !is BulletSpan }
    val spanStarts = IntArray(spans.size) { spanned.getSpanStart(spans[it]) }
    val spanEnds = IntArray(spans.size) { spanned.getSpanEnd(spans[it]) }
    val spannedString = transformSpanRanges(
        text = getTransformText(spanned.toString(), textTransform),
        starts = spanStarts,
        ends = spanEnds,
        transform = linkTextTransform,
    )
    spans.forEachIndexed { index, span ->
        val start = spanStarts[index]
        val end = spanEnds[index]
        when (span) {
            is RelativeSizeSpan -> span.spanStyle(fontSize)

            is StyleSpan -> span.spanStyle()

            is UnderlineSpan -> span.spanStyle()

            is BackgroundColorSpan -> span.spanStyle()

            is ForegroundColorSpan -> span.spanStyle()

            is StrikethroughSpan -> span.spanStyle()

            is SuperscriptSpan -> span.spanStyle()

            is SubscriptSpan -> span.spanStyle()

            is URLSpan -> {
                addLink(
                    LinkAnnotation.Url(
                        url = span.url,
                        styles = TextLinkStyles(style = urlSpanStyle),
                        linkInteractionListener = {
                            onClick.invoke(span.url)
                        },
                    ),
                    start,
                    end,
                )
                null
            }

            else -> {
                null
            }
        }?.let { spanStyle ->
            addStyle(spanStyle, start, end)
        }
    }
    append(spannedString)
}

private fun UnderlineSpan.spanStyle(): SpanStyle = SpanStyle(textDecoration = TextDecoration.Underline)

private fun ForegroundColorSpan.spanStyle(): SpanStyle = SpanStyle(color = Color(foregroundColor))

private fun BackgroundColorSpan.spanStyle(): SpanStyle = SpanStyle(background = Color(backgroundColor))

private fun StrikethroughSpan.spanStyle(): SpanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough)

private fun RelativeSizeSpan.spanStyle(fontSize: TextUnit): SpanStyle = SpanStyle(
    fontSize = if (fontSize.isSpecified && fontSize.value.isFinite()) {
        // Base size is known, so scale it directly to an absolute size.
        (fontSize.value * sizeChange).sp
    } else {
        // No usable base size (e.g. no surrounding font size was set). Express the change
        // relative to whatever size Compose ultimately resolves for this text instead of
        // multiplying an unspecified/non-finite value, which would produce a NaN/infinite
        // Sp that Compose's text layout cannot handle.
        sizeChange.em
    },
)

private fun StyleSpan.spanStyle(): SpanStyle? = when (style) {
    Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)

    Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)

    Typeface.BOLD_ITALIC -> SpanStyle(
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
    )

    else -> null
}

private fun SubscriptSpan.spanStyle(): SpanStyle = SpanStyle(baselineShift = BaselineShift.Subscript)

private fun SuperscriptSpan.spanStyle(): SpanStyle = SpanStyle(baselineShift = BaselineShift.Superscript)

private fun getTransformText(text: String, textTransform: TextUiTransform?): String = textTransform?.run {
    when (textTransform) {
        TextUiTransform.Capitalize -> text.split(" ").joinToString(" ") { value ->
            value.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        TextUiTransform.Lowercase -> text.lowercase()

        TextUiTransform.Uppercase -> text.uppercase()

        TextUiTransform.None -> text
    }
} ?: text

/**
 * Applies [transform] to every character covered by at least one `[starts[i], ends[i])` range, in
 * one pass over [text]. The result matches transforming each range in turn, but the cost grows
 * with the text length plus the number of ranges, not with their product, so text with many
 * small spans cannot block the main thread.
 */
internal fun transformSpanRanges(text: String, starts: IntArray, ends: IntArray, transform: TextUiTransform?): String {
    if (transform == null || transform == TextUiTransform.None || starts.isEmpty()) return text
    val length = text.length
    // depthChange[i] is how many ranges open at i minus how many close at i.
    val depthChange = IntArray(length + 1)
    val rangeStart = BooleanArray(length)
    for (index in starts.indices) {
        val start = starts[index].coerceIn(0, length)
        val end = ends[index].coerceIn(start, length)
        if (start < end) {
            depthChange[start]++
            depthChange[end]--
            rangeStart[start] = true
        }
    }
    val covered = BooleanArray(length)
    var depth = 0
    for (position in 0 until length) {
        depth += depthChange[position]
        covered[position] = depth > 0
    }
    val result = StringBuilder(length)
    var runStart = 0
    while (runStart < length) {
        if (!covered[runStart]) {
            result.append(text[runStart])
            runStart++
            continue
        }
        // A maximal run of covered characters.
        var runEnd = runStart
        while (runEnd < length && covered[runEnd]) runEnd++
        if (transform == TextUiTransform.Capitalize) {
            // Transforming a range capitalizes its first character and every character after a
            // space inside it. A covered character that starts no range has a range that started
            // earlier, so the character before it is in the same range.
            for (position in runStart until runEnd) {
                val char = text[position]
                val capitalize = rangeStart[position] || (position > runStart && text[position - 1] == ' ')
                if (capitalize && char.isLowerCase()) {
                    result.append(char.titlecase(Locale.getDefault()))
                } else {
                    result.append(char)
                }
            }
        } else {
            result.append(getTransformText(text.substring(runStart, runEnd), transform))
        }
        runStart = runEnd
    }
    return result.toString()
}
