package com.rokt.roktux.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.core.testutils.assertion.assertBackgroundColor
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.viewmodel.layout.LayoutContract
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogImageGalleryComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "CatalogImageGalleryComponent/CatalogImageGallery_with_Images_Indicators.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_images.json")
    fun testCatalogImageGalleryComponentWithImagesAndIndicators() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertWidthIsEqualTo(180.dp)
            .assertHeightIsEqualTo(140.dp)
            .assertBackgroundColor("#eef7ff")
    }

    @Test
    @DcuiNodeJson(jsonFile = "CatalogImageGalleryComponent/CatalogImageGallery_with_Images_Indicators.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_images.json")
    fun testCatalogImageGallerySideTapMovesForward() {
        composeTestRule.waitForIdle()
        dcuiComponentRule.capturedEvents.clear()

        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .performTouchInput {
                click(position = Offset(x = center.x * 1.5f, y = center.y / 2f))
            }

        waitForImageCarouselPosition(2)
    }

    @Test
    @DcuiNodeJson(jsonFile = "CatalogImageGalleryComponent/CatalogImageGallery_with_Images_Indicators.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_images.json")
    fun testCatalogImageGalleryNavigationButtonMovesForward() {
        composeTestRule.waitForIdle()
        dcuiComponentRule.capturedEvents.clear()

        composeTestRule.onNodeWithContentDescription("Next image")
            .assertIsDisplayed()
            .performClick()

        waitForImageCarouselPosition(2)
    }

    private fun waitForImageCarouselPosition(position: Int) {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            getCapturedEvents().any { event ->
                event == LayoutContract.LayoutEvent.SetCustomState("imageCarouselPosition", position)
            }
        }
    }
}
