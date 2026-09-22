package com.rokt.roktux.utils

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.window.Popup
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeErrorBoundaryTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `renders content and never calls onError when content does not throw`() {
        var reportedError: Throwable? = null
        composeTestRule.setContent {
            ComposeErrorBoundary(
                onError = { reportedError = it },
                fallback = { Text("fallback") },
            ) {
                Text("content")
            }
        }

        composeTestRule.onNodeWithText("content").assertExists()
        composeTestRule.onNodeWithText("fallback").assertDoesNotExist()
        assertNull(reportedError)
    }

    @Test
    fun `falls back when the boundary sits inside a Popup and its content throws`() {
        // Mirrors the real nesting: OverlayComponent opens a Popup, and deeper inside it,
        // OneByOneDistributionComponent's ComposeErrorBoundary wraps NavHost. The boundary must
        // sit *inside* the Popup's own content to catch an exception thrown there — a check made
        // *before* entering the Popup (e.g. via Context/CompositionLocal) couldn't see this.
        var reportedError: Throwable? = null
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            Popup {
                ComposeErrorBoundary(
                    onError = { reportedError = it },
                    fallback = { Text("fallback") },
                ) {
                    error("boom inside popup")
                }
            }
        }
        composeTestRule.mainClock.advanceTimeBy(5_000)

        composeTestRule.onNodeWithText("fallback").assertExists()
        assertEquals("boom inside popup", reportedError?.message)
    }
}
