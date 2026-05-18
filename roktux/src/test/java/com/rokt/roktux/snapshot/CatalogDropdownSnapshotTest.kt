package com.rokt.roktux.snapshot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@Category(SnapshotTest::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "xxhdpi",
)
class CatalogDropdownSnapshotTest : BaseDcuiEspressoTest() {

    @OptIn(ExperimentalRoborazziApi::class)
    @Test
    @DcuiNodeJson(jsonFile = "CatalogDropdownComponent/CatalogDropdown_with_Group.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_group.json")
    fun testCatalogDropdownWithCatalogItemGroup() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
        composeTestRule.waitForIdle()

        composeTestRule.onRoot().captureRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }

    @OptIn(ExperimentalRoborazziApi::class)
    @Test
    @DcuiNodeJson(jsonFile = "CatalogDropdownComponent/CatalogDropdown_with_Group.json")
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_group.json")
    fun testCatalogDropdownOpenWithCatalogItemGroup() {
        composeTestRule.onNodeWithText("Select size").performClick()
        composeTestRule.onNodeWithText("8oz").assertIsDisplayed()
        composeTestRule.waitForIdle()

        composeTestRule.onRoot().captureRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }
}
