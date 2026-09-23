package com.rokt.roktux.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfferScopedViewModelStoreOwnerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private class ProbeViewModel : ViewModel() {
        var cleared = false
        override fun onCleared() {
            cleared = true
        }
    }

    @Test
    fun `same key reuses the same store across recompositions`() {
        val owners = mutableListOf<ViewModelStoreOwner>()
        var recomposeTrigger by mutableStateOf(0)
        composeTestRule.setContent {
            key(0) {
                OfferScopedViewModelStoreOwner {
                    @Suppress("UNUSED_EXPRESSION")
                    recomposeTrigger
                    owners.add(LocalViewModelStoreOwner.current!!)
                }
            }
        }

        composeTestRule.runOnIdle { recomposeTrigger = 1 }

        composeTestRule.runOnIdle {
            assertEquals(2, owners.size)
            assertTrue(owners[0] === owners[1])
        }
    }

    @Test
    fun `changing the key disposes the old store exactly once and creates a distinct new one`() {
        var currentKey by mutableStateOf(0)
        val owners = mutableMapOf<Int, ViewModelStoreOwner>()
        composeTestRule.setContent {
            key(currentKey) {
                OfferScopedViewModelStoreOwner {
                    owners[currentKey] = LocalViewModelStoreOwner.current!!
                }
            }
        }
        val probe = ProbeViewModel()
        composeTestRule.runOnIdle {
            owners.getValue(0).viewModelStore.put("probe", probe)
        }

        composeTestRule.runOnIdle { currentKey = 1 }

        composeTestRule.runOnIdle {
            assertTrue("old store's ViewModel should be cleared exactly once it's superseded", probe.cleared)
            assertFalse("new key must get a distinct store instance", owners.getValue(0) === owners.getValue(1))
        }
    }

    @Test
    fun `returning to a previously visited key creates a brand-new store, not the original`() {
        var currentKey by mutableStateOf(0)
        val owners = mutableListOf<ViewModelStoreOwner>()
        composeTestRule.setContent {
            key(currentKey) {
                OfferScopedViewModelStoreOwner {
                    owners.add(LocalViewModelStoreOwner.current!!)
                }
            }
        }

        composeTestRule.runOnIdle { currentKey = 1 }
        composeTestRule.runOnIdle { currentKey = 0 }

        composeTestRule.runOnIdle {
            assertEquals(3, owners.size)
            assertFalse(
                "revisiting a key must not resolve the original, never-cleared store",
                owners[0] === owners[2],
            )
        }
    }
}
