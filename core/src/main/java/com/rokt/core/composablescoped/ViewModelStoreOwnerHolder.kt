package com.rokt.core.composablescoped

import androidx.compose.runtime.Stable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.eventFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

@Stable
internal class ComposableScopedViewModelStoreOwnerHolder : ViewModel() {
    private var heldComposableScopedViewModelStoreOwners = mapOf<String, ComposableScopedViewModelStoreOwner>()

    operator fun get(composableKey: String, key: Any?, lifecycle: Lifecycle) =
        heldComposableScopedViewModelStoreOwners[composableKey]?.let {
            it.update(key, lifecycle)
            it.viewModelStoreOwner
        } ?: run {
            val composableScopedViewModelStoreOwner = ComposableScopedViewModelStoreOwner(composableKey, key, lifecycle)
            heldComposableScopedViewModelStoreOwners += composableKey to composableScopedViewModelStoreOwner
            composableScopedViewModelStoreOwner.viewModelStoreOwner
        }

    fun holdComposableScopedViewModelStoreOwner(composableKey: String) {
        heldComposableScopedViewModelStoreOwners[composableKey]?.setComposablePresent(true)
    }

    fun releaseComposableScopedViewModelStoreOwner(composableKey: String) {
        heldComposableScopedViewModelStoreOwners[composableKey]?.setComposablePresent(false)
    }

    override fun onCleared() {
        heldComposableScopedViewModelStoreOwners.keys.forEach {
            clearComposableScopedViewModelStore(it)
        }
        super.onCleared()
    }

    private fun clearComposableScopedViewModelStore(composableKey: String) {
        heldComposableScopedViewModelStoreOwners[composableKey]?.let {
            it.clear()
            heldComposableScopedViewModelStoreOwners -= composableKey
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private inner class ComposableScopedViewModelStoreOwner(
        private val composableKey: String,
        private var key: Any?,
        initialLifecycle: Lifecycle,
    ) {
        val viewModelStoreOwner: ViewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }

        private val supervisor: CompletableJob = SupervisorJob()
        private var coroutineScope: CoroutineScope = viewModelScope + supervisor
        private val scopedComposableLifecycle = MutableStateFlow<Lifecycle?>(initialLifecycle)
        private val scopedComposableIsPresentInComposition = MutableSharedFlow<Boolean>()

        init {
            coroutineScope.launch {
                scopedComposableLifecycle
                    .flatMapLatest { lifecycle -> lifecycle?.eventFlow ?: emptyFlow() }
                    .collectLatest {
                        if (it == Lifecycle.Event.ON_DESTROY) {
                            scopedComposableLifecycle.update { null }
                        }
                    }
            }
            coroutineScope.launch {
                scopedComposableIsPresentInComposition.collectLatest { isPresent ->
                    when {
                        isPresent || scopedComposableLifecycle.value == null -> return@collectLatest
                        scopedComposableLifecycle.value?.currentState == Lifecycle.State.RESUMED -> {
                            clearComposableScopedViewModelStore(composableKey)
                        }

                        else -> {
                            scopedComposableLifecycle
                                .flatMapLatest { lifecycle -> lifecycle?.eventFlow ?: emptyFlow() }
                                .firstOrNull { it == Lifecycle.Event.ON_RESUME } ?: return@collectLatest
                            clearComposableScopedViewModelStore(composableKey)
                        }
                    }
                }
            }
        }

        fun clear() {
            supervisor.cancelChildren()
            viewModelStoreOwner.viewModelStore.clear()
        }

        fun update(key: Any?, lifecycle: Lifecycle) {
            if (key != this.key) {
                this.key = key
                viewModelStoreOwner.viewModelStore.clear()
            }
            scopedComposableLifecycle.update { lifecycle }
        }

        fun setComposablePresent(isPresent: Boolean) {
            coroutineScope.launch { scopedComposableIsPresentInComposition.emit(isPresent) }
        }
    }
}
