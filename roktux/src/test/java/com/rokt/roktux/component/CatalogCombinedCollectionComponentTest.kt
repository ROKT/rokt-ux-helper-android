package com.rokt.roktux.component

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiConfig
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.core.testutils.annotations.DcuiOfferJson
import com.rokt.modelmapper.uimodel.ButtonUiModel
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.testutil.renderParsedModelWithMutableOfferState
import org.assertj.core.api.Assertions.assertThat
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

    @Test
    @DcuiNodeJson(
        jsonFile = "CatalogCombinedCollectionComponent/CatalogCombinedCollection_with_Dropdown.json",
        loadComponent = false,
    )
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_group.json")
    fun testCatalogDropdownAttributeIndexIsScopedAcrossCatalogItemCopies() {
        val model = dcuiComponentRule.uiModel as LayoutSchemaUiModel.CatalogCombinedCollectionUiModel

        val dropdownsByCatalogItem = model.childrenByCatalogItem.mapValues { entry ->
            entry.value.findCatalogDropdowns()
        }

        assertThat(dropdownsByCatalogItem).containsOnlyKeys(0, 1, 2)
        assertThat(dropdownsByCatalogItem.values).allSatisfy { dropdowns ->
            assertThat(dropdowns).hasSize(1)
            assertThat(dropdowns.single().attributeIndex).isEqualTo(0)
            assertThat(dropdowns.single().customStateKey).isEqualTo("CatalogDropdown.0.selectedIndex")
        }
    }

    @Test
    @DcuiNodeJson(
        jsonFile = "CatalogCombinedCollectionComponent/CatalogCombinedCollection_with_Dropdown.json",
        loadComponent = false,
    )
    @DcuiConfig(testInInnerLayout = true)
    @DcuiOfferJson(jsonFile = "offer/Offer_with_catalog_item_group.json")
    fun testCatalogDropdownSelectionKeepsDropdownVisibleAfterCatalogItemSwitch() {
        renderParsedModelWithMutableOfferState()

        composeTestRule.onNodeWithText("\$19.88").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Select size").performClick()
        composeTestRule.onNodeWithText("16oz")
            .assertIsDisplayed()
            .performSemanticsAction(SemanticsActions.OnClick)

        composeTestRule.onNodeWithText("\$29.88").assertIsDisplayed()
        composeTestRule.onNodeWithText("16oz").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Select size").assertIsDisplayed()
    }

    private fun List<LayoutSchemaUiModel?>.findCatalogDropdowns(): List<LayoutSchemaUiModel.CatalogDropdownUiModel> = flatMap { child -> child.findCatalogDropdowns() }

    private fun LayoutSchemaUiModel?.findCatalogDropdowns(): List<LayoutSchemaUiModel.CatalogDropdownUiModel> = when (this) {
        is LayoutSchemaUiModel.CatalogDropdownUiModel -> listOf(this)

        is LayoutSchemaUiModel.ColumnUiModel -> children.findCatalogDropdowns()

        is LayoutSchemaUiModel.RowUiModel -> children.findCatalogDropdowns()

        is LayoutSchemaUiModel.BoxUiModel -> children.findCatalogDropdowns()

        is LayoutSchemaUiModel.WhenUiModel -> children.findCatalogDropdowns()

        is LayoutSchemaUiModel.CatalogCombinedCollectionUiModel -> childrenByCatalogItem.values.flatten()
            .findCatalogDropdowns()

        is ButtonUiModel -> children.findCatalogDropdowns()

        else -> emptyList()
    }
}
