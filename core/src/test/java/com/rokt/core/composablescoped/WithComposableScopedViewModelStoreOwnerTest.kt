package com.rokt.core.composablescoped

import android.app.Activity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class WithComposableScopedViewModelStoreOwnerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testViewModelStoreOwner = object : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore = ViewModelStore()
    }

    @Test
    fun `content renders when LocalViewModelStoreOwner is available`() {
        var contentRendered = false
        var callbackInvoked = false

        composeTestRule.setContent {
            CompositionLocalProvider(LocalViewModelStoreOwner provides testViewModelStoreOwner) {
                WithComposableScopedViewModelStoreOwner(
                    key = "test-key",
                    onNoViewModelStoreOwner = { callbackInvoked = true },
                ) {
                    contentRendered = true
                }
            }
        }

        composeTestRule.waitForIdle()

        assertTrue("Content should be rendered when LocalViewModelStoreOwner is available", contentRendered)
        assertFalse("Callback should not be invoked when ViewModelStoreOwner is available", callbackInvoked)
    }

    @Test
    fun `content renders when Context has ViewModelStoreOwner`() {
        var contentRendered = false
        var callbackInvoked = false

        // Create an Activity which implements ViewModelStoreOwner
        val activityController = Robolectric.buildActivity(TestViewModelStoreOwnerActivity::class.java)
        val activity = activityController.create().start().resume().get()

        composeTestRule.setContent {
            // Provide the activity as context
            CompositionLocalProvider(LocalContext provides activity) {
                WithComposableScopedViewModelStoreOwner(
                    key = "test-key",
                    onNoViewModelStoreOwner = { callbackInvoked = true },
                ) {
                    contentRendered = true
                }
            }
        }

        composeTestRule.waitForIdle()

        assertTrue("Content should be rendered when ViewModelStoreOwner is available", contentRendered)
        assertFalse("Callback should not be invoked when ViewModelStoreOwner is available", callbackInvoked)

        activityController.destroy()
    }
}

class TestViewModelStoreOwnerActivity :
    Activity(),
    ViewModelStoreOwner {
    private val _viewModelStore = ViewModelStore()
    override val viewModelStore: ViewModelStore
        get() = _viewModelStore
}
