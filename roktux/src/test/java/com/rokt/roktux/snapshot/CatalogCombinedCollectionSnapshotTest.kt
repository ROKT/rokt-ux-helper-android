package com.rokt.roktux.snapshot

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.testutil.renderParsedModelWithMutableOfferState
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
class CatalogCombinedCollectionSnapshotTest : BaseDcuiEspressoTest() {

    @OptIn(ExperimentalRoborazziApi::class)
    @Test
    @DcuiNodeJson(
        jsonFile = "CatalogCombinedCollectionComponent/CatalogCombinedCollection_with_Background_Border_Text.json",
    )
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_items.json")
    fun testCatalogCombinedCollectionWithBackgroundBorderAndText() {
        composeTestRule.onNodeWithText("Everyday sneakers").assertIsDisplayed()

        composeTestRule.onRoot().captureRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }

    @OptIn(ExperimentalRoborazziApi::class)
    @Test
    @DcuiNodeJson(
        jsonFile = "CatalogCombinedCollectionComponent/CatalogCombinedCollection_with_Dropdown.json",
        loadComponent = false,
    )
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_group.json")
    fun testCatalogCombinedCollectionDropdownAfterCatalogItemSwitch() {
        renderParsedModelWithMutableOfferState()

        composeTestRule.onNodeWithContentDescription("Select size").performClick()
        composeTestRule.onNodeWithText("16oz")
            .assertIsDisplayed()
            .performSemanticsAction(SemanticsActions.OnClick)
        composeTestRule.onNodeWithText("\$29.88").assertIsDisplayed()
        composeTestRule.onNodeWithText("16oz").assertIsDisplayed()
        composeTestRule.waitForIdle()

        composeTestRule.onRoot().captureRoboImage(roborazziOptions = snapshotRoborazziOptions)
    }
}
