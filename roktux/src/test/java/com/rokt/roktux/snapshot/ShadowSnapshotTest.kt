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
 * Visual regression coverage for the `shadow` container styling property.
 * Maps to Compose `Modifier.shadow` (or graphics-layer shadow), which has
 * shifted noticeably across Compose BOM releases — both render quality
 * and the API contract. Each test exercises a different combination of
 * offset, blur, spread, and color so any regression in shadow rendering
 * surfaces here.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class ShadowSnapshotTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonString = SHADOW_SOFT_OFFSET)
    fun testSoftShadowWithOffset() = capture()

    @Test
    @DcuiNodeJson(jsonString = SHADOW_HARD_NO_BLUR)
    fun testHardShadowNoBlur() = capture()

    @Test
    @DcuiNodeJson(jsonString = SHADOW_LARGE_BLUR_NO_OFFSET)
    fun testLargeBlurNoOffset() = capture()

    @Test
    @DcuiNodeJson(jsonString = SHADOW_COLOURED)
    fun testColouredShadow() = capture()

    @Test
    @DcuiNodeJson(jsonString = SHADOW_NEGATIVE_OFFSET)
    fun testNegativeOffsetShadow() = capture()

    @Test
    @DcuiNodeJson(jsonString = SHADOW_WITH_BORDER_RADIUS)
    fun testShadowOnRoundedCard() = capture()

    @Test
    @DcuiNodeJson(jsonString = SHADOW_DARK_MODE)
    @DcuiConfig(isDarkModeEnabled = true)
    fun testShadowDarkModeColor() = capture()

    private fun capture() {
        composeTestRule.onRoot().captureRoboImage()
    }

    private companion object {
        const val CARD_CHILD = """{"type":"BasicText","node":{"value":"Card","styles":{"elements":{"own":[{"default":{
            "text":{"fontSize":18,"fontWeight":"700","textColor":{"light":"#222222","dark":"#FFFFFF"},"horizontalTextAlign":"center"}
        }}]}}}}"""

        const val SHADOW_SOFT_OFFSET = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":120}},
            "spacing":{"margin":"32 32 32 32"},
            "container":{"alignItems":"center","justifyContent":"center",
                "shadow":{"color":{"light":"#33000000"},"blurRadius":12,"offsetX":0,"offsetY":4,"spreadRadius":0}
            },
            "background":{"backgroundColor":{"light":"#FFFFFF"}}
        }}]}},"children":[$CARD_CHILD]}}"""

        const val SHADOW_HARD_NO_BLUR = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":120}},
            "spacing":{"margin":"32 32 32 32"},
            "container":{"alignItems":"center","justifyContent":"center",
                "shadow":{"color":{"light":"#000000"},"blurRadius":0,"offsetX":8,"offsetY":8,"spreadRadius":0}
            },
            "background":{"backgroundColor":{"light":"#FFFFFF"}}
        }}]}},"children":[$CARD_CHILD]}}"""

        const val SHADOW_LARGE_BLUR_NO_OFFSET = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":120}},
            "spacing":{"margin":"40 40 40 40"},
            "container":{"alignItems":"center","justifyContent":"center",
                "shadow":{"color":{"light":"#66000000"},"blurRadius":40,"offsetX":0,"offsetY":0,"spreadRadius":4}
            },
            "background":{"backgroundColor":{"light":"#FFFFFF"}}
        }}]}},"children":[$CARD_CHILD]}}"""

        const val SHADOW_COLOURED = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":120}},
            "spacing":{"margin":"32 32 32 32"},
            "container":{"alignItems":"center","justifyContent":"center",
                "shadow":{"color":{"light":"#990066CC"},"blurRadius":24,"offsetX":0,"offsetY":12,"spreadRadius":0}
            },
            "background":{"backgroundColor":{"light":"#FFFFFF"}}
        }}]}},"children":[$CARD_CHILD]}}"""

        const val SHADOW_NEGATIVE_OFFSET = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":120}},
            "spacing":{"margin":"32 32 32 32"},
            "container":{"alignItems":"center","justifyContent":"center",
                "shadow":{"color":{"light":"#66000000"},"blurRadius":16,"offsetX":-8,"offsetY":-8,"spreadRadius":0}
            },
            "background":{"backgroundColor":{"light":"#FFFFFF"}}
        }}]}},"children":[$CARD_CHILD]}}"""

        const val SHADOW_WITH_BORDER_RADIUS = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":120}},
            "spacing":{"margin":"32 32 32 32"},
            "border":{"borderRadius":24},
            "container":{"alignItems":"center","justifyContent":"center",
                "shadow":{"color":{"light":"#33000000"},"blurRadius":16,"offsetX":0,"offsetY":6,"spreadRadius":0}
            },
            "background":{"backgroundColor":{"light":"#FFFFFF"}}
        }}]}},"children":[$CARD_CHILD]}}"""

        const val SHADOW_DARK_MODE = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":120}},
            "spacing":{"margin":"32 32 32 32"},
            "container":{"alignItems":"center","justifyContent":"center",
                "shadow":{"color":{"light":"#33000000","dark":"#9900FFFF"},"blurRadius":24,"offsetX":0,"offsetY":8,"spreadRadius":0}
            },
            "background":{"backgroundColor":{"light":"#FFFFFF","dark":"#111111"}}
        }}]}},"children":[$CARD_CHILD]}}"""
    }
}
