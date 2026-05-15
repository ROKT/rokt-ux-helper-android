package com.rokt.roktux.component

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.viewmodel.layout.LayoutContract
import org.junit.Assert.assertEquals
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
    fun testCatalogDropdownOptionsRenderAsOverlay() {
        composeTestRule.onNodeWithText("Select size").performClick()

        composeTestRule.onNodeWithText("8oz").assertIsDisplayed()
        composeTestRule.onAllNodes(
            matcher = isRoot().and(hasAnyDescendant(hasText("8oz"))),
            useUnmergedTree = true,
        ).apply {
            assertCountEquals(1)
            this[0].assertWidthIsEqualTo(220.dp)
        }
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHeightIsEqualTo(44.dp)
    }

    @Test
    fun testCatalogDropdownPopupOffsetUsesAvailableSpace() {
        val windowSize = IntSize(width = 400, height = 500)
        val popupSize = IntSize(width = 220, height = 120)

        assertEquals(
            IntOffset(0, -120),
            catalogDropdownPopupOffset(
                anchorTop = 350,
                anchorHeight = 44,
                windowHeight = windowSize.height,
                popupHeight = popupSize.height,
                windowTopOnScreen = 0,
            ),
        )

        assertEquals(
            IntOffset(0, 44),
            catalogDropdownPopupOffset(
                anchorTop = 100,
                anchorHeight = 44,
                windowHeight = windowSize.height,
                popupHeight = popupSize.height,
                windowTopOnScreen = 0,
            ),
        )

        assertEquals(
            IntOffset(0, 68),
            catalogDropdownPopupOffset(
                anchorTop = 100,
                anchorHeight = 44,
                windowHeight = windowSize.height,
                popupHeight = popupSize.height,
                windowTopOnScreen = 24,
            ),
        )
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
