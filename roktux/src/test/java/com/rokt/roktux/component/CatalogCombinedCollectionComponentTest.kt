package com.rokt.roktux.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogCombinedCollectionComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(
        jsonFile = "CatalogCombinedCollectionComponent/CatalogCombinedCollection_with_Background_Border_Text.json",
    )
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_items.json")
    fun testCatalogCombinedCollectionComponentWithBackgroundBorderAndText() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertWidthIsEqualTo(220.dp)

        composeTestRule.onNodeWithText("Everyday sneakers").assertIsDisplayed()
        composeTestRule.onNodeWithText("Weekend boots").assertDoesNotExist()
    }
}
