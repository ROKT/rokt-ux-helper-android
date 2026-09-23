package com.rokt.roktux.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Holds one [ViewModelStore] per offer index, keyed for the lifetime of an [OfferViewModelStoreCache]
 * rather than for the lifetime of any particular composable subtree.
 *
 * Backed by a [ViewModel] — resolved against the ambient (real, host-provided) [LocalViewModelStoreOwner]
 * via [rememberOfferViewModelStoreCache] — so it survives host Activity recreation (rotation, etc.) the
 * same way `NavHost`'s own internal per-destination `ViewModelStore`s do via `NavControllerViewModel`.
 * A plain `remember` cannot do this: it's discarded and rebuilt on recreation along with the rest of the
 * composition, which would silently reset already-seen-offer state (e.g. view-signal dedup) on every
 * rotation.
 *
 * Callers control eviction explicitly via [retainOnly], called only when the set of currently relevant
 * offer indices changes for a real reason (the current offer moving), not on every recomposition — a
 * transient, breakpoint-driven change in how many offers are visible at once must never evict an
 * offer's state just because it briefly left a `for` loop's range.
 */
internal class OfferViewModelStoreCache : ViewModel() {
    private val stores = mutableMapOf<Int, ViewModelStore>()

    fun storeFor(offerIndex: Int): ViewModelStore = stores.getOrPut(offerIndex) { ViewModelStore() }

    /** Clears and removes every store whose offer index is not in [keep]. */
    fun retainOnly(keep: Set<Int>) {
        val stale = stores.keys - keep
        stale.forEach { stores.remove(it)?.clear() }
    }

    override fun onCleared() {
        stores.values.forEach { it.clear() }
        stores.clear()
    }
}

/**
 * Resolves an [OfferViewModelStoreCache] scoped to this call site, not just to the ambient (real,
 * host-provided) ViewModelStoreOwner. A bare `viewModel()` call resolves by class name alone, so two
 * Distribution components composed under the same ambient owner (e.g. two Distribution nodes in one
 * schema, both sharing the experience-scoped owner `RoktLayout` provides) would otherwise collide on
 * the exact same cache and its per-offer stores. Call this once per component instance, at a call
 * site that isn't itself wrapped in a per-offer `key(...)` block, so it stays stable across offer
 * changes but distinct per component instance.
 */
@Composable
internal fun rememberOfferViewModelStoreCache(): OfferViewModelStoreCache =
    viewModel(key = "OfferViewModelStoreCache#" + currentCompositeKeyHash.toString(36))

/**
 * Provides [content] with a [LocalViewModelStoreOwner] backed by [cache]'s store for [offerIndex],
 * so `viewModel(key = ...)` lookups inside [content] resolve per-offer state that survives host Activity
 * recreation and breakpoint-driven `viewableItems` churn, but is still cleared when [cache]'s owner
 * calls [OfferViewModelStoreCache.retainOnly] to say this offer is no longer relevant.
 */
@Composable
internal fun OfferScopedViewModelStoreOwner(
    offerIndex: Int,
    cache: OfferViewModelStoreCache,
    content: @Composable () -> Unit,
) {
    val owner = remember(cache, offerIndex) {
        object : ViewModelStoreOwner {
            override val viewModelStore = cache.storeFor(offerIndex)
        }
    }
    CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        content()
    }
}
