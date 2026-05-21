package com.rokt.roktux.snapshot

import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.TestPseudoState
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual regression coverage for RichTextComponent across the full set of HTML tags
 * mapped to Compose AnnotatedString spans. Compose BOM upgrades have historically
 * touched the `androidx.compose.ui.text` package (LinkAnnotation, SpanStyle behavior),
 * so any change in how these spans render must surface here.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class RichTextSnapshotTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"Plain unformatted text",$DEFAULT_STYLES}}""")
    fun testPlainText() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"This is <b>bold</b> text",$DEFAULT_STYLES}}""")
    fun testBoldTag() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"This is <strong>strong</strong> text",$DEFAULT_STYLES}}""")
    fun testStrongTag() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"This is <i>italic</i> text",$DEFAULT_STYLES}}""")
    fun testItalicTag() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"This is <em>emphasised</em> text",$DEFAULT_STYLES}}""")
    fun testEmphasisTag() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"This is <b><i>bold italic</i></b> text",$DEFAULT_STYLES}}""")
    fun testBoldItalicNested() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"This is <u>underlined</u> text",$DEFAULT_STYLES}}""")
    fun testUnderlineTag() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"This is <s>strikethrough</s> text",$DEFAULT_STYLES}}""")
    fun testStrikeShortTag() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"This is <strike>strike</strike> text",$DEFAULT_STYLES}}""")
    fun testStrikeLongTag() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"This is <del>deleted</del> text",$DEFAULT_STYLES}}""")
    fun testDelTag() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"H<sub>2</sub>O molecule",$DEFAULT_STYLES}}""")
    fun testSubscriptTag() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"E = mc<sup>2</sup>",$DEFAULT_STYLES}}""")
    fun testSuperscriptTag() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"<font color='#FF0000'>Red</font> and <font color='#00AA00'>Green</font>",$DEFAULT_STYLES}}""")
    fun testFontColorTag() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"Try <big>big</big> and <small>small</small> sizing",$DEFAULT_STYLES}}""")
    fun testRelativeSizeTags() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"First line<br/>Second line<br/>Third line",$DEFAULT_STYLES}}""")
    fun testLineBreakTag() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"Visit <a href='https://rokt.com'>rokt.com</a> today",$DEFAULT_STYLES_WITH_LINK}}""")
    fun testAnchorTagWithLinkStyles() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"Read the <a href='https://rokt.com/terms'>Terms</a> and <a href='https://rokt.com/privacy'>Privacy Policy</a>",$DEFAULT_STYLES_WITH_LINK}}""")
    fun testMultipleAnchorTags() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"Mix of <b>bold</b>, <i>italic</i>, <u>underline</u>, <s>strike</s>, <font color='#0066CC'>color</font> and <a href='https://rokt.com'>link</a> all together",$DEFAULT_STYLES_WITH_LINK}}""")
    fun testCombinedTags() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"powered by <a href='https://rokt.com'>rokt</a>",$STYLES_WITH_CAPITALIZE}}""")
    fun testCapitalizeTransform() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"powered by <a href='https://rokt.com'>rokt</a>",$STYLES_WITH_UPPERCASE}}""")
    fun testUppercaseTransform() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"POWERED BY <a href='https://rokt.com'>ROKT</a>",$STYLES_WITH_LOWERCASE}}""")
    fun testLowercaseTransform() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"powered by <a href='https://rokt.com'>rokt</a>",$STYLES_LINK_UPPERCASE_ONLY}}""")
    fun testLinkOnlyUppercaseTransform() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"Long form rich text rendering — used to validate line wrapping, line height, and letter spacing on <b>bold</b> and <i>italic</i> spans across multiple lines so that any regression in Compose text layout would be visible here.",$DEFAULT_STYLES}}""")
    @DcuiConfig(componentTag = DCUI_COMPONENT_TAG)
    fun testWrappedMultiLine() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"Pressed state link <a href='https://rokt.com'>click me</a>",$DEFAULT_STYLES_WITH_LINK}}""")
    @DcuiConfig(pseudoState = TestPseudoState(isPressed = true))
    fun testPressedState() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"<a href='https://rokt.com'>Dark mode link</a>",$DEFAULT_STYLES_WITH_LINK_DARK}}""")
    @DcuiConfig(isDarkModeEnabled = true)
    fun testDarkModeLink() = capture()

    // ---- Combination cases (high-value because most production copy mixes features) ----

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"<b>Bold</b> and <i>italic</i> text gets uppercased entirely",$STYLES_WITH_UPPERCASE}}""")
    fun testCombinedBoldItalicWithUppercaseTransform() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"Read the <a href='https://rokt.com/terms'>terms</a> and <a href='https://rokt.com/privacy'>privacy policy</a> carefully",$STYLES_WITH_CAPITALIZE}}""")
    fun testMultipleLinksWithCapitalizeTransform() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"<font color='#FF0000'>Important:</font> H<sub>2</sub>O at <b>100°C</b><sup>1</sup> per <a href='https://example.com'>spec</a>",$DEFAULT_STYLES_WITH_LINK}}""")
    fun testColorWithSubSupBoldAndLink() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"Line one with <b>bold span</b>.<br/>Line two with an <a href='https://rokt.com'>inline link</a> that should wrap.<br/>Line three plain.",$DEFAULT_STYLES_WITH_LINK}}""")
    fun testMultiLineWithSpansAndLink() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"A long single-line of rich text containing a <b>bold</b> and <i>italic</i> span used to verify ellipsis truncation behaves consistently across Compose BOM releases","styles":{"elements":{"own":[{"default":{"text":{"fontSize":16,"textColor":{"light":"#222222"},"lineLimit":1,"horizontalTextAlign":"left"},"dimension":{"width":{"type":"fixed","value":280}},"spacing":{"padding":"12 12 12 12"}}}]}}}}""")
    fun testLineLimitEllipsisTruncation() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"<u>Underlined</u> <s>strike</s> <b>bold</b> <i>italic</i> on a tinted background row",$STYLES_ON_TINTED_BG}}""")
    fun testCombinedDecorationsOnTintedBackground() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"RichText","node":{"value":"Pressed link state should darken the entire <a href='https://rokt.com'>linked phrase</a> only",$DEFAULT_STYLES_WITH_LINK_PRESSED}}""")
    @DcuiConfig(pseudoState = TestPseudoState(isPressed = true))
    fun testLinkPressedStateStyling() = capture()

    private fun capture() {
        composeTestRule.onRoot().captureRoboImage()
    }

    private companion object {
        const val DEFAULT_STYLES = """"styles":{"elements":{"own":[{"default":{"text":{"fontSize":16,"fontWeight":"400","textColor":{"light":"#222222","dark":"#FFFFFF"},"lineHeight":22,"horizontalTextAlign":"left"},"spacing":{"padding":"12 12 12 12"}}}]}}"""

        const val DEFAULT_STYLES_WITH_LINK = """"styles":{"elements":{"own":[{"default":{"text":{"fontSize":16,"fontWeight":"400","textColor":{"light":"#222222","dark":"#FFFFFF"},"lineHeight":22,"horizontalTextAlign":"left"},"spacing":{"padding":"12 12 12 12"}}}],"link":[{"default":{"text":{"textColor":{"light":"#0066CC","dark":"#66AAFF"},"textDecoration":"underline","fontWeight":"700"}}}]}}"""

        const val DEFAULT_STYLES_WITH_LINK_DARK = """"styles":{"elements":{"own":[{"default":{"text":{"fontSize":18,"fontWeight":"400","textColor":{"light":"#222222","dark":"#EEEEEE"},"lineHeight":24,"horizontalTextAlign":"left"},"spacing":{"padding":"12 12 12 12"},"background":{"backgroundColor":{"light":"#FFFFFF","dark":"#1A1A1A"}}}}],"link":[{"default":{"text":{"textColor":{"light":"#0066CC","dark":"#66AAFF"},"textDecoration":"underline","fontWeight":"700"}}}]}}"""

        const val STYLES_WITH_CAPITALIZE = """"styles":{"elements":{"own":[{"default":{"text":{"fontSize":16,"textColor":{"light":"#222222"},"horizontalTextAlign":"left","textTransform":"capitalize"},"spacing":{"padding":"12 12 12 12"}}}],"link":[{"default":{"text":{"textColor":{"light":"#0066CC"},"textDecoration":"underline"}}}]}}"""

        const val STYLES_WITH_UPPERCASE = """"styles":{"elements":{"own":[{"default":{"text":{"fontSize":16,"textColor":{"light":"#222222"},"horizontalTextAlign":"left","textTransform":"uppercase"},"spacing":{"padding":"12 12 12 12"}}}],"link":[{"default":{"text":{"textColor":{"light":"#0066CC"},"textDecoration":"underline"}}}]}}"""

        const val STYLES_WITH_LOWERCASE = """"styles":{"elements":{"own":[{"default":{"text":{"fontSize":16,"textColor":{"light":"#222222"},"horizontalTextAlign":"left","textTransform":"lowercase"},"spacing":{"padding":"12 12 12 12"}}}],"link":[{"default":{"text":{"textColor":{"light":"#0066CC"},"textDecoration":"underline"}}}]}}"""

        const val STYLES_LINK_UPPERCASE_ONLY = """"styles":{"elements":{"own":[{"default":{"text":{"fontSize":16,"textColor":{"light":"#222222"},"horizontalTextAlign":"left"},"spacing":{"padding":"12 12 12 12"}}}],"link":[{"default":{"text":{"textColor":{"light":"#0066CC"},"textDecoration":"underline","textTransform":"uppercase"}}}]}}"""

        const val STYLES_ON_TINTED_BG = """"styles":{"elements":{"own":[{"default":{"text":{"fontSize":16,"fontWeight":"400","textColor":{"light":"#222222"},"lineHeight":22,"horizontalTextAlign":"left"},"background":{"backgroundColor":{"light":"#FFF7E6"}},"spacing":{"padding":"12 12 12 12"}}}]}}"""

        const val DEFAULT_STYLES_WITH_LINK_PRESSED = """"styles":{"elements":{"own":[{"default":{"text":{"fontSize":16,"textColor":{"light":"#222222"},"horizontalTextAlign":"left"},"spacing":{"padding":"12 12 12 12"}},"pressed":{"text":{"fontSize":16,"textColor":{"light":"#222222"},"horizontalTextAlign":"left"},"spacing":{"padding":"12 12 12 12"}}}],"link":[{"default":{"text":{"textColor":{"light":"#0066CC"},"textDecoration":"underline","fontWeight":"700"}},"pressed":{"text":{"textColor":{"light":"#003F80"},"textDecoration":"underline","fontWeight":"700"}}}]}}"""
    }
}
