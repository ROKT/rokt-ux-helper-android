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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.TextUiTransform
import com.rokt.roktux.utils.getValue
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import java.util.Locale

internal class RichTextComponent(
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.RichTextUiModel> {

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
        )
        val linkStyleUiState = modifierFactory.createTextStyle(
            text = value,
            textStyles = model.linkStyles,
            breakpointIndex = breakpointIndex,
            isPressed = isPressed,
            isDarkModeEnabled = isDarkModeEnabled,
            baseStyles = model.textStyles,
            offerState = offerState,
        )

        if (value.isNotEmpty()) {
            val content = textStyleUiState.value.asHTML(
                fontSize = textStyleUiState.textStyle.fontSize,
                urlSpanStyle = SpanStyle(
                    color = linkStyleUiState.textStyle.color,
                    fontSize = linkStyleUiState.textStyle.fontSize,
                    fontFamily = linkStyleUiState.textStyle.fontFamily,
                    fontWeight = linkStyleUiState.textStyle.fontWeight,
                    baselineShift = linkStyleUiState.textStyle.baselineShift,
                    fontStyle = linkStyleUiState.textStyle.fontStyle,
                    letterSpacing = linkStyleUiState.textStyle.letterSpacing,
                    textDecoration = linkStyleUiState.textStyle.textDecoration,
                ),
                linkStyleUiState.textTransform,
            ) { url ->
                onEventSent(LayoutContract.LayoutEvent.UrlSelected(url, model.openLinks))
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
                    .then(modifier),
                maxLines = textStyleUiState.lineLimit,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun String.asHTML(
    fontSize: TextUnit,
    urlSpanStyle: SpanStyle,
    textTransform: TextUiTransform,
    onClick: (url: String) -> Unit,
) = buildAnnotatedString {
    val spanned = HtmlCompat.fromHtml(this@asHTML, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val spans = spanned.getSpans<Any>(0, spanned.length)
    var spannedString = spanned.toString()
    spans.filter { it !is BulletSpan }.forEach { span ->
        val start = spanned.getSpanStart(span)
        val end = spanned.getSpanEnd(span)
        val spanString = getTransformText(spannedString.substring(start, end), textTransform)
        spannedString = spannedString.replaceRange(start, end, spanString)
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
                        linkInteractionListener = {
                            onClick.invoke(span.url)
                        },
                    ),
                    start,
                    end,
                )
                urlSpanStyle
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

private fun RelativeSizeSpan.spanStyle(fontSize: TextUnit): SpanStyle =
    SpanStyle(fontSize = (fontSize.value * sizeChange).sp)

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
