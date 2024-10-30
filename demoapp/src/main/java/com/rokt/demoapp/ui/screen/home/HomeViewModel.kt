package com.rokt.demoapp.ui.screen.home

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.rokt.demoapp.R
import com.rokt.demoapp.utils.openInBrowser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    fun contactUsClicked(context: Context) {
        context.getString(R.string.link_contact_us).toUri().openInBrowser(context = context) {
            Log.e("Rokt", "Failed to open link")
        }
    }
}
