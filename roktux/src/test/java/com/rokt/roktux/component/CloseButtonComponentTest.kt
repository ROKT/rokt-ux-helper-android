package com.rokt.roktux.component

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.viewmodel.layout.LayoutContract
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CloseButtonComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "CloseButtonComponent/CloseButton_with_Children.json")
    fun testCloseButtonClickable() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG)
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    @DcuiNodeJson(jsonFile = "CloseButtonComponent/CloseButton_with_Children.json")
    fun testCloseButtonWithChildren() {
        val component = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .assertIsDisplayed()

        component.onChildAt(0).assertTextEquals("Test1")
        component.onChildAt(1).onChildAt(0).assertTextEquals("Test2")
        component.onChildAt(1).onChildAt(1).assertTextEquals("Test3")
    }

    @Test
    @DcuiNodeJson(jsonFile = "CloseButtonComponent/CloseButton_with_Children.json")
    fun testCloseButtonClickedEvent() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .performClick()

        assertTrue(getCapturedEvents().stream().anyMatch { e -> e is LayoutContract.LayoutEvent.CloseSelected })
    }
}
