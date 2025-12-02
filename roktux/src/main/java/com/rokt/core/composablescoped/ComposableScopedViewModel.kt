package com.rokt.core.composablescoped

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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

/**
 * Provides a composable-scoped ViewModelStoreOwner for the given content.
 *
 * This function includes a recovery strategy: if [LocalViewModelStoreOwner] is not available
 * in the current composition (e.g., during view recycling, detachment, or when used in contexts
 * like RecyclerView without proper ViewModelStoreOwner setup), it will attempt to find a
 * ViewModelStoreOwner from the Context hierarchy (Activity/Fragment).
 *
 * If no ViewModelStoreOwner can be found through either method, the content will not be rendered
 * to prevent crashes.
 *
 * @param key Optional key to differentiate between different scopes
 * @param onNoViewModelStoreOwner Optional callback invoked when ViewModelStoreOwner is unavailable
 *        even after attempting recovery, useful for diagnostics or logging
 * @param content The composable content to render within the scoped ViewModelStoreOwner
 */
@Composable
fun WithComposableScopedViewModelStoreOwner(
    key: Any?,
    onNoViewModelStoreOwner: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val currentOwner = LocalViewModelStoreOwner.current
    val context = LocalContext.current

    // Recovery Strategy: If local owner is missing, try to find it from the Context hierarchy
    val owner = currentOwner ?: remember(context) { context.findViewModelStoreOwner() }

    if (owner == null) {
        onNoViewModelStoreOwner?.invoke()
        return
    }

    // If we recovered an owner that wasn't in the Local provider, we must provide it
    // so that the internal viewModel() call inside rememberComposableScopedViewModelStoreOwner works.
    if (currentOwner == null) {
        CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
            CompositionLocalProvider(
                value = LocalViewModelStoreOwner provides rememberComposableScopedViewModelStoreOwner(key),
                content = content,
            )
        }
    } else {
        // Standard path where owner already exists in CompositionLocal
        CompositionLocalProvider(
            value = LocalViewModelStoreOwner provides rememberComposableScopedViewModelStoreOwner(key),
            content = content,
        )
    }
}

/**
 * Recursively walks up the Context hierarchy to find a ViewModelStoreOwner.
 * Returns the first ViewModelStoreOwner found, or null if none exists.
 */
private tailrec fun Context.findViewModelStoreOwner(): ViewModelStoreOwner? = when (this) {
    is ViewModelStoreOwner -> this
    is ContextWrapper -> baseContext.findViewModelStoreOwner()
    else -> null
}
