package com.rokt.roktux.component

import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.testutil.renderParsedModelWithMutableOfferState
import com.rokt.roktux.viewmodel.layout.LayoutContract
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BottomSheetComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "BottomSheetComponent/BottomSheet_with_height.json")
    fun testBottomSheetComponentWithHeight() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithText("Purchase successful").assertIsDisplayed()
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).assertHeightIsEqualTo(20.dp)
    }

    @Test
    @DcuiNodeJson(
        jsonFile = "BottomSheetComponent/BottomSheet_with_expandable_percentage_height.json",
        loadComponent = false,
    )
    fun testBottomSheetPercentageHeightExpandsAndCollapsesWithCustomState() {
        val controller = renderParsedModelWithMutableOfferState()

        composeTestRule.onNodeWithText("Collapsed content", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Expanded content", useUnmergedTree = true).assertDoesNotExist()
        val collapsedHeight = sheetHeight()

        composeTestRule.runOnIdle {
            controller.setCustomState(BottomSheetExpandedStateKey, 1)
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Expanded content", useUnmergedTree = true).assertIsDisplayed()
        val expandedHeight = sheetHeight()
        assertTrue(
            "Expected expanded sheet height ($expandedHeight) to be greater than collapsed height ($collapsedHeight)",
            expandedHeight > collapsedHeight * ExpandedHeightGrowthThreshold,
        )

        composeTestRule.runOnIdle {
            controller.setCustomState(BottomSheetExpandedStateKey, 0)
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Expanded content", useUnmergedTree = true).assertDoesNotExist()
        val collapsedAgainHeight = sheetHeight()
        assertTrue(
            "Expected collapsed sheet height ($collapsedAgainHeight) to be less than expanded height ($expandedHeight)",
            collapsedAgainHeight < expandedHeight / ExpandedHeightGrowthThreshold,
        )
    }

    @Test
    @DcuiNodeJson(
        jsonFile = "BottomSheetComponent/BottomSheet_with_expandable_percentage_height.json",
        loadComponent = false,
    )
    fun testBottomSheetExpandedStateUsesOfferScopedStateBeforeGlobalState() {
        renderParsedModelWithMutableOfferState(
            initialCustomState = persistentMapOf(BottomSheetExpandedStateKey to 1),
            initialOfferCustomStates = persistentMapOf(
                "0" to persistentMapOf(BottomSheetExpandedStateKey to 0),
            ),
        )

        composeTestRule.onNodeWithText("Collapsed content", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Expanded content", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    @DcuiNodeJson(
        jsonFile = "BottomSheetComponent/BottomSheet_with_expandable_percentage_height.json",
        loadComponent = false,
    )
    fun testBottomSheetExpandedStateToggleEmitsCustomStateEvent() {
        renderParsedModelWithMutableOfferState()

        composeTestRule.onNode(
            hasClickAction() and hasAnyDescendant(hasText("Toggle details")),
            useUnmergedTree = true,
        ).performClick()

        assertTrue(
            getCapturedEvents().any {
                it == LayoutContract.LayoutEvent.SetCustomState(BottomSheetExpandedStateKey, 1)
            },
        )
    }

    private fun sheetHeight(): Float {
        composeTestRule.waitForIdle()
        return composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG).fetchSemanticsNode().boundsInRoot.height
    }

    private companion object {
        const val BottomSheetExpandedStateKey = "BottomSheetExpandedState"
        const val ExpandedHeightGrowthThreshold = 1.5f
    }
}
