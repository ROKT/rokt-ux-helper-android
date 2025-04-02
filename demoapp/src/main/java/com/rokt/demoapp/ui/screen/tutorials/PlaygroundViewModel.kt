package com.rokt.demoapp.ui.screen.tutorials

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rokt.demoapp.data.AssetRepository
import com.rokt.demoapp.ui.state.RoktDemoErrorTypes
import com.rokt.demoapp.ui.state.UiContent
import com.rokt.demoapp.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PlaygroundViewModel @Inject constructor(private val assetRepository: AssetRepository) : ViewModel() {
    private val location = "Location1" // Add your location here
    private val _state: MutableStateFlow<UiState<UiContent>> =
        MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState<UiContent>>
        get() = _state

    fun handleInitialLoad() {
        viewModelScope.launch {
            // Read contents from partner_experiences.json assets file
            try {
                val input = assetRepository.getAssetContent("partner_experiences.json")
                val data = input.bufferedReader().use(BufferedReader::readText)
                _state.value =
                    UiState(data = UiContent.ExperienceContent(experienceResponse = data, location = location))
            } catch (_: IOException) {
                _state.value = UiState(error = RoktDemoErrorTypes.GENERAL)
            }
        }
    }
}
