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
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ToggleButtonComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "ToggleButtonComponent/ToggleButton_with_State.json")
    fun testToggleButtonClickable() {
        val component = composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .assertHasClickAction()
            .assertIsDisplayed()

        component.onChildAt(0).assertTextEquals("Toggle button")
    }

    @Test
    @DcuiNodeJson(jsonFile = "ToggleButtonComponent/ToggleButton_with_State.json")
    fun testToggleButtonClickedEvent() {
        composeTestRule.onNodeWithTag(DCUI_COMPONENT_TAG, useUnmergedTree = true)
            .performClick()

        Assert.assertTrue(
            getCapturedEvents().any { e -> e.equals(LayoutContract.LayoutEvent.SetCustomState("ToggleButtonState", 1)) },
        )
    }
}
