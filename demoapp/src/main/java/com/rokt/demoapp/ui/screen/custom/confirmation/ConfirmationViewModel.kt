package com.rokt.demoapp.ui.screen.custom.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rokt.demoapp.ui.screen.custom.accountdetails.AccountDetails
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
class ConfirmationViewModel @Inject constructor() : ViewModel() {

    private val _state: MutableStateFlow<UiState<ViewState>> =
        MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState<ViewState>>
        get() = _state

    fun init(integrationInfo: String, accountDetails: AccountDetails, customerAttributes: Map<String, String>) {
        viewModelScope.launch {
            val experienceRequest = ExperienceRequest(
                pageIdentifier = accountDetails.viewName,
                attributes = customerAttributes,
                integrationInfo = integrationInfo,
            )
            RoktNetwork.experience(accountDetails.accountID, experienceRequest).onSuccess {
                _state.value = UiState(data = ViewState(it, accountDetails.placementLocation1))
            }.onFailure {
                _state.value = UiState(error = RoktDemoErrorTypes.NETWORK)
            }
        }
    }
}
