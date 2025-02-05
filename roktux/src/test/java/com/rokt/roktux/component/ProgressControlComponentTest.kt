package com.rokt.roktux.component

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiNodeComponentState
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.viewmodel.layout.LayoutContract
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgressControlComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "ProgressControlComponent/ProgressControl_with_Children.json")
    fun testProgressControlButtonClickable() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressControlComponent/ProgressControl_with_Children.json")
    fun testProgressControlButtonWithChildren() {
        val component = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .assertIsDisplayed()

        component.onChildAt(0).assertTextEquals("Test1")
        component.onChildAt(1).onChildAt(0).assertTextEquals("Test2")
        component.onChildAt(1).onChildAt(1).assertTextEquals("Test3")
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressControlComponent/ProgressControl_Forward.json")
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 2)
    fun testProgressControlButtonNavigationEventForwardWhenPossible() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        Assert.assertTrue(
            getCapturedEvents().stream()
                .anyMatch { e -> e.equals(LayoutContract.LayoutEvent.LayoutVariantNavigated(1)) },
        )
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressControlComponent/ProgressControl_Forward.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2)
    fun testProgressControlButtonNavigationNoEventForwardWhenNotPossible() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        Assert.assertFalse(
            getCapturedEvents().stream()
                .anyMatch { e -> e.equals(LayoutContract.LayoutEvent.LayoutVariantNavigated(2)) },
        )
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressControlComponent/ProgressControl_Backward.json")
    @DcuiNodeComponentState(currentOffer = 1, totalOffer = 2)
    fun testProgressControlButtonNavigationEventBackwardWhenPossible() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        Assert.assertTrue(
            getCapturedEvents().stream()
                .anyMatch { e -> e.equals(LayoutContract.LayoutEvent.LayoutVariantNavigated(0)) },
        )
    }

    @Test
    @DcuiNodeJson(jsonFile = "ProgressControlComponent/ProgressControl_Backward.json")
    @DcuiNodeComponentState(currentOffer = 0, totalOffer = 2)
    fun testProgressControlButtonNavigationNoEventBackwardWhenNotPossible() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        Assert.assertFalse(
            getCapturedEvents().stream()
                .anyMatch { e -> e.equals(LayoutContract.LayoutEvent.LayoutVariantNavigated(0)) },
        )
    }
}
