package com.rokt.roktux.snapshot

import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.font.FontFamily
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.captureScreenRoboImage
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual regression coverage for the consumer-facing custom font registration
 * API. Consumers register fonts via `RoktUxConfig.Builder().composeFontMap(...)`,
 * and DCUI JSON references them by name (`"fontFamily": "myBrandFont"`). The
 * lookup happens in `ModifierFactory` and the result flows through `LocalFontFamilyProvider`.
 *
 * Compose BOM upgrades have touched the `androidx.compose.ui.text.font`
 * package surface area (FontFamily resolution, Font loading, FontWeight
 * matching) — these snapshots catch silent regressions in any of that
 * machinery, plus the SDK-specific glue in `ModifierFactory.kt:1169-1183`
 * (the try/catch that falls back to FontFamily.Default on missing names).
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class CustomFontSnapshotTest :
    BaseDcuiEspressoTest(
        extraFontMap = persistentMapOf(
            "test-serif" to FontFamily.Serif,
            "test-monospace" to FontFamily.Monospace,
            "test-cursive" to FontFamily.Cursive,
            "test-sansserif" to FontFamily.SansSerif,
        ),
    ) {

    @Test
    @DcuiNodeJson(jsonString = TEXT_SERIF)
    fun testCustomSerifFontApplied() = capture()

    @Test
    @DcuiNodeJson(jsonString = TEXT_MONOSPACE)
    fun testCustomMonospaceFontApplied() = capture()

    @Test
    @DcuiNodeJson(jsonString = TEXT_CURSIVE)
    fun testCustomCursiveFontApplied() = capture()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_MIXED_FONTS)
    fun testMixedFontFamiliesInColumn() = capture()

    @Test
    @DcuiNodeJson(jsonString = TEXT_UNKNOWN_FONT)
    fun testUnknownFontFamilyFallsBackToDefault() = capture()

    @Test
    @DcuiNodeJson(jsonString = TEXT_SERIF_BOLD)
    fun testCustomFontWithBoldWeightResolution() = capture()

    @Test
    @DcuiNodeJson(jsonString = RICH_TEXT_SERIF)
    fun testCustomFontInRichText() = capture()

    @Test
    @DcuiNodeJson(jsonString = ROW_CUSTOM_FONT_PLUS_ICON)
    fun testCustomFontCoexistsWithIconFont() = capture()

    @Test
    @DcuiNodeJson(jsonString = BOTTOM_SHEET_CUSTOM_FONT)
    fun testCustomFontInsideBottomSheet() = captureScreen()

    private fun capture() {
        composeTestRule.onRoot().captureRoboImage()
    }

    private fun captureScreen() {
        // BottomSheet renders into a Popup window outside the compose root;
        // wait for the appear animation to settle before capturing.
        composeTestRule.waitForIdle()
        captureScreenRoboImage()
    }

    private companion object {
        const val LABEL = "The quick brown fox jumps over the lazy dog"

        const val TEXT_SERIF = """{"type":"BasicText","node":{"value":"$LABEL","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":20,"fontFamily":"test-serif","textColor":{"light":"#222222"}},
            "spacing":{"padding":"16 16 16 16"},
            "dimension":{"width":{"type":"fixed","value":340}}
        }}]}}}}"""

        const val TEXT_MONOSPACE = """{"type":"BasicText","node":{"value":"$LABEL","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":20,"fontFamily":"test-monospace","textColor":{"light":"#222222"}},
            "spacing":{"padding":"16 16 16 16"},
            "dimension":{"width":{"type":"fixed","value":340}}
        }}]}}}}"""

        const val TEXT_CURSIVE = """{"type":"BasicText","node":{"value":"$LABEL","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":20,"fontFamily":"test-cursive","textColor":{"light":"#222222"}},
            "spacing":{"padding":"16 16 16 16"},
            "dimension":{"width":{"type":"fixed","value":340}}
        }}]}}}}"""

        // DCUI says "fontFamily": "nonexistent" — `ModifierFactory` should catch
        // the IllegalStateException and fall back to FontFamily.Default.
        // Expected visual: same default rendering as BasicTextSnapshotTest.testDefault.
        const val TEXT_UNKNOWN_FONT = """{"type":"BasicText","node":{"value":"$LABEL — should fall back to default","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":20,"fontFamily":"this-font-is-not-registered","textColor":{"light":"#222222"}},
            "spacing":{"padding":"16 16 16 16"},
            "dimension":{"width":{"type":"fixed","value":340}}
        }}]}}}}"""

        // fontWeight=700 against a custom-registered Serif. The system serif
        // typeface includes a Bold variant — verifies Compose's FontFamily
        // matcher resolves to the bold glyph from within the mapped family.
        const val TEXT_SERIF_BOLD = """{"type":"BasicText","node":{"value":"$LABEL — bold","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":20,"fontFamily":"test-serif","fontWeight":"700","textColor":{"light":"#222222"}},
            "spacing":{"padding":"16 16 16 16"},
            "dimension":{"width":{"type":"fixed","value":340}}
        }}]}}}}"""

        const val COLUMN_MIXED_FONTS = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "container":{"gap":"4"},
            "spacing":{"padding":"16 16 16 16"},
            "dimension":{"width":{"type":"fixed","value":340}}
        }}]}},"children":[
            {"type":"BasicText","node":{"value":"Serif: $LABEL","styles":{"elements":{"own":[{"default":{
                "text":{"fontSize":16,"fontFamily":"test-serif","textColor":{"light":"#222222"}}
            }}]}}}},
            {"type":"BasicText","node":{"value":"Sans-serif: $LABEL","styles":{"elements":{"own":[{"default":{
                "text":{"fontSize":16,"fontFamily":"test-sansserif","textColor":{"light":"#222222"}}
            }}]}}}},
            {"type":"BasicText","node":{"value":"Monospace: $LABEL","styles":{"elements":{"own":[{"default":{
                "text":{"fontSize":16,"fontFamily":"test-monospace","textColor":{"light":"#222222"}}
            }}]}}}}
        ]}}"""

        const val RICH_TEXT_SERIF = """{"type":"RichText","node":{"value":"Mix of <b>bold</b>, <i>italic</i>, <u>underline</u> and an <a href='https://rokt.com'>inline link</a> rendered against the custom Serif family.","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":18,"fontFamily":"test-serif","textColor":{"light":"#222222"},"lineHeight":24,"horizontalTextAlign":"left"},
            "spacing":{"padding":"16 16 16 16"},
            "dimension":{"width":{"type":"fixed","value":340}}
        }}],"link":[{"default":{
            "text":{"textColor":{"light":"#0066CC"},"textDecoration":"underline","fontWeight":"700"}
        }}]}}}}"""

        // Row with a custom-font BasicText next to a built-in rokt-icons StaticIcon.
        // If the extraFontMap merge clobbered ROKT_ICONS_FONT_FAMILY, the icon
        // would render as the literal name "Star" instead of a glyph.
        const val ROW_CUSTOM_FONT_PLUS_ICON = """{"type":"Row","node":{"styles":{"elements":{"own":[{"default":{
            "container":{"alignItems":"center","gap":"12"},
            "spacing":{"padding":"16 16 16 16"}
        }}]}},"children":[
            {"type":"StaticIcon","node":{"name":"Star","styles":{"elements":{"own":[{"default":{
                "text":{"fontSize":32,"textColor":{"light":"#FF9933"}}
            }}]}}}},
            {"type":"BasicText","node":{"value":"Star rated — Serif","styles":{"elements":{"own":[{"default":{
                "text":{"fontSize":18,"fontFamily":"test-serif","textColor":{"light":"#222222"}}
            }}]}}}}
        ]}}"""

        const val BOTTOM_SHEET_CUSTOM_FONT = """{"type":"BottomSheet","node":{
            "allowBackdropToClose":true,
            "styles":{"elements":{"own":[{"default":{
                "background":{"backgroundColor":{"light":"#FFFFFF"}},
                "spacing":{"padding":"24 24 24 24"},
                "dimension":{"height":{"type":"fixed","value":200}}
            }}],"wrapper":[{"default":{
                "background":{"backgroundColor":{"light":"#660E0A13"}}
            }}]}},
            "children":[{"type":"BasicText","node":{"value":"Custom font survives the popup window","styles":{"elements":{"own":[{"default":{
                "text":{"fontSize":18,"fontFamily":"test-serif","textColor":{"light":"#171717"},"horizontalTextAlign":"center"}
            }}]}}}}]
        }}"""
    }
}
