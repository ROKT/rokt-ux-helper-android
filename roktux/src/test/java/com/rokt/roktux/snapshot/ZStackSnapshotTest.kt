package com.rokt.roktux.snapshot

import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual regression coverage for the ZStack component (the schema's
 * Box-equivalent — children render on top of one another with optional
 * alignment). Wraps Compose `Box` + alignment semantics that shift
 * between Compose foundation releases.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class ZStackSnapshotTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonString = ZSTACK_CENTER)
    fun testZStackCenter() = capture()

    @Test
    @DcuiNodeJson(jsonString = ZSTACK_TOP_START)
    fun testZStackTopStart() = capture()

    @Test
    @DcuiNodeJson(jsonString = ZSTACK_BOTTOM_END)
    fun testZStackBottomEnd() = capture()

    @Test
    @DcuiNodeJson(jsonString = ZSTACK_OVERLAPPING)
    fun testZStackOverlappingChildren() = capture()

    @Test
    @DcuiNodeJson(jsonString = ZSTACK_ALIGNSELF_CHILDREN)
    fun testZStackPerChildAlignment() = capture()

    private fun capture() {
        composeTestRule.onRoot().captureRoboImage()
    }

    private companion object {
        const val SWATCH_LARGE = """{"type":"BasicText","node":{"value":"BG","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#0066CC"}},"dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":200}}}}]}}}}"""
        const val SWATCH_MID = """{"type":"BasicText","node":{"value":"M","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#22AA66"}},"dimension":{"width":{"type":"fixed","value":140},"height":{"type":"fixed","value":140}}}}]}}}}"""
        const val SWATCH_TOP = """{"type":"BasicText","node":{"value":"FG","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#CC3366"}},"dimension":{"width":{"type":"fixed","value":80},"height":{"type":"fixed","value":80}}}}]}}}}"""

        const val SWATCH_SMALL_BLUE = """{"type":"BasicText","node":{"value":"A","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#0066CC"}},"dimension":{"width":{"type":"fixed","value":60},"height":{"type":"fixed","value":60}}}}]}}}}"""
        const val SWATCH_SMALL_GREEN_TL = """{"type":"BasicText","node":{"value":"TL","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#22AA66"}},"flexChild":{"alignSelf":"flex-start"},"dimension":{"width":{"type":"fixed","value":50},"height":{"type":"fixed","value":50}}}}]}}}}"""
        const val SWATCH_SMALL_PINK_BR = """{"type":"BasicText","node":{"value":"BR","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#CC3366"}},"flexChild":{"alignSelf":"flex-end"},"dimension":{"width":{"type":"fixed","value":50},"height":{"type":"fixed","value":50}}}}]}}}}"""

        const val ZSTACK_CENTER = """{"type":"ZStack","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":240},"height":{"type":"fixed","value":240}},
            "container":{"alignItems":"center","justifyContent":"center"},
            "background":{"backgroundColor":{"light":"#EEEEEE"}}
        }}]}},"children":[$SWATCH_SMALL_BLUE]}}"""

        const val ZSTACK_TOP_START = """{"type":"ZStack","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":240},"height":{"type":"fixed","value":240}},
            "container":{"alignItems":"flex-start","justifyContent":"flex-start"},
            "background":{"backgroundColor":{"light":"#EEEEEE"}}
        }}]}},"children":[$SWATCH_SMALL_BLUE]}}"""

        const val ZSTACK_BOTTOM_END = """{"type":"ZStack","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":240},"height":{"type":"fixed","value":240}},
            "container":{"alignItems":"flex-end","justifyContent":"flex-end"},
            "background":{"backgroundColor":{"light":"#EEEEEE"}}
        }}]}},"children":[$SWATCH_SMALL_BLUE]}}"""

        const val ZSTACK_OVERLAPPING = """{"type":"ZStack","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":240},"height":{"type":"fixed","value":240}},
            "container":{"alignItems":"center","justifyContent":"center"},
            "background":{"backgroundColor":{"light":"#EEEEEE"}}
        }}]}},"children":[$SWATCH_LARGE,$SWATCH_MID,$SWATCH_TOP]}}"""

        const val ZSTACK_ALIGNSELF_CHILDREN = """{"type":"ZStack","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":240},"height":{"type":"fixed","value":240}},
            "background":{"backgroundColor":{"light":"#EEEEEE"}}
        }}]}},"children":[$SWATCH_SMALL_GREEN_TL,$SWATCH_SMALL_PINK_BR]}}"""
    }
}
