package com.rokt.demoapp.ui.screen.layouts

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rokt.demoapp.ui.screen.layouts.model.DemoLayoutConfig
import com.rokt.demoapp.ui.screen.layouts.model.DemoLayoutSlotConfig
import com.rokt.demoapp.ui.screen.layouts.model.DemoModeConfig
import com.rokt.demoapp.ui.screen.layouts.model.PreviewData
import com.rokt.demoapp.ui.state.RoktDemoErrorTypes
import com.rokt.demoapp.ui.state.UiState
import com.rokt.networkhelper.data.RoktNetwork
import com.rokt.networkhelper.model.ExperienceRequest
import com.rokt.roktux.RoktUx
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanQrViewModel @Inject constructor() : ViewModel() {

    private val _state: MutableStateFlow<UiState<ScanQrState>> =
        MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState<ScanQrState>>
        get() = _state

    init {
        _state.value = UiState(data = ScanQrState())
    }

    fun qrCodeScanned(rawData: String?) {
        if (rawData == null) {
            _state.update { UiState(data = ScanQrState()) }
        } else {
            try {
                val qrData = Gson().fromJson(rawData, PreviewData::class.java)
                _state.update {
                    UiState(
                        data = ScanQrState(
                            scannedData = qrData,
                            previewState = PreviewState.ScannedState(qrData),
                        ),
                    )
                }
            } catch (e: Exception) {
                _state.update { UiState(error = RoktDemoErrorTypes.QRCODE) }
            }
        }
    }

    fun uxHelperSdkSelected(context: Context) {
        viewModelScope.launch {
            _state.value.data?.scannedData?.let { data ->
                RoktNetwork.experience(
                    data.tagId,
                    ExperienceRequest(
                        pageIdentifier = "",
                        attributes = buildAttributes(data),
                        integrationInfo = RoktUx.getIntegrationConfig(context).toJsonString(),
                    ),
                )
                    .onSuccess { response ->
                        _state.update {
                            it.copy(
                                data = _state.value.data!!.copy(
                                    previewState = PreviewState.UxHelperSdkData(
                                        response,
                                    ),
                                ),
                            )
                        }
                    }
                    .onFailure { Log.e("Rokt", "$it") }
            }
        }
    }

    private fun buildAttributes(data: PreviewData): Map<String, String> {
        val attributes = mutableMapOf<String, String>()
        attributes[ATTRIBUTE_IS_DEMO] = true.toString()
        data.language?.let { attributes[ATTRIBUTE_LANGUAGE] = it }
        getDemoConfig(data)?.let { attributes[ATTRIBUTE_DEMO_CONFIG] = it }
        return attributes
    }
}

private fun getDemoConfig(previewData: PreviewData): String? {
    previewData.layoutVariantIds?.let { layoutVariantIds ->
        val slots = mutableListOf<DemoLayoutSlotConfig>()
        previewData.creativeIds.forEachIndexed { index, creativeId ->
            val slot = DemoLayoutSlotConfig(
                layoutVariantId = layoutVariantIds[index % layoutVariantIds.count()],
                creativeId = creativeId,
            )
            slots.add(slot)
        }
        val demoConfig = DemoModeConfig(
            layouts = listOf(
                DemoLayoutConfig(
                    layoutId = previewData.previewId,
                    versionId = previewData.versionId,
                    targetElementSelector = PREVIEW_PLACEHOLDER,
                    slots = slots,
                ),
            ),
        )
        return Gson().toJson(demoConfig)
    }
    return null
}

data class ScanQrState(
    val scannedData: PreviewData? = null,
    val previewState: PreviewState? = null,
)

sealed class PreviewState {
    data class ScannedState(val data: PreviewData) : PreviewState()
    data class UxHelperSdkData(val response: String) : PreviewState()
}

private const val ATTRIBUTE_IS_DEMO = "isDemo"
private const val ATTRIBUTE_LANGUAGE = "rokt.language"
private const val ATTRIBUTE_DEMO_CONFIG = "demoConfig"
const val PREVIEW_PLACEHOLDER = "#rokt-placeholder"
private const val EXECUTE_DELAY_SECONDS = 3L
