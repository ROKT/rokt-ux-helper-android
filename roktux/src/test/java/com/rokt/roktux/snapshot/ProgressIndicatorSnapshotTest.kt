package com.rokt.roktux.snapshot

import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.rokt.core.testutils.annotations.DcuiNodeComponentState
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual regression coverage for the ProgressIndicator component.
 * Exercises active/seen/unseen indicator styles across progression
 * positions — important because pager state behaviour shifts across
 * Compose foundation releases.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class ProgressIndicatorSnapshotTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonString = INDICATOR_BASE)
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 4)
    fun testActiveAtPositionOne() = capture()

    @Test
    @DcuiNodeJson(jsonString = INDICATOR_BASE)
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 4)
    fun testActiveAtPositionTwo() = capture()

    @Test
    @DcuiNodeJson(jsonString = INDICATOR_BASE)
    @DcuiNodeComponentState(currentOffer = 2, totalOffer = 4)
    fun testActiveAtPositionThree() = capture()

    @Test
    @DcuiNodeJson(jsonString = INDICATOR_BASE)
    @DcuiNodeComponentState(currentOffer = 3, totalOffer = 4)
    fun testActiveAtLastPosition() = capture()

    @Test
    @DcuiNodeJson(jsonString = INDICATOR_WITH_SEEN_STATE)
    @DcuiNodeComponentState(currentOffer = 2, totalOffer = 5)
    fun testIndicatorWithSeenAndUnseenStates() = capture()

    @Test
    @DcuiNodeJson(jsonString = INDICATOR_NUMBERED)
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 4)
    fun testNumberedIndicator() = capture()

    @Test
    @DcuiNodeJson(jsonString = INDICATOR_JUSTIFY_CENTER)
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 4)
    fun testJustifyContentCenter() = capture()

    private fun capture() {
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }

    private companion object {
        const val DOT_DIMENSIONS = """"dimension":{"width":{"type":"fixed","value":16},"height":{"type":"fixed","value":16}}"""

        const val INDICATOR_BASE = """{
            "type": "ProgressIndicator",
            "node": {
                "indicator": "%^STATE.IndicatorPosition^%",
                "styles": {
                    "elements": {
                        "own": [{"default": {
                            "container": {"gap": "8", "justifyContent": "flex-start", "alignItems": "center"},
                            "spacing": {"padding": "8 8 8 8"}
                        }}],
                        "indicator": [{"default": {
                            "background": {"backgroundColor": {"light": "#CCCCCC"}},
                            "border": {"borderRadius": 8},
                            $DOT_DIMENSIONS
                        }}],
                        "activeIndicator": [{"default": {
                            "background": {"backgroundColor": {"light": "#0066CC"}},
                            "border": {"borderRadius": 8},
                            $DOT_DIMENSIONS
                        }}]
                    }
                }
            }
        }"""

        const val INDICATOR_WITH_SEEN_STATE = """{
            "type": "ProgressIndicator",
            "node": {
                "indicator": "%^STATE.IndicatorPosition^%",
                "styles": {
                    "elements": {
                        "own": [{"default": {
                            "container": {"gap": "8", "justifyContent": "center", "alignItems": "center"},
                            "spacing": {"padding": "8 8 8 8"}
                        }}],
                        "indicator": [{"default": {
                            "background": {"backgroundColor": {"light": "#EEEEEE"}},
                            "border": {"borderRadius": 8},
                            $DOT_DIMENSIONS
                        }}],
                        "activeIndicator": [{"default": {
                            "background": {"backgroundColor": {"light": "#0066CC"}},
                            "border": {"borderRadius": 8},
                            $DOT_DIMENSIONS
                        }}],
                        "seenIndicator": [{"default": {
                            "background": {"backgroundColor": {"light": "#22AA66"}},
                            "border": {"borderRadius": 8},
                            $DOT_DIMENSIONS
                        }}]
                    }
                }
            }
        }"""

        const val INDICATOR_NUMBERED = """{
            "type": "ProgressIndicator",
            "node": {
                "indicator": "%^STATE.IndicatorPosition^%",
                "styles": {
                    "elements": {
                        "own": [{"default": {
                            "container": {"gap": "10", "justifyContent": "center", "alignItems": "center"},
                            "spacing": {"padding": "8 8 8 8"}
                        }}],
                        "indicator": [{"default": {
                            "background": {"backgroundColor": {"light": "#FFFFFF"}},
                            "border": {"borderColor": {"light": "#999999"}, "borderWidth": "1", "borderRadius": 12},
                            "container": {"alignItems": "center", "justifyContent": "center"},
                            "text": {"fontSize": 12, "textColor": {"light": "#999999"}, "horizontalTextAlign": "center"},
                            "dimension": {"width": {"type": "fixed", "value": 24}, "height": {"type": "fixed", "value": 24}}
                        }}],
                        "activeIndicator": [{"default": {
                            "background": {"backgroundColor": {"light": "#0066CC"}},
                            "border": {"borderColor": {"light": "#0066CC"}, "borderWidth": "1", "borderRadius": 12},
                            "container": {"alignItems": "center", "justifyContent": "center"},
                            "text": {"fontSize": 12, "textColor": {"light": "#FFFFFF"}, "horizontalTextAlign": "center", "fontWeight": "700"},
                            "dimension": {"width": {"type": "fixed", "value": 24}, "height": {"type": "fixed", "value": 24}}
                        }}]
                    }
                }
            }
        }"""

        const val INDICATOR_JUSTIFY_CENTER = """{
            "type": "ProgressIndicator",
            "node": {
                "indicator": "%^STATE.IndicatorPosition^%",
                "styles": {
                    "elements": {
                        "own": [{"default": {
                            "container": {"gap": "8", "justifyContent": "center", "alignItems": "center"},
                            "dimension": {"width": {"type": "fixed", "value": 320}, "height": {"type": "fixed", "value": 40}},
                            "background": {"backgroundColor": {"light": "#EEEEEE"}}
                        }}],
                        "indicator": [{"default": {
                            "background": {"backgroundColor": {"light": "#CCCCCC"}},
                            "border": {"borderRadius": 8},
                            $DOT_DIMENSIONS
                        }}],
                        "activeIndicator": [{"default": {
                            "background": {"backgroundColor": {"light": "#0066CC"}},
                            "border": {"borderRadius": 8},
                            $DOT_DIMENSIONS
                        }}]
                    }
                }
            }
        }"""
    }
}
