package com.rokt.roktux.viewmodel.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import coil.ImageLoader
import com.rokt.roktux.di.layout.LayoutComponent
import com.rokt.roktux.event.RoktPlatformEvent
import com.rokt.roktux.event.RoktUxEvent

internal class DIComponentViewModel(
    private val experienceResponse: String,
    private val location: String,
    private val onUxEvent: (uxEvent: RoktUxEvent) -> Unit,
    private val onPlatformEvent: (platformEvents: List<RoktPlatformEvent>) -> Unit,
    private val imageLoader: ImageLoader,
    private val currentOffer: Int,
    private val customState: Map<String, Int>,
    private val handleUrlByApp: Boolean,
) : ViewModel() {

    val component = LayoutComponent(
        experienceResponse,
        location,
        onUxEvent,
        onPlatformEvent,
        imageLoader,
        handleUrlByApp,
        currentOffer,
        customState,
    )

    class DIComponentViewModelFactory(
        private val experienceResponse: String,
        private val location: String,
        private val uxEvent: (uxEvent: RoktUxEvent) -> Unit,
        private val platformEvent: (platformEvents: List<RoktPlatformEvent>) -> Unit,
        private val imageLoader: ImageLoader,
        private val currentOffer: Int,
        private val customState: Map<String, Int>,
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
                    imageLoader = imageLoader,
                    currentOffer = currentOffer,
                    customState = customState,
                    handleUrlByApp = handleUrlByApp,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel type")
        }
    }
}
