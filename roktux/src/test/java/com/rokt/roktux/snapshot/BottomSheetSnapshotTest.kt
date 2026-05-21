package com.rokt.roktux.snapshot

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureScreenRoboImage
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual regression coverage for the BottomSheet component, plus full RoktLayout
 * fixtures for the bottom sheet placements used by partner experiences.
 *
 * Uses captureScreenRoboImage() because the bottom sheet renders in a separate
 * window/popup that is not part of the compose root.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "xxhdpi",
)
@OptIn(ExperimentalRoborazziApi::class)
class BottomSheetSnapshotTest : RoktLayoutSnapshotTest() {

    @Test
    fun testBottomSheetOneByOne() = runTest(testDispatcher) {
        renderLayout("BottomSheetOneByOne.json")
        capture()
    }

    @Test
    fun testBottomSheetModern() = runTest(testDispatcher) {
        renderLayout("BottomSheetModern.json")
        capture()
    }

    @Test
    fun testBottomSheetCarousel() = runTest(testDispatcher) {
        renderLayout("BottomSheetCarousel.json")
        capture()
    }

    @Test
    @DcuiNodeJson(jsonString = BOTTOM_SHEET_DEFAULT)
    fun testBottomSheetWithSimpleText() = capture()

    @Test
    @DcuiNodeJson(jsonString = BOTTOM_SHEET_ROUNDED_TOP)
    fun testBottomSheetWithRoundedTopBorder() = capture()

    @Test
    @DcuiNodeJson(jsonString = BOTTOM_SHEET_WITH_BUTTONS)
    fun testBottomSheetWithButtons() = capture()

    @Test
    @DcuiNodeJson(jsonString = BOTTOM_SHEET_DEFAULT)
    @DcuiConfig(isDarkModeEnabled = true)
    fun testBottomSheetDarkMode() = capture()

    private fun capture() {
        composeTestRule.waitForIdle()
        captureScreenRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }

    private companion object {
        const val BOTTOM_SHEET_DEFAULT = """{
            "type":"BottomSheet","node":{
                "allowBackdropToClose":true,
                "styles":{"elements":{"own":[{"default":{
                    "background":{"backgroundColor":{"light":"#FFFFFF","dark":"#1A1A1A"}},
                    "spacing":{"padding":"24 24 24 24"},
                    "dimension":{"height":{"type":"fixed","value":160}}
                }}],"wrapper":[{"default":{
                    "background":{"backgroundColor":{"light":"#660E0A13","dark":"#660E0A13"}}
                }}]}},
                "children":[{"type":"BasicText","node":{"value":"Thank you for your purchase","styles":{"elements":{"own":[{"default":{
                    "text":{"fontSize":18,"fontWeight":"700","textColor":{"light":"#171717","dark":"#EEEEEE"},"horizontalTextAlign":"center"}
                }}]}}}}]
            }
        }"""

        const val BOTTOM_SHEET_ROUNDED_TOP = """{
            "type":"BottomSheet","node":{
                "allowBackdropToClose":true,
                "styles":{"elements":{"own":[{"default":{
                    "background":{"backgroundColor":{"light":"#FFFFFF"}},
                    "border":{"borderRadius":24,"borderColor":{"light":"#222222"},"borderWidth":"1"},
                    "spacing":{"padding":"24 24 24 24"},
                    "dimension":{"height":{"type":"fixed","value":200}}
                }}],"wrapper":[{"default":{
                    "background":{"backgroundColor":{"light":"#660E0A13"}}
                }}]}},
                "children":[{"type":"BasicText","node":{"value":"Rounded top sheet","styles":{"elements":{"own":[{"default":{
                    "text":{"fontSize":18,"fontWeight":"700","textColor":{"light":"#171717"},"horizontalTextAlign":"center"}
                }}]}}}}]
            }
        }"""

        const val BOTTOM_SHEET_WITH_BUTTONS = """{
            "type":"BottomSheet","node":{
                "allowBackdropToClose":true,
                "styles":{"elements":{"own":[{"default":{
                    "background":{"backgroundColor":{"light":"#FFFFFF"}},
                    "spacing":{"padding":"24 24 24 24"},
                    "dimension":{"height":{"type":"fixed","value":260}}
                }}],"wrapper":[{"default":{
                    "background":{"backgroundColor":{"light":"#660E0A13"}}
                }}]}},
                "children":[{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
                    "container":{"gap":"12","alignItems":"center"}
                }}]}},"children":[
                    {"type":"BasicText","node":{"value":"Confirm action","styles":{"elements":{"own":[{"default":{
                        "text":{"fontSize":18,"fontWeight":"700","textColor":{"light":"#171717"},"horizontalTextAlign":"center"}
                    }}]}}}},
                    {"type":"BasicText","node":{"value":"This will close the offer","styles":{"elements":{"own":[{"default":{
                        "text":{"fontSize":14,"textColor":{"light":"#555555"},"horizontalTextAlign":"center"}
                    }}]}}}},
                    {"type":"Row","node":{"styles":{"elements":{"own":[{"default":{
                        "container":{"gap":"12","justifyContent":"center"}
                    }}]}},"children":[
                        {"type":"BasicText","node":{"value":"Cancel","styles":{"elements":{"own":[{"default":{
                            "text":{"fontSize":14,"textColor":{"light":"#222222"},"horizontalTextAlign":"center"},
                            "border":{"borderColor":{"light":"#222222"},"borderWidth":"1","borderRadius":4},
                            "spacing":{"padding":"8 16 8 16"}
                        }}]}}}},
                        {"type":"BasicText","node":{"value":"Confirm","styles":{"elements":{"own":[{"default":{
                            "text":{"fontSize":14,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},
                            "background":{"backgroundColor":{"light":"#0066CC"}},
                            "border":{"borderRadius":4},
                            "spacing":{"padding":"8 16 8 16"}
                        }}]}}}}
                    ]}}
                ]}}]
            }
        }"""
    }
}
