package com.rokt.demoapp.ui.screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    val previewParameterString = mutableStateOf<String?>(null)

    fun updatePreviewParameter(intentData: String) {
        previewParameterString.value = intentData
    }
}
