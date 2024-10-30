package com.rokt.roktux.viewmodel.composablescoped

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
private fun rememberComposableScopedViewModelStoreOwner(key: Any?): ViewModelStoreOwner {
    val composableKey = "rememberComposableScopedViewModelStoreOwner#" + currentCompositeKeyHash.toString(36)
    val localLifecycle = LocalLifecycleOwner.current.lifecycle
    val composableScopedViewModelStoreOwnerHolder = viewModel<ComposableScopedViewModelStoreOwnerHolder>()
    val composableScopedViewModelStoreOwner =
        composableScopedViewModelStoreOwnerHolder[composableKey, key, localLifecycle]

    remember {
        object : RememberObserver {
            override fun onAbandoned() {
                composableScopedViewModelStoreOwnerHolder.releaseComposableScopedViewModelStoreOwner(composableKey)
            }

            override fun onForgotten() {
                composableScopedViewModelStoreOwnerHolder.releaseComposableScopedViewModelStoreOwner(composableKey)
            }

            override fun onRemembered() {
                composableScopedViewModelStoreOwnerHolder.holdComposableScopedViewModelStoreOwner(composableKey)
            }
        }
    }
    return composableScopedViewModelStoreOwner
}

@Composable
internal fun WithComposableScopedViewModelStoreOwner(key: Any?, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalViewModelStoreOwner provides rememberComposableScopedViewModelStoreOwner(key),
        content = content,
    )
}
