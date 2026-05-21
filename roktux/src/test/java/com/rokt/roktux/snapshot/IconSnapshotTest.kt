package com.rokt.roktux.snapshot

import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual regression for the StaticIcon component. The test rule wires the
 * production rokt_icons.otf into LocalFontFamilyProvider so each test
 * renders the real glyph mapped to the configured `name`, not a plain-text
 * fallback.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class IconSnapshotTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"StaticIcon","node":{"name":"PercentIcon","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":40,"textColor":{"light":"#FF0000","dark":"#0000FF"}}
        }}]}}}}""",
    )
    fun testStaticIconBasic() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"StaticIcon","node":{"name":"Car","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":40,"textColor":{"light":"#FFFFFF"}},
            "background":{"backgroundColor":{"light":"#0066CC"}},
            "spacing":{"padding":"16 24 16 24"},
            "border":{"borderRadius":12}
        }}]}}}}""",
    )
    fun testStaticIconWithBackgroundAndRoundedCorners() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"StaticIcon","node":{"name":"Star","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":40,"textColor":{"light":"#222222","dark":"#FFFFFF"}},
            "background":{"backgroundColor":{"light":"#FFFFFF","dark":"#000000"}},
            "spacing":{"padding":"16 16 16 16"}
        }}]}}}}""",
    )
    @DcuiConfig(isDarkModeEnabled = true)
    fun testStaticIconDarkMode() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"StaticIcon","node":{"name":"Close","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":60,"textColor":{"light":"#CC3366"}},
            "border":{"borderColor":{"light":"#CC3366"},"borderWidth":"3","borderRadius":50},
            "spacing":{"padding":"12 12 12 12"}
        }}]}}}}""",
    )
    fun testStaticIconWithCircularBorder() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"StaticIcon","node":{"name":"Dollar","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":40,"textColor":{"light":"#22AA66"}},
            "spacing":{"padding":"12 12 12 12"}
        }}]}}}}""",
    )
    fun testStaticIconDollar() = capture()

    // ---- Glyph variety coverage ----
    // The rokt_icons.otf font defines ~86 ligature names. The cases below sample a
    // visually-distinct cross-section (curves, angles, composite shapes, monolines,
    // multi-stroke glyphs) so that a Compose text-layout regression affecting only a
    // subset of glyphs would still surface here.

    @Test
    @DcuiNodeJson(jsonString = """{"type":"StaticIcon","node":{"name":"Heart",$GLYPH_STYLES}}""")
    fun testGlyphHeart() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"StaticIcon","node":{"name":"ShoppingCart",$GLYPH_STYLES}}""")
    fun testGlyphShoppingCart() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"StaticIcon","node":{"name":"ChevronRight",$GLYPH_STYLES}}""")
    fun testGlyphChevronRight() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"StaticIcon","node":{"name":"CircleCheck",$GLYPH_STYLES}}""")
    fun testGlyphCircleCheck() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"StaticIcon","node":{"name":"Lock",$GLYPH_STYLES}}""")
    fun testGlyphLock() = capture()

    // Glyph name "PlaneDepature" preserves the typo from `rokt_icons.otf`'s ligature
    // table — changing it would break the font lookup. (Production marketing layouts
    // also reference the typo'd name.)
    @Test
    @DcuiNodeJson(jsonString = """{"type":"StaticIcon","node":{"name":"PlaneDepature",$GLYPH_STYLES}}""")
    fun testGlyphPlane() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"StaticIcon","node":{"name":"Gift",$GLYPH_STYLES}}""")
    fun testGlyphGift() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"StaticIcon","node":{"name":"Mail",$GLYPH_STYLES}}""")
    fun testGlyphMail() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"StaticIcon","node":{"name":"ThumbUp",$GLYPH_STYLES}}""")
    fun testGlyphThumbUp() = capture()

    @Test
    @DcuiNodeJson(jsonString = """{"type":"StaticIcon","node":{"name":"CreditCard",$GLYPH_STYLES}}""")
    fun testGlyphCreditCard() = capture()

    private fun capture() {
        composeTestRule.onRoot().captureRoboImage()
    }

    private companion object {
        const val GLYPH_STYLES = """"styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":48,"textColor":{"light":"#222222"}},
            "spacing":{"padding":"16 16 16 16"}
        }}]}}"""
    }
}
