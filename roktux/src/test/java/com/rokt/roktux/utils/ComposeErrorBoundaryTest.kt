package com.rokt.roktux.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
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

    @Test
    fun `does not crash when a sibling's Stretch alignment forces an intrinsic-size query`() {
        // A Row with a Modifier.fillMaxHeight() sibling gets Modifier.height(IntrinsicSize.Min)
        // (mirrors RowComponent's Stretch handling), which queries every child's intrinsic
        // height, including this one's — SubcomposeLayout can't answer that and throws by
        // default. This must not surface as a crash, on the success path or the fallback one.
        composeTestRule.setContent {
            Row(Modifier.height(IntrinsicSize.Min)) {
                ComposeErrorBoundary(fallback = { Text("fallback") }) {
                    Text("content")
                }
                Box(Modifier.fillMaxHeight())
            }
        }

        composeTestRule.onNodeWithText("content").assertExists()
    }

    @Test
    fun `content that throws does not crash under a sibling's intrinsic-size query either`() {
        // Same setup, but content throws: NoIntrinsicsModifier must apply before the failure
        // state is even known, so this must not crash regardless of which branch ends up
        // showing. (Robolectric's test dispatcher doesn't reliably drain the fallback's
        // LaunchedEffect within a manual clock advance here — see the Popup-nested test above
        // for that assertion — so this only checks setContent+advance complete without crashing.)
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            Row(Modifier.height(IntrinsicSize.Min)) {
                ComposeErrorBoundary(fallback = { Text("fallback") }) {
                    error("boom")
                }
                Box(Modifier.fillMaxHeight())
            }
        }
        composeTestRule.mainClock.advanceTimeBy(5_000)
    }
}
