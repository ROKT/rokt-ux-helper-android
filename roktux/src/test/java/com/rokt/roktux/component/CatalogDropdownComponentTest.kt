package com.rokt.roktux.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.viewmodel.layout.LayoutContract
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogDropdownComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "CatalogDropdownComponent/CatalogDropdown_with_Group.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_group.json")
    fun testCatalogDropdownComponentWithCatalogItemGroup() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertWidthIsEqualTo(220.dp)

        composeTestRule.onNodeWithText("Select size").assertIsDisplayed()
        composeTestRule.onNodeWithText("8oz").assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(jsonFile = "CatalogDropdownComponent/CatalogDropdown_with_Group.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_group.json")
    fun testCatalogDropdownSelectionUpdatesActiveCatalogItem() {
        composeTestRule.onNodeWithText("Select size").performClick()
        composeTestRule.onNodeWithText("16oz").assertIsDisplayed().performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            getCapturedEvents().contains(
                LayoutContract.LayoutEvent.SetCustomState(
                    key = "CatalogDropdown.0.selectedIndex",
                    value = 1,
                ),
            ) && getCapturedEvents().contains(LayoutContract.LayoutEvent.SetActiveCatalogItem(index = 1))
        }
    }

    @Test
    @DcuiNodeJson(jsonFile = "CatalogDropdownComponent/CatalogDropdown_with_Group.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_group.json")
    fun testCatalogDropdownDisablesOutOfStockOptions() {
        composeTestRule.onNodeWithText("Select size").performClick()

        composeTestRule.onNodeWithText("32oz")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }
}
