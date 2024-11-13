package com.rokt.demoapp.ui.screen.tutorials.three

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rokt.demoapp.BuildConfig
import com.rokt.demoapp.ui.state.RoktDemoErrorTypes
import com.rokt.demoapp.ui.state.UiState
import com.rokt.demoapp.ui.state.ViewState
import com.rokt.networkhelper.data.RoktNetwork
import com.rokt.networkhelper.model.ExperienceRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorialThreeViewModel @Inject constructor() : ViewModel() {
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
    private val _state: MutableStateFlow<UiState<ViewState>> =
        MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState<ViewState>>
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
                _state.value = UiState(data = ViewState(it, location))
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
}
