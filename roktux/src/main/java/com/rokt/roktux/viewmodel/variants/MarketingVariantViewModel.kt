package com.rokt.roktux.viewmodel.variants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.rokt.modelmapper.mappers.ModelMapper
import com.rokt.roktux.viewmodel.base.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class MarketingViewModel(
    val currentOffer: Int,
    modelMapper: ModelMapper,
    private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel<MarketingVariantContract.LayoutVariantEvent, MarketingVariantUiState, MarketingVariantContract.LayoutVariantEffect>() {
    private var offerViewedJob: Job? = null

    init {
        val slot = modelMapper.getSavedExperience()?.plugins?.getOrNull(
            0,
        )?.slots?.getOrNull(currentOffer)
        val layoutVariantSchema = slot?.layoutVariant?.layoutVariantSchema
        val creativeCopy = slot?.offer?.creative?.copy
        if (layoutVariantSchema != null && creativeCopy != null) {
            setSuccessState(MarketingVariantUiState(layoutVariantSchema, creativeCopy))
        }
    }

    override suspend fun handleEvents(event: MarketingVariantContract.LayoutVariantEvent) {
        when (event) {
            is MarketingVariantContract.LayoutVariantEvent.OfferVisibilityChanged -> {
                handleOfferVisibilityChanged(event)
            }
        }
    }

    private fun handleOfferVisibilityChanged(
        event: MarketingVariantContract.LayoutVariantEvent.OfferVisibilityChanged,
    ) {
        offerViewedJob?.takeIf { !event.visible && it.isActive }?.cancel()
        if (event.visible && offerViewedJob == null) {
            offerViewedJob = viewModelScope.launch(ioDispatcher) {
                delay(VISIBILITY_CHECKPOINT_MILLIS)
                setEffect { MarketingVariantContract.LayoutVariantEffect.SetSignalViewed(event.offerId) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancelChildren()
    }

    class MarketingViewModelFactory(
        private val currentOffer: Int,
        private val modelMapper: ModelMapper,
        private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(MarketingViewModel::class.java)) {
                return MarketingViewModel(currentOffer, modelMapper, ioDispatcher) as T
            }
            throw IllegalArgumentException("Unknown ViewModel type")
        }
    }

    companion object {
        private const val VISIBILITY_CHECKPOINT_MILLIS = 1000L
    }
}
