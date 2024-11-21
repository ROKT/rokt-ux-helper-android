package com.rokt.demoapp.ui.screen.tutorials.nine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rokt.demoapp.BuildConfig
import com.rokt.demoapp.ui.state.RoktDemoErrorTypes
import com.rokt.demoapp.ui.state.UiContent
import com.rokt.demoapp.ui.state.UiState
import com.rokt.networkhelper.data.RoktNetwork
import com.rokt.networkhelper.model.ExperienceRequest
import com.rokt.roktux.event.RoktUxEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class TutorialNineViewModel @Inject constructor() : ViewModel() {
    private val viewName = BuildConfig.VIEW_NAME // Update value in local.properties
    private val roktTagId = BuildConfig.ROKT_TAG_ID // Update value in local.properties
    private val attributes = mapOf(
        "lastname" to "Smith",
        "mobile" to "(323) 867-5309",
        "country" to "AU",
        "noFunctional" to "true",
        "pageinit" to "${System.currentTimeMillis()}",
        "sandbox" to "true",
    ) // Add your attributes here
    private val location = "Location1" // Add your location here
    private val _state: MutableStateFlow<UiState<UiContent>> =
        MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState<UiContent>>
        get() = _state

    private var integrationInfo: String = ""

    fun handleInitialLoad(integrationInfo: String) {
        this.integrationInfo = integrationInfo
        viewModelScope.launch {
            val experienceRequest = ExperienceRequest(
                pageIdentifier = viewName,
                attributes = attributes,
                integrationInfo = integrationInfo,
            )
            RoktNetwork.experience(roktTagId, experienceRequest).onSuccess {
                _state.value = UiState(data = UiContent.ExperienceContent(it, location))
            }.onFailure {
                _state.value = UiState(error = RoktDemoErrorTypes.NETWORK)
            }
        }
    }

    fun handlePlatformEvent(events: String) {
        viewModelScope.launch {
            RoktNetwork.postEvents(events)
        }
    }

    fun handleUXEvent(event: RoktUxEvent) {
        viewModelScope.launch {
            if (event is RoktUxEvent.CartItemInstantPurchase) {
                _state.value = UiState(loading = true)
                try {
                    withContext(Dispatchers.IO) {
                        delay(5000)
                        // Simulate a failure scenario 1 out of 5 times
                        val random = Random.nextInt(5)
                        if (random == 0) {
                            throw Exception("Purchase failed")
                        }
                    }
                    _state.value = UiState(data = UiContent.PaymentSuccessContent("Purchase completed"))
                } catch (e: Exception) {
                    _state.value = UiState(data = UiContent.PaymentFailureContent(e.message.orEmpty()))
                }
            }
        }
    }
}
