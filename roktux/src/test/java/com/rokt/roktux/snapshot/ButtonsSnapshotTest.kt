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
 * Visual regression coverage for the button-shaped components:
 * CreativeResponse, StaticLink, CloseButton, ToggleButtonStateTrigger,
 * and ProgressControl. All wrap a clickable Box / Row composable so they
 * share modifier / ripple / pressed-state code paths that can shift in
 * Compose BOM upgrades.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class ButtonsSnapshotTest : BaseDcuiEspressoTest() {

    // ---- StaticLink ----------------------------------------------------------------------

    @Test
    @DcuiNodeJson(jsonString = STATIC_LINK_BASIC)
    fun testStaticLinkDefault() = capture()

    @Test
    @DcuiNodeJson(jsonString = STATIC_LINK_BASIC)
    @DcuiConfig(pseudoState = TestPseudoState(isPressed = true))
    fun testStaticLinkPressed() = capture()

    // ---- CloseButton ---------------------------------------------------------------------

    @Test
    @DcuiNodeJson(jsonString = CLOSE_BUTTON)
    fun testCloseButtonRendered() = capture()

    @Test
    @DcuiNodeJson(jsonString = CLOSE_BUTTON_ICON_STYLE)
    fun testCloseButtonAsIcon() = capture()

    // ---- ToggleButtonStateTrigger --------------------------------------------------------

    @Test
    @DcuiNodeJson(jsonString = TOGGLE_BUTTON)
    fun testToggleButtonRendered() = capture()

    // ---- ProgressControl -----------------------------------------------------------------

    @Test
    @DcuiNodeJson(jsonString = PROGRESS_FORWARD)
    fun testProgressControlForward() = capture()

    @Test
    @DcuiNodeJson(jsonString = PROGRESS_BACKWARD)
    fun testProgressControlBackward() = capture()

    private fun capture() {
        composeTestRule.onRoot().captureRoboImage()
    }

    private companion object {
        const val BUTTON_LABEL_STYLES = """"text":{"fontSize":16,"fontWeight":"700","textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"}"""

        const val STATIC_LINK_BASIC = """{
            "type":"StaticLink","node":{
                "src":"https://rokt.com",
                "open":"externally",
                "styles":{"elements":{"own":[{"default":{
                    "border":{"borderColor":{"light":"#0066CC"},"borderWidth":"1","borderRadius":24},
                    "background":{"backgroundColor":{"light":"#0066CC"}},
                    "container":{"alignItems":"center","justifyContent":"center"},
                    "spacing":{"padding":"12 24 12 24"},
                    "dimension":{"width":{"type":"fixed","value":200}}
                },"pressed":{
                    "border":{"borderColor":{"light":"#003F80"},"borderWidth":"1","borderRadius":24},
                    "background":{"backgroundColor":{"light":"#003F80"}},
                    "container":{"alignItems":"center","justifyContent":"center"},
                    "spacing":{"padding":"12 24 12 24"},
                    "dimension":{"width":{"type":"fixed","value":200}}
                }}]}},
                "children":[{"type":"BasicText","node":{"value":"Visit Rokt","styles":{"elements":{"own":[{"default":{$BUTTON_LABEL_STYLES}}]}}}}]
            }
        }"""

        const val CLOSE_BUTTON = """{
            "type":"CloseButton","node":{
                "styles":{"elements":{"own":[{"default":{
                    "border":{"borderColor":{"light":"#222222"},"borderWidth":"1","borderRadius":4},
                    "background":{"backgroundColor":{"light":"#FFFFFF"}},
                    "container":{"alignItems":"center","justifyContent":"center"},
                    "spacing":{"padding":"8 16 8 16"}
                }}]}},
                "children":[{"type":"BasicText","node":{"value":"Close","styles":{"elements":{"own":[{"default":{"text":{"fontSize":14,"textColor":{"light":"#222222"}}}}]}}}}]
            }
        }"""

        const val CLOSE_BUTTON_ICON_STYLE = """{
            "type":"CloseButton","node":{
                "styles":{"elements":{"own":[{"default":{
                    "container":{"alignItems":"center","justifyContent":"center"},
                    "background":{"backgroundColor":{"light":"#FFFFFF"}},
                    "border":{"borderColor":{"light":"#999999"},"borderWidth":"1","borderRadius":20},
                    "dimension":{"width":{"type":"fixed","value":40},"height":{"type":"fixed","value":40}}
                }}]}},
                "children":[{"type":"StaticIcon","node":{"name":"Close","styles":{"elements":{"own":[{"default":{
                    "text":{"fontSize":20,"textColor":{"light":"#222222"}}
                }}]}}}}]
            }
        }"""

        const val TOGGLE_BUTTON = """{
            "type":"ToggleButtonStateTrigger","node":{
                "customStateKey":"snapshot-toggle",
                "styles":{"elements":{"own":[{"default":{
                    "border":{"borderColor":{"light":"#222222"},"borderWidth":"1","borderRadius":8},
                    "background":{"backgroundColor":{"light":"#FFFFFF"}},
                    "container":{"alignItems":"center","justifyContent":"center"},
                    "spacing":{"padding":"12 24 12 24"}
                }}]}},
                "children":[{"type":"BasicText","node":{"value":"Show more details","styles":{"elements":{"own":[{"default":{"text":{"fontSize":14,"textColor":{"light":"#222222"}}}}]}}}}]
            }
        }"""

        const val PROGRESS_FORWARD = """{
            "type":"ProgressControl","node":{
                "direction":"Forward",
                "styles":{"elements":{"own":[{"default":{
                    "background":{"backgroundColor":{"light":"#0066CC"}},
                    "container":{"alignItems":"center","justifyContent":"center"},
                    "spacing":{"padding":"10 16 10 16"},
                    "border":{"borderRadius":4}
                }}]}},
                "children":[{"type":"BasicText","node":{"value":"Next ›","styles":{"elements":{"own":[{"default":{$BUTTON_LABEL_STYLES}}]}}}}]
            }
        }"""

        const val PROGRESS_BACKWARD = """{
            "type":"ProgressControl","node":{
                "direction":"Backward",
                "styles":{"elements":{"own":[{"default":{
                    "background":{"backgroundColor":{"light":"#666666"}},
                    "container":{"alignItems":"center","justifyContent":"center"},
                    "spacing":{"padding":"10 16 10 16"},
                    "border":{"borderRadius":4}
                }}]}},
                "children":[{"type":"BasicText","node":{"value":"‹ Back","styles":{"elements":{"own":[{"default":{$BUTTON_LABEL_STYLES}}]}}}}]
            }
        }"""
    }
}
