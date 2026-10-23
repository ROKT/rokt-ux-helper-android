package com.rokt.roktux.snapshot

import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual regression coverage for the layout container components
 * (Column / Row). These wrap Compose foundation layout APIs that
 * often shift behavior in Compose BOM upgrades — flex children,
 * alignment, justification, and weighted distribution.
 *
 * Box is intentionally not covered here: it is an internal render
 * component and not exposed in the DCUI schema, so it cannot be
 * driven through the JSON fixture pipeline.
 */
@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "xxhdpi")
@OptIn(ExperimentalRoborazziApi::class)
class ContainerSnapshotTest : BaseDcuiEspressoTest() {

    // ---- Column ---------------------------------------------------------------------------

    @Test
    @DcuiNodeJson(jsonString = COLUMN_ALIGN_ITEMS_START)
    fun testColumnAlignItemsStart() = capture()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_ALIGN_ITEMS_CENTER)
    fun testColumnAlignItemsCenter() = capture()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_ALIGN_ITEMS_END)
    fun testColumnAlignItemsEnd() = capture()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_JUSTIFY_START)
    fun testColumnJustifyContentStart() = capture()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_JUSTIFY_CENTER)
    fun testColumnJustifyContentCenter() = capture()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_JUSTIFY_END)
    fun testColumnJustifyContentEnd() = capture()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_GAP)
    fun testColumnGap() = capture()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_WEIGHTED_CHILDREN)
    fun testColumnWeightedChildren() = capture()

    @Test
    @DcuiNodeJson(jsonString = COLUMN_NESTED_ROWS)
    fun testColumnNestedRows() = capture()

    // ---- Row ------------------------------------------------------------------------------

    @Test
    @DcuiNodeJson(jsonString = ROW_JUSTIFY_START)
    fun testRowJustifyContentStart() = capture()

    @Test
    @DcuiNodeJson(jsonString = ROW_JUSTIFY_CENTER)
    fun testRowJustifyContentCenter() = capture()

    @Test
    @DcuiNodeJson(jsonString = ROW_JUSTIFY_END)
    fun testRowJustifyContentEnd() = capture()

    @Test
    @DcuiNodeJson(jsonString = ROW_ALIGN_ITEMS_CENTER)
    fun testRowAlignItemsCenter() = capture()

    @Test
    @DcuiNodeJson(jsonString = ROW_GAP_AND_WEIGHTED)
    fun testRowGapAndWeighted() = capture()

    @Test
    @DcuiNodeJson(jsonString = STRETCH_ROW_WITH_CAROUSEL_AND_TEXT_SIBLINGS)
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_image_carousel_key.json")
    fun testStretchRowWithCarouselAndTextSiblings() = capture()

    @Test
    @DcuiNodeJson(jsonString = SCROLLABLE_ROW_WITH_CAROUSEL)
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_image_carousel_key.json")
    fun testScrollableRowWithCarousel() = capture()

    private fun capture() {
        composeTestRule.onRoot().captureRoboImage()
    }

    private companion object {
        // ---- Reusable child snippets ----
        const val SWATCH_A_60 = """{"type":"BasicText","node":{"value":"A","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#0066CC"}},"dimension":{"width":{"type":"fixed","value":60},"height":{"type":"fixed","value":60}}}}]}}}}"""
        const val SWATCH_B_60 = """{"type":"BasicText","node":{"value":"B","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#22AA66"}},"dimension":{"width":{"type":"fixed","value":60},"height":{"type":"fixed","value":60}}}}]}}}}"""
        const val SWATCH_C_60 = """{"type":"BasicText","node":{"value":"C","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#CC3366"}},"dimension":{"width":{"type":"fixed","value":60},"height":{"type":"fixed","value":60}}}}]}}}}"""

        const val SWATCH_A_40 = """{"type":"BasicText","node":{"value":"A","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#0066CC"}},"dimension":{"width":{"type":"fixed","value":40},"height":{"type":"fixed","value":40}}}}]}}}}"""
        const val SWATCH_B_40 = """{"type":"BasicText","node":{"value":"B","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#22AA66"}},"dimension":{"width":{"type":"fixed","value":40},"height":{"type":"fixed","value":40}}}}]}}}}"""
        const val SWATCH_C_40 = """{"type":"BasicText","node":{"value":"C","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#CC3366"}},"dimension":{"width":{"type":"fixed","value":40},"height":{"type":"fixed","value":40}}}}]}}}}"""
        const val SWATCH_D_40 = """{"type":"BasicText","node":{"value":"D","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#FF9933"}},"dimension":{"width":{"type":"fixed","value":40},"height":{"type":"fixed","value":40}}}}]}}}}"""
        const val SWATCH_E_40 = """{"type":"BasicText","node":{"value":"E","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#9933CC"}},"dimension":{"width":{"type":"fixed","value":40},"height":{"type":"fixed","value":40}}}}]}}}}"""

        const val SWATCH_A_50 = """{"type":"BasicText","node":{"value":"A","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#0066CC"}},"dimension":{"width":{"type":"fixed","value":50},"height":{"type":"fixed","value":50}}}}]}}}}"""
        const val SWATCH_B_50 = """{"type":"BasicText","node":{"value":"B","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#22AA66"}},"dimension":{"width":{"type":"fixed","value":50},"height":{"type":"fixed","value":50}}}}]}}}}"""

        const val SWATCH_A_80 = """{"type":"BasicText","node":{"value":"A","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#0066CC"}},"dimension":{"width":{"type":"fixed","value":80},"height":{"type":"fixed","value":80}}}}]}}}}"""

        // ---- Column ----
        const val COLUMN_ALIGN_ITEMS_START = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":280},"height":{"type":"fixed","value":280}},"container":{"alignItems":"flex-start"},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[$SWATCH_A_60,$SWATCH_B_60,$SWATCH_C_60]}}"""

        const val COLUMN_ALIGN_ITEMS_CENTER = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":280},"height":{"type":"fixed","value":280}},"container":{"alignItems":"center"},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[$SWATCH_A_60,$SWATCH_B_60,$SWATCH_C_60]}}"""

        const val COLUMN_ALIGN_ITEMS_END = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":280},"height":{"type":"fixed","value":280}},"container":{"alignItems":"flex-end"},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[$SWATCH_A_60,$SWATCH_B_60,$SWATCH_C_60]}}"""

        const val COLUMN_JUSTIFY_START = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":300}},"container":{"justifyContent":"flex-start"},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[$SWATCH_A_60,$SWATCH_B_60]}}"""

        const val COLUMN_JUSTIFY_CENTER = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":300}},"container":{"justifyContent":"center"},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[$SWATCH_A_60,$SWATCH_B_60]}}"""

        const val COLUMN_JUSTIFY_END = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":300}},"container":{"justifyContent":"flex-end"},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[$SWATCH_A_60,$SWATCH_B_60]}}"""

        const val COLUMN_GAP = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":300}},"container":{"gap":"16"},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[$SWATCH_A_60,$SWATCH_B_60,$SWATCH_C_60]}}"""

        const val COLUMN_WEIGHTED_CHILDREN = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":200},"height":{"type":"fixed","value":300}},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[{"type":"BasicText","node":{"value":"1x","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#0066CC"}},"flexChild":{"weight":1},"dimension":{"width":{"type":"percentage","value":100}}}}]}}}},{"type":"BasicText","node":{"value":"2x","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#22AA66"}},"flexChild":{"weight":2},"dimension":{"width":{"type":"percentage","value":100}}}}]}}}}]}}"""

        const val COLUMN_NESTED_ROWS = """{"type":"Column","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":300},"height":{"type":"fixed","value":260}},"container":{"gap":"8"},"background":{"backgroundColor":{"light":"#EEEEEE"}},"spacing":{"padding":"8 8 8 8"}}}]}},"children":[{"type":"Row","node":{"styles":{"elements":{"own":[{"default":{"container":{"gap":"8"}}}]}},"children":[$SWATCH_A_40,$SWATCH_B_40,$SWATCH_C_40]}},{"type":"Row","node":{"styles":{"elements":{"own":[{"default":{"container":{"gap":"8","justifyContent":"flex-end"}}}]}},"children":[$SWATCH_D_40,$SWATCH_E_40]}}]}}"""

        // ---- Row ----
        const val ROW_JUSTIFY_START = """{"type":"Row","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":320},"height":{"type":"fixed","value":80}},"container":{"justifyContent":"flex-start"},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[$SWATCH_A_50,$SWATCH_B_50]}}"""

        const val ROW_JUSTIFY_CENTER = """{"type":"Row","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":320},"height":{"type":"fixed","value":80}},"container":{"justifyContent":"center"},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[$SWATCH_A_50,$SWATCH_B_50]}}"""

        const val ROW_JUSTIFY_END = """{"type":"Row","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":320},"height":{"type":"fixed","value":80}},"container":{"justifyContent":"flex-end"},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[$SWATCH_A_50,$SWATCH_B_50]}}"""

        const val ROW_ALIGN_ITEMS_CENTER = """{"type":"Row","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":320},"height":{"type":"fixed","value":120}},"container":{"alignItems":"center","gap":"12"},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[$SWATCH_A_40,$SWATCH_B_60,$SWATCH_A_80]}}"""

        const val ROW_GAP_AND_WEIGHTED = """{"type":"Row","node":{"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":320},"height":{"type":"fixed","value":80}},"container":{"gap":"16"},"background":{"backgroundColor":{"light":"#EEEEEE"}}}}]}},"children":[{"type":"BasicText","node":{"value":"1x","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#0066CC"}},"flexChild":{"weight":1},"dimension":{"height":{"type":"percentage","value":100}}}}]}}}},{"type":"BasicText","node":{"value":"3x","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"},"horizontalTextAlign":"center"},"background":{"backgroundColor":{"light":"#22AA66"}},"flexChild":{"weight":3},"dimension":{"height":{"type":"percentage","value":100}}}}]}}}}]}}"""

        // ---- Carousels inside rows ----
        // Images do not load under Robolectric, so every box has its own background and a 1dp border.
        const val OUTLINE = """"border":{"borderColor":{"light":"#333333"},"borderWidth":"1"}"""
        const val TEXT_SHORT = """{"type":"BasicText","node":{"value":"Short","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"}},"background":{"backgroundColor":{"light":"#22AA66"}},$OUTLINE,"spacing":{"padding":"4 4 4 4"}}}]}}}}"""
        const val TEXT_LONG = """{"type":"BasicText","node":{"value":"A longer label","styles":{"elements":{"own":[{"default":{"text":{"fontSize":12,"textColor":{"light":"#FFFFFF"}},"background":{"backgroundColor":{"light":"#CC3366"}},$OUTLINE,"spacing":{"padding":"4 4 4 4"}}}]}}}}"""

        // Fixed size: the stretch row above it must leave its text siblings at their own height.
        const val CAROUSEL_FIXED = """{"type":"DataImageCarousel","node":{"imageKey":"creativeCarouselImageVertical","duration":0,"styles":{"elements":{"own":[{"default":{"background":{"backgroundColor":{"light":"#0066CC"}},$OUTLINE,"dimension":{"width":{"type":"fixed","value":120},"height":{"type":"fixed","value":120}}}}]}}}}"""

        // Fixed height only: under a horizontal scroll the carousel takes the window width.
        const val CAROUSEL_WITH_INDICATORS = """{"type":"DataImageCarousel","node":{"imageKey":"creativeCarouselImageVertical","duration":0,"indicators":{"show":true,"activeIndicatorMode":"normal"},"styles":{"elements":{"own":[{"default":{"background":{"backgroundColor":{"light":"#0066CC"}},$OUTLINE,"dimension":{"height":{"type":"fixed","value":120}}}}],"indicator":[{"default":{"background":{"backgroundColor":{"light":"#FFFFFF"}},$OUTLINE,"dimension":{"width":{"type":"fixed","value":24},"height":{"type":"fixed","value":6}},"spacing":{"margin":"0 2 0 2"}}}],"activeIndicator":[{"default":{"background":{"backgroundColor":{"light":"#FF9933"}},$OUTLINE,"dimension":{"width":{"type":"fixed","value":24},"height":{"type":"fixed","value":6}},"spacing":{"margin":"0 2 0 2"}}}],"progressIndicatorContainer":[{"default":{"background":{"backgroundColor":{"light":"#9933CC"}},$OUTLINE,"spacing":{"padding":"4 4 4 4"}}}]}}}}"""

        const val STRETCH_ROW_WITH_CAROUSEL_AND_TEXT_SIBLINGS = """{"type":"Row","node":{"styles":{"elements":{"own":[{"default":{"container":{"alignItems":"stretch","gap":"8"},"background":{"backgroundColor":{"light":"#EEEEEE"}},$OUTLINE,"spacing":{"padding":"8 8 8 8"}}}]}},"children":[$CAROUSEL_FIXED,$TEXT_SHORT,$TEXT_LONG]}}"""

        const val SCROLLABLE_ROW_WITH_CAROUSEL = """{"type":"ScrollableRow","node":{"styles":{"elements":{"own":[{"default":{"background":{"backgroundColor":{"light":"#EEEEEE"}},$OUTLINE,"dimension":{"height":{"type":"fixed","value":160}}}}]}},"children":[$CAROUSEL_WITH_INDICATORS,$TEXT_SHORT]}}"""
    }
}
