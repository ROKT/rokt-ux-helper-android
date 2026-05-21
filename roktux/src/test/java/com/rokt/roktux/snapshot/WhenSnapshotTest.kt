package com.rokt.roktux.snapshot

import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeComponentState
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual regression coverage for the When component. Wraps Compose
 * AnimatedVisibility — a frequent source of behavioural differences
 * across Compose BOM versions, especially around transition completion
 * and content size animation.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class WhenSnapshotTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonString = WHEN_STATIC_TRUE)
    fun testStaticBooleanTrueRendersChildren() = capture()

    @Test
    @DcuiNodeJson(jsonString = WHEN_STATIC_FALSE)
    fun testStaticBooleanFalseHidesChildren() = capture()

    @Test
    @DcuiNodeJson(jsonString = WHEN_PROGRESSION_IS_FIRST)
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 3)
    fun testProgressionConditionMatchesFirstOffer() = capture()

    @Test
    @DcuiNodeJson(jsonString = WHEN_PROGRESSION_IS_FIRST)
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 3)
    fun testProgressionConditionFailsForSecondOffer() = capture()

    @Test
    @DcuiNodeJson(jsonString = WHEN_DARK_MODE_IS_TRUE)
    @DcuiConfig(isDarkModeEnabled = true)
    fun testDarkModeConditionMatchesDarkMode() = capture()

    @Test
    @DcuiNodeJson(jsonString = WHEN_DARK_MODE_IS_TRUE)
    @DcuiConfig(isDarkModeEnabled = false)
    fun testDarkModeConditionFailsInLightMode() = capture()

    @Test
    @DcuiNodeJson(jsonString = WHEN_POSITION_FIRST)
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 3)
    fun testPositionConditionMatchesFirstOffer() = capture()

    @Test
    @DcuiNodeJson(jsonString = WHEN_POSITION_FIRST)
    @DcuiNodeComponentState(currentOffer = 2, totalOffer = 3)
    fun testPositionConditionFailsForLastOffer() = capture()

    @Test
    @DcuiNodeJson(jsonString = WHEN_BREAKPOINT_IS_LANDSCAPE)
    fun testBreakpointConditionMatches() = capture()

    @Test
    @DcuiNodeJson(jsonString = WHEN_BREAKPOINT_IS_ABOVE_MOBILE)
    fun testBreakpointAboveCondition() = capture()

    @Test
    @DcuiNodeJson(jsonString = WHEN_MULTIPLE_PREDICATES_AND)
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 3)
    @DcuiConfig(isDarkModeEnabled = false)
    fun testMultiplePredicatesAllMatch() = capture()

    @Test
    @DcuiNodeJson(jsonString = WHEN_MULTIPLE_PREDICATES_AND)
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 3)
    @DcuiConfig(isDarkModeEnabled = false)
    fun testMultiplePredicatesOneFails() = capture()

    @Test
    @DcuiNodeJson(jsonString = WHEN_FADE_TRANSITION_STATIC_TRUE)
    fun testWhenWithFadeInOutTransitionVisible() = capture()

    private fun capture() {
        composeTestRule.onRoot().captureRoboImage()
    }

    private companion object {
        const val VISIBLE_CHILD = """
            {"type":"BasicText","node":{"value":"VISIBLE","styles":{"elements":{"own":[{"default":{
                "text":{"fontSize":18,"textColor":{"light":"#FFFFFF","dark":"#FFFFFF"},"horizontalTextAlign":"center"},
                "background":{"backgroundColor":{"light":"#22AA66","dark":"#22AA66"}},
                "spacing":{"padding":"16 24 16 24"},
                "dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":80}}
            }}]}}}}
        """

        // When the predicate evaluates false the When component renders nothing,
        // so wrap every test in a fixed-size Column so the snapshot has a stable
        // canvas (empty for "false" predicates, populated for "true" predicates).
        const val WRAPPER_OPEN = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{
            "dimension":{"width":{"type":"fixed","value":260},"height":{"type":"fixed","value":120}},
            "container":{"alignItems":"center","justifyContent":"center"},
            "background":{"backgroundColor":{"light":"#EEEEEE","dark":"#222222"}}
        }}]}},"children":["""
        const val WRAPPER_CLOSE = "]}}"

        const val WHEN_STATIC_TRUE_INNER = """{
            "type":"When","node":{
                "predicates":[{"type":"StaticBoolean","predicate":{"condition":"is-true","value":true}}],
                "children":[$VISIBLE_CHILD]
            }
        }"""
        const val WHEN_STATIC_TRUE = "$WRAPPER_OPEN$WHEN_STATIC_TRUE_INNER$WRAPPER_CLOSE"

        const val WHEN_STATIC_FALSE_INNER = """{
            "type":"When","node":{
                "predicates":[{"type":"StaticBoolean","predicate":{"condition":"is-true","value":false}}],
                "children":[$VISIBLE_CHILD]
            }
        }"""
        const val WHEN_STATIC_FALSE = "$WRAPPER_OPEN$WHEN_STATIC_FALSE_INNER$WRAPPER_CLOSE"

        const val WHEN_PROGRESSION_INNER = """{
            "type":"When","node":{
                "predicates":[{"type":"Progression","predicate":{"condition":"is","value":"0"}}],
                "children":[$VISIBLE_CHILD]
            }
        }"""
        const val WHEN_PROGRESSION_IS_FIRST = "$WRAPPER_OPEN$WHEN_PROGRESSION_INNER$WRAPPER_CLOSE"

        const val WHEN_DARK_MODE_INNER = """{
            "type":"When","node":{
                "predicates":[{"type":"DarkMode","predicate":{"condition":"is","value":"true"}}],
                "children":[$VISIBLE_CHILD]
            }
        }"""
        const val WHEN_DARK_MODE_IS_TRUE = "$WRAPPER_OPEN$WHEN_DARK_MODE_INNER$WRAPPER_CLOSE"

        const val WHEN_POSITION_INNER = """{
            "type":"When","node":{
                "predicates":[{"type":"Position","predicate":{"condition":"is","value":"0"}}],
                "children":[$VISIBLE_CHILD]
            }
        }"""
        const val WHEN_POSITION_FIRST = "$WRAPPER_OPEN$WHEN_POSITION_INNER$WRAPPER_CLOSE"

        const val WHEN_BREAKPOINT_IS_INNER = """{
            "type":"When","node":{
                "predicates":[{"type":"Breakpoint","predicate":{"condition":"is","value":"Landscape"}}],
                "children":[$VISIBLE_CHILD]
            }
        }"""
        const val WHEN_BREAKPOINT_IS_LANDSCAPE = "$WRAPPER_OPEN$WHEN_BREAKPOINT_IS_INNER$WRAPPER_CLOSE"

        const val WHEN_BREAKPOINT_ABOVE_INNER = """{
            "type":"When","node":{
                "predicates":[{"type":"Breakpoint","predicate":{"condition":"is-above","value":"Mobile"}}],
                "children":[$VISIBLE_CHILD]
            }
        }"""
        const val WHEN_BREAKPOINT_IS_ABOVE_MOBILE = "$WRAPPER_OPEN$WHEN_BREAKPOINT_ABOVE_INNER$WRAPPER_CLOSE"

        // Combine three predicates with implicit AND — all must match for the When to render its children.
        const val WHEN_MULTIPLE_INNER = """{
            "type":"When","node":{
                "predicates":[
                    {"type":"StaticBoolean","predicate":{"condition":"is-true","value":true}},
                    {"type":"Progression","predicate":{"condition":"is","value":"0"}},
                    {"type":"DarkMode","predicate":{"condition":"is","value":"false"}}
                ],
                "children":[$VISIBLE_CHILD]
            }
        }"""
        const val WHEN_MULTIPLE_PREDICATES_AND = "$WRAPPER_OPEN$WHEN_MULTIPLE_INNER$WRAPPER_CLOSE"

        // FadeIn/FadeOut transitions — short duration so the snapshot capture happens after the
        // animation has fully settled (Roborazzi waits for compose idleness).
        const val WHEN_FADE_INNER = """{
            "type":"When","node":{
                "transition":{
                    "inTransition":[{"type":"FadeIn","settings":{"duration":50}}],
                    "outTransition":[{"type":"FadeOut","settings":{"duration":50}}]
                },
                "predicates":[{"type":"StaticBoolean","predicate":{"condition":"is-true","value":true}}],
                "children":[$VISIBLE_CHILD]
            }
        }"""
        const val WHEN_FADE_TRANSITION_STATIC_TRUE = "$WRAPPER_OPEN$WHEN_FADE_INNER$WRAPPER_CLOSE"
    }
}
