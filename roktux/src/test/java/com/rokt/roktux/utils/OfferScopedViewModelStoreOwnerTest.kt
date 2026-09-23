package com.rokt.roktux.utils

import androidx.compose.runtime.Composable
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

    // -- OfferViewModelStoreCache: direct unit tests of its own eviction contract --

    @Test
    fun `storeFor returns the same store for the same offer index until evicted`() {
        val cache = OfferViewModelStoreCache()
        val store = cache.storeFor(0)

        assertTrue(cache.storeFor(0) === store)
        cache.retainOnly(setOf(0))
        assertTrue("keeping an index must not evict its store", cache.storeFor(0) === store)
    }

    @Test
    fun `retainOnly clears and evicts stores outside the keep set, then a fresh lookup creates a new store`() {
        val cache = OfferViewModelStoreCache()
        val probe = ProbeViewModel()
        val originalStore = cache.storeFor(0)
        originalStore.put("probe", probe)

        cache.retainOnly(setOf(1))

        assertTrue("evicted offer's ViewModel must be cleared", probe.cleared)
        assertFalse(
            "a fresh lookup for the evicted offer must return a new store, not the cleared original",
            cache.storeFor(0) === originalStore,
        )
    }

    @Test
    fun `a store never targeted by retainOnly is never evicted, however long it goes unused`() {
        // Models a breakpoint-driven viewableItems shrink: an offer briefly leaves the visible
        // range, but nothing calls retainOnly excluding it (production code only calls retainOnly
        // when the *current offer* changes, not on every viewableItems recomputation) — so it must
        // still be there, unevicted, whenever it's looked up again.
        val cache = OfferViewModelStoreCache()
        val store = cache.storeFor(1)

        cache.retainOnly(setOf(0, 1, 2))
        cache.retainOnly(setOf(0, 1, 2))

        assertTrue(cache.storeFor(1) === store)
    }

    // -- rememberOfferViewModelStoreCache: call-site-scoped, not just ambient-owner-scoped --

    @Test
    fun `rememberOfferViewModelStoreCache resolves the same cache across recompositions of the same call site`() {
        val caches = mutableListOf<OfferViewModelStoreCache>()
        var recomposeTrigger by mutableStateOf(0)
        composeTestRule.setContent {
            @Suppress("UNUSED_EXPRESSION")
            recomposeTrigger
            caches.add(rememberOfferViewModelStoreCache())
        }

        composeTestRule.runOnIdle { recomposeTrigger = 1 }

        composeTestRule.runOnIdle {
            assertEquals(2, caches.size)
            assertTrue(
                "the same call site must resolve the same cache across recompositions — this is what " +
                    "lets it survive host Activity recreation in production, since the real Activity's " +
                    "own ViewModelStore (which backs the resolving viewModel() call) is retained across that",
                caches[0] === caches[1],
            )
        }
    }

    @Test
    fun `rememberOfferViewModelStoreCache resolves distinct caches for distinct call sites under the same owner`() {
        // Models two Distribution components composed under one experience-scoped owner (e.g. two
        // Distribution nodes in one schema) — they must not collide on the same per-offer stores.
        val caches = mutableListOf<OfferViewModelStoreCache>()
        composeTestRule.setContent {
            FirstDistributionSite { caches.add(rememberOfferViewModelStoreCache()) }
            SecondDistributionSite { caches.add(rememberOfferViewModelStoreCache()) }
        }

        composeTestRule.runOnIdle {
            assertEquals(2, caches.size)
            assertFalse(
                "two distinct component call sites must not resolve the same cache, or they'd collide " +
                    "on the same per-offer-index ViewModelStores",
                caches[0] === caches[1],
            )
        }
    }

    @Composable
    private fun FirstDistributionSite(content: @Composable () -> Unit) = content()

    @Composable
    private fun SecondDistributionSite(content: @Composable () -> Unit) = content()

    // -- OfferScopedViewModelStoreOwner: the composable wiring on top of the cache --

    @Test
    fun `same offer index resolves the same store across recompositions`() {
        val cache = OfferViewModelStoreCache()
        val owners = mutableListOf<ViewModelStoreOwner>()
        var recomposeTrigger by mutableStateOf(0)
        composeTestRule.setContent {
            key(0) {
                OfferScopedViewModelStoreOwner(offerIndex = 0, cache = cache) {
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
    fun `evicting the current offer index from the cache surfaces a distinct new store on the next composition`() {
        val cache = OfferViewModelStoreCache()
        val owners = mutableListOf<ViewModelStoreOwner>()
        var generation by mutableStateOf(0)
        composeTestRule.setContent {
            key(generation) {
                OfferScopedViewModelStoreOwner(offerIndex = 0, cache = cache) {
                    owners.add(LocalViewModelStoreOwner.current!!)
                }
            }
        }

        val probe = ProbeViewModel()
        composeTestRule.runOnIdle { owners[0].viewModelStore.put("probe", probe) }

        composeTestRule.runOnIdle {
            cache.retainOnly(emptySet())
            generation = 1
        }

        composeTestRule.runOnIdle {
            assertTrue("evicted offer's ViewModel should be cleared", probe.cleared)
            assertFalse("revisiting offer index 0 after eviction must not resolve the original store", owners[0] === owners[1])
        }
    }
}
