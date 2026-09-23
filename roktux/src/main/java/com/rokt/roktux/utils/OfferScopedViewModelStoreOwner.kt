package com.rokt.roktux.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * Gives [content] its own [ViewModelStore], cleared when this composable leaves the composition.
 *
 * Used inside a `key(offerIndex) { ... }` block so each offer gets a fresh store when first
 * composed and has it cleared (`onCleared()`) when `key()` discards it for a different offer —
 * reproducing what a `NavHost` back-stack entry's own per-entry `ViewModelStore` provided, without
 * `NavHost`. Without this, `viewModel(key = offerIndex.toString())` would resolve against the
 * single ambient `LocalViewModelStoreOwner` for the component's entire lifetime, so returning to a
 * previously seen offer would resolve the *same*, never-cleared `ViewModel` instance instead
 * of a fresh one.
 */
@Composable
internal fun OfferScopedViewModelStoreOwner(content: @Composable () -> Unit) {
    val owner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
    }
    DisposableEffect(Unit) {
        onDispose { owner.viewModelStore.clear() }
    }
    CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        content()
    }
}
