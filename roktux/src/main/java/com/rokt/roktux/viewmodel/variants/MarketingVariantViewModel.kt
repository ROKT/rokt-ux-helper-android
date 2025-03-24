package com.rokt.roktux.viewmodel.variants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.rokt.modelmapper.mappers.ModelMapper
import com.rokt.roktux.viewmodel.base.BaseViewModel
import com.rokt.roktux.viewmodel.layout.LayoutContract
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

internal class MarketingViewModel(
    val currentOffer: Int,
    modelMapper: ModelMapper,
    private val ioDispatcher: CoroutineDispatcher,
    private var customState: Map<String, Int>,
) : BaseViewModel<LayoutContract.LayoutEvent, MarketingVariantUiState, MarketingVariantContract.LayoutVariantEffect>() {
    private var offerViewedJob: Job? = null
    private var signalViewedSent = AtomicBoolean(false)

    init {
        val slot = modelMapper.getSavedExperience()?.plugins?.getOrNull(
            0,
        )?.slots?.getOrNull(currentOffer)
        val layoutVariantSchema = slot?.layoutVariant?.layoutVariantSchema
        val creativeCopy = slot?.offer?.creative?.copy
        if (layoutVariantSchema != null && creativeCopy != null) {
            setSuccessState(MarketingVariantUiState(layoutVariantSchema, creativeCopy, customState.toImmutableMap()))
        }
    }

    override suspend fun handleEvents(event: LayoutContract.LayoutEvent) {
        when (event) {
            is LayoutContract.LayoutEvent.OfferVisibilityChanged -> {
                handleOfferVisibilityChanged(event)
            }

            is LayoutContract.LayoutEvent.SetCustomState -> {
                updateCustomState(event.key, event.value)
                setEffect {
                    MarketingVariantContract.LayoutVariantEffect.PropagateEvent(
                        LayoutContract.LayoutEvent.SetOfferCustomState(
                            currentOffer,
                            customState,
                        ),
                    )
                }
            }

            is LayoutContract.LayoutEvent.UserInteracted -> {
                offerViewedJob?.cancel()
                if (signalViewedSent.compareAndSet(false, true)) {
                    setEffect { MarketingVariantContract.LayoutVariantEffect.SetSignalViewed(currentOffer) }
                }
            }

            else -> {
                setEffect { MarketingVariantContract.LayoutVariantEffect.PropagateEvent(event) }
            }
        }
    }

    private fun handleOfferVisibilityChanged(event: LayoutContract.LayoutEvent.OfferVisibilityChanged) {
        offerViewedJob?.takeIf { !event.visible && it.isActive }?.cancel()
        if (event.visible && offerViewedJob == null) {
            offerViewedJob = viewModelScope.launch(ioDispatcher) {
                delay(VISIBILITY_CHECKPOINT_MILLIS)
                if (signalViewedSent.compareAndSet(false, true)) {
                    setEffect { MarketingVariantContract.LayoutVariantEffect.SetSignalViewed(event.offerId) }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancelChildren()
    }

    private fun updateCustomState(key: String, value: Int) {
        customState += (key to value)
        updateState { currentUiState ->
            currentUiState.copy(
                customState = customState.toImmutableMap(),
            )
        }
    }

    class MarketingViewModelFactory(
        private val currentOffer: Int,
        private val modelMapper: ModelMapper,
        private val ioDispatcher: CoroutineDispatcher,
        private var customState: Map<String, Int>,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(MarketingViewModel::class.java)) {
                return MarketingViewModel(currentOffer, modelMapper, ioDispatcher, customState) as T
            }
            throw IllegalArgumentException("Unknown ViewModel type")
        }
    }

    companion object {
        private const val VISIBILITY_CHECKPOINT_MILLIS = 1000L
    }
}
