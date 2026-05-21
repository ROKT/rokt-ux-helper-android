package com.rokt.roktux.snapshot

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureScreenRoboImage
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual regression coverage for variants of the Overlay component beyond
 * the single existing BasicSnapshotTest.testOverlay. The Overlay renders
 * through Compose Popup — backdrop scrim, child positioning, and edge
 * inset behavior all live on APIs that have shifted across Compose BOMs.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class OverlayVariantSnapshotTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonString = OVERLAY_CENTERED_CARD)
    fun testCenteredCardOverlay() = capture()

    @Test
    @DcuiNodeJson(jsonString = OVERLAY_OPAQUE_BACKDROP)
    fun testOpaqueBackdropOverlay() = capture()

    @Test
    @DcuiNodeJson(jsonString = OVERLAY_FULLSCREEN_CONTENT)
    fun testFullscreenContentOverlay() = capture()

    @Test
    @DcuiNodeJson(jsonString = OVERLAY_CENTERED_CARD)
    @DcuiConfig(isDarkModeEnabled = true)
    fun testCenteredCardOverlayDarkMode() = capture()

    private fun capture() {
        // The Overlay renders into a Popup window that lives outside the compose root,
        // so wait for the popup's enter animation to settle before capturing.
        composeTestRule.waitForIdle()
        captureScreenRoboImage()
    }

    private companion object {
        const val CARD_BODY = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "container":{"gap":"8","alignItems":"center"},"spacing":{"padding":"24 24 24 24"}
        }}]}},"children":[
            {"type":"BasicText","node":{"value":"Limited time offer","styles":{"elements":{"own":[{"default":{
                "text":{"fontSize":18,"fontWeight":"700","textColor":{"light":"#171717","dark":"#FFFFFF"},"horizontalTextAlign":"center"}
            }}]}}}},
            {"type":"BasicText","node":{"value":"Tap continue to claim your free trial","styles":{"elements":{"own":[{"default":{
                "text":{"fontSize":14,"textColor":{"light":"#555555","dark":"#CCCCCC"},"horizontalTextAlign":"center"}
            }}]}}}}
        ]}}"""

        const val OVERLAY_CENTERED_CARD = """{"type":"Overlay","node":{
            "allowBackdropToClose":true,
            "styles":{"elements":{"own":[{"default":{
                "dimension":{"width":{"type":"fit","value":"wrap-content"},"height":{"type":"fit","value":"wrap-content"},"maxWidth":320},
                "flexChild":{"alignSelf":"center"},
                "background":{"backgroundColor":{"light":"#FFFFFF","dark":"#222222"}},
                "border":{"borderRadius":16},
                "spacing":{"margin":"24 24 24 24"}
            }}],"wrapper":[{"default":{
                "container":{"alignItems":"center","justifyContent":"center"},
                "background":{"backgroundColor":{"light":"#80000000","dark":"#80000000"}}
            }}]}},
            "children":[$CARD_BODY]
        }}"""

        const val OVERLAY_OPAQUE_BACKDROP = """{"type":"Overlay","node":{
            "allowBackdropToClose":false,
            "styles":{"elements":{"own":[{"default":{
                "dimension":{"width":{"type":"fit","value":"wrap-content"},"height":{"type":"fit","value":"wrap-content"},"maxWidth":280},
                "flexChild":{"alignSelf":"center"},
                "background":{"backgroundColor":{"light":"#FFFFFF"}},
                "spacing":{"margin":"24 24 24 24"}
            }}],"wrapper":[{"default":{
                "container":{"alignItems":"center","justifyContent":"center"},
                "background":{"backgroundColor":{"light":"#CC000000"}}
            }}]}},
            "children":[$CARD_BODY]
        }}"""

        const val OVERLAY_FULLSCREEN_CONTENT = """{"type":"Overlay","node":{
            "allowBackdropToClose":true,
            "styles":{"elements":{"own":[{"default":{
                "dimension":{"width":{"type":"percentage","value":100},"height":{"type":"percentage","value":100}},
                "background":{"backgroundColor":{"light":"#FFFFFF"}},
                "container":{"alignItems":"center","justifyContent":"center"}
            }}],"wrapper":[{"default":{
                "background":{"backgroundColor":{"light":"#00000000"}}
            }}]}},
            "children":[{"type":"BasicText","node":{"value":"Full-screen overlay","styles":{"elements":{"own":[{"default":{
                "text":{"fontSize":24,"fontWeight":"700","textColor":{"light":"#222222"},"horizontalTextAlign":"center"}
            }}]}}}}]
        }}"""
    }
}
