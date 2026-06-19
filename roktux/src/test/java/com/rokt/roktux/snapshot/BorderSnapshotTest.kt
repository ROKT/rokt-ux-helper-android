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
 * Visual regression for the `border` styling property.
 * The DCUI border supports solid and dashed strokes, four-way radii, and
 * per-state overrides — all of which are drawn through Compose's border
 * Modifier and can shift across Compose BOM releases.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class BorderSnapshotTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonString = BORDER_SOLID_THIN)
    fun testSolidThinBorder() = capture()

    @Test
    @DcuiNodeJson(jsonString = BORDER_SOLID_THICK)
    fun testSolidThickBorder() = capture()

    @Test
    @DcuiNodeJson(jsonString = BORDER_DASHED)
    fun testDashedBorder() = capture()

    @Test
    @DcuiNodeJson(jsonString = BORDER_LARGE_RADIUS)
    fun testLargeRadiusBorder() = capture()

    @Test
    @DcuiNodeJson(jsonString = BORDER_CIRCLE)
    fun testCircleBorder() = capture()

    @Test
    @DcuiNodeJson(jsonString = BORDER_PRESSED_STATE)
    @DcuiConfig(pseudoState = TestPseudoState(isPressed = true))
    fun testPressedStateBorder() = capture()

    @Test
    @DcuiNodeJson(jsonString = BORDER_DARK_MODE)
    @DcuiConfig(isDarkModeEnabled = true)
    fun testDarkModeBorderColor() = capture()

    private fun capture() {
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }

    private companion object {
        const val LABEL = """{"type":"BasicText","node":{"value":"Bordered","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":16,"textColor":{"light":"#222222","dark":"#FFFFFF"},"horizontalTextAlign":"center"}
        }}]}}}}"""

        const val BORDER_SOLID_THIN = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":100}},
            "spacing":{"margin":"32 32 32 32","padding":"8 8 8 8"},
            "container":{"alignItems":"center","justifyContent":"center"},
            "border":{"borderColor":{"light":"#0066CC"},"borderWidth":"1","borderRadius":0,"borderStyle":"solid"},
            "background":{"backgroundColor":{"light":"#FFFFFF"}}
        }}]}},"children":[$LABEL]}}"""

        const val BORDER_SOLID_THICK = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":100}},
            "spacing":{"margin":"32 32 32 32","padding":"8 8 8 8"},
            "container":{"alignItems":"center","justifyContent":"center"},
            "border":{"borderColor":{"light":"#CC3366"},"borderWidth":"6","borderRadius":4,"borderStyle":"solid"},
            "background":{"backgroundColor":{"light":"#FFFFFF"}}
        }}]}},"children":[$LABEL]}}"""

        const val BORDER_DASHED = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":100}},
            "spacing":{"margin":"32 32 32 32","padding":"8 8 8 8"},
            "container":{"alignItems":"center","justifyContent":"center"},
            "border":{"borderColor":{"light":"#22AA66"},"borderWidth":"2","borderRadius":8,"borderStyle":"dashed"},
            "background":{"backgroundColor":{"light":"#FFFFFF"}}
        }}]}},"children":[$LABEL]}}"""

        const val BORDER_LARGE_RADIUS = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":100}},
            "spacing":{"margin":"32 32 32 32","padding":"8 8 8 8"},
            "container":{"alignItems":"center","justifyContent":"center"},
            "border":{"borderColor":{"light":"#FF9933"},"borderWidth":"2","borderRadius":40,"borderStyle":"solid"},
            "background":{"backgroundColor":{"light":"#FFFFFF"}}
        }}]}},"children":[$LABEL]}}"""

        const val BORDER_CIRCLE = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":120},"height":{"type":"fixed","value":120}},
            "spacing":{"margin":"32 32 32 32"},
            "container":{"alignItems":"center","justifyContent":"center"},
            "border":{"borderColor":{"light":"#0066CC"},"borderWidth":"4","borderRadius":60,"borderStyle":"solid"},
            "background":{"backgroundColor":{"light":"#E6F0FF"}}
        }}]}},"children":[$LABEL]}}"""

        const val BORDER_PRESSED_STATE = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":100}},
            "spacing":{"margin":"32 32 32 32"},
            "container":{"alignItems":"center","justifyContent":"center"},
            "border":{"borderColor":{"light":"#999999"},"borderWidth":"2","borderRadius":8,"borderStyle":"solid"},
            "background":{"backgroundColor":{"light":"#FFFFFF"}}
        },"pressed":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":100}},
            "spacing":{"margin":"32 32 32 32"},
            "container":{"alignItems":"center","justifyContent":"center"},
            "border":{"borderColor":{"light":"#0066CC"},"borderWidth":"4","borderRadius":8,"borderStyle":"solid"},
            "background":{"backgroundColor":{"light":"#E6F0FF"}}
        }}]}},"children":[$LABEL]}}"""

        const val BORDER_DARK_MODE = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":100}},
            "spacing":{"margin":"32 32 32 32"},
            "container":{"alignItems":"center","justifyContent":"center"},
            "border":{"borderColor":{"light":"#222222","dark":"#FFFFFF"},"borderWidth":"2","borderRadius":8,"borderStyle":"solid"},
            "background":{"backgroundColor":{"light":"#FFFFFF","dark":"#000000"}}
        }}]}},"children":[$LABEL]}}"""
    }
}
