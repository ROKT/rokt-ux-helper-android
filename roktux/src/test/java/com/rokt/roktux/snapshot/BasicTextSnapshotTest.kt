package com.rokt.roktux.snapshot

import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
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
 * Visual regression coverage for BasicTextComponent across the full
 * text styling matrix. Drives the Material3 `Text` composable + Compose
 * text APIs that are reshuffled by Compose BOM upgrades.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class BasicTextSnapshotTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonString = """{"type":"BasicText","node":{"value":"Default text"}}""")
    fun testDefault() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Bold weight","styles":{"elements":{"own":[{"default":{"text":{"fontSize":20,"fontWeight":"700","textColor":{"light":"#222222"}}}}]}}}}""",
    )
    fun testBoldWeight() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Light weight 200","styles":{"elements":{"own":[{"default":{"text":{"fontSize":20,"fontWeight":"200","textColor":{"light":"#222222"}}}}]}}}}""",
    )
    fun testLightWeight() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Italic text","styles":{"elements":{"own":[{"default":{"text":{"fontSize":20,"fontStyle":"italic","textColor":{"light":"#222222"}}}}]}}}}""",
    )
    fun testItalicStyle() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Underlined text","styles":{"elements":{"own":[{"default":{"text":{"fontSize":20,"textDecoration":"underline","textColor":{"light":"#222222"}}}}]}}}}""",
    )
    fun testUnderlineDecoration() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Strikethrough","styles":{"elements":{"own":[{"default":{"text":{"fontSize":20,"textDecoration":"strike-through","textColor":{"light":"#222222"}}}}]}}}}""",
    )
    fun testStrikethroughDecoration() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Right aligned text spanning multiple lines so that alignment is observable in the snapshot","styles":{"elements":{"own":[{"default":{"text":{"fontSize":18,"horizontalTextAlign":"right","textColor":{"light":"#222222"}},"dimension":{"width":{"type":"fixed","value":300}}}}]}}}}""",
    )
    fun testHorizontalAlignmentRight() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Center aligned text spanning multiple lines so that alignment is observable in the snapshot","styles":{"elements":{"own":[{"default":{"text":{"fontSize":18,"horizontalTextAlign":"center","textColor":{"light":"#222222"}},"dimension":{"width":{"type":"fixed","value":300}}}}]}}}}""",
    )
    fun testHorizontalAlignmentCenter() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Wide letter spacing","styles":{"elements":{"own":[{"default":{"text":{"fontSize":18,"letterSpacing":4,"textColor":{"light":"#222222"}}}}]}}}}""",
    )
    fun testLetterSpacing() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Line one that is long enough to wrap onto multiple lines for line height verification purposes here","styles":{"elements":{"own":[{"default":{"text":{"fontSize":18,"lineHeight":40,"textColor":{"light":"#222222"}},"dimension":{"width":{"type":"fixed","value":260}}}}]}}}}""",
    )
    fun testLineHeight() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"On a dark background","styles":{"elements":{"own":[{"default":{"text":{"fontSize":20,"textColor":{"light":"#FFFFFF","dark":"#FFFFFF"}},"background":{"backgroundColor":{"light":"#222222","dark":"#222222"}},"spacing":{"padding":"16 16 16 16"}}}]}}}}""",
    )
    fun testTextOnDarkBackground() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Padding test","styles":{"elements":{"own":[{"default":{"text":{"fontSize":18,"textColor":{"light":"#FFFFFF"}},"background":{"backgroundColor":{"light":"#0066CC"}},"spacing":{"padding":"4 24 32 48"}}}]}}}}""",
    )
    fun testAsymmetricPadding() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Border and corner","styles":{"elements":{"own":[{"default":{"text":{"fontSize":18,"textColor":{"light":"#222222"}},"border":{"borderColor":{"light":"#FF6699"},"borderWidth":"3","borderRadius":12},"spacing":{"padding":"12 16 12 16"}}}]}}}}""",
    )
    fun testBorderRadiusAndWidth() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Fixed size","styles":{"elements":{"own":[{"default":{"text":{"fontSize":16,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#222222"}},"dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":100}}}}]}}}}""",
    )
    fun testFixedDimensions() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Pressed state","styles":{"elements":{"own":[{"default":{"text":{"fontSize":18,"textColor":{"light":"#222222"}},"background":{"backgroundColor":{"light":"#FFFFFF"}},"spacing":{"padding":"12 16 12 16"}},"pressed":{"text":{"fontSize":18,"textColor":{"light":"#FFFFFF"}},"background":{"backgroundColor":{"light":"#0066CC"}},"spacing":{"padding":"12 16 12 16"}}}]}}}}""",
    )
    @DcuiConfig(pseudoState = TestPseudoState(isPressed = true))
    fun testPressedState() = capture()

    @Test
    @DcuiNodeJson(
        jsonString = """{"type":"BasicText","node":{"value":"Dark mode text","styles":{"elements":{"own":[{"default":{"text":{"fontSize":18,"textColor":{"light":"#222222","dark":"#FFFFFF"}},"background":{"backgroundColor":{"light":"#FFFFFF","dark":"#000000"}},"spacing":{"padding":"12 16 12 16"}}}]}}}}""",
    )
    @DcuiConfig(isDarkModeEnabled = true)
    fun testDarkMode() = capture()

    private fun capture() {
        composeTestRule.onRoot().captureRoboImage()
    }
}
