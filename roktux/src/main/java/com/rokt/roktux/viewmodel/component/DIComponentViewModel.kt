package com.rokt.roktux.viewmodel.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import coil.ImageLoader
import com.rokt.roktux.RoktViewState
import com.rokt.roktux.di.layout.LayoutComponent
import com.rokt.roktux.event.RoktPlatformEvent
import com.rokt.roktux.event.RoktUxEvent

internal class DIComponentViewModel(
    private val experienceResponse: String,
    private val location: String,
    private val onUxEvent: (uxEvent: RoktUxEvent) -> Unit,
    private val onPlatformEvent: (platformEvents: List<RoktPlatformEvent>) -> Unit,
    private val onViewStateChange: (state: RoktViewState) -> Unit,
    private val imageLoader: ImageLoader,
    private val currentOffer: Int,
    private val customStates: Map<String, Int>,
    private val offerCustomStates: Map<String, Map<String, Int>>,
    private val handleUrlByApp: Boolean,
) : ViewModel() {

    val component = LayoutComponent(
        experienceResponse,
        location,
        onUxEvent,
        onPlatformEvent,
        onViewStateChange,
        imageLoader,
        handleUrlByApp,
        currentOffer,
        customStates,
        offerCustomStates,
    )

    class DIComponentViewModelFactory(
        private val experienceResponse: String,
        private val location: String,
        private val uxEvent: (uxEvent: RoktUxEvent) -> Unit,
        private val platformEvent: (platformEvents: List<RoktPlatformEvent>) -> Unit,
        private val viewStateChange: (state: RoktViewState) -> Unit,
        private val imageLoader: ImageLoader,
        private val currentOffer: Int,
        private val customStates: Map<String, Int>,
        private val offerCustomStates: Map<String, Map<String, Int>>,
        private val handleUrlByApp: Boolean,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(DIComponentViewModel::class.java)) {
                return DIComponentViewModel(
                    experienceResponse = experienceResponse,
                    location = location,
                    onUxEvent = uxEvent,
                    onPlatformEvent = platformEvent,
                    onViewStateChange = viewStateChange,
                    imageLoader = imageLoader,
                    currentOffer = currentOffer,
                    customStates = customStates,
                    offerCustomStates = offerCustomStates,
                    handleUrlByApp = handleUrlByApp,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel type")
        }
    }
}
