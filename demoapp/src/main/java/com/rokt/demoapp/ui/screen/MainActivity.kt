package com.rokt.demoapp.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.rokt.demoapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getStringExtra(PREVIEW_PARAM_EXTRA)?.let(viewModel::updatePreviewParameter)
        intent.data?.getQueryParameter(URI_QUERY_PARAM)?.let(viewModel::updatePreviewParameter)

        // Draw behind status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (savedInstanceState != null) {
            installSplashScreen()
        }
        setTheme(R.style.Theme_RoktDemo)
        setContent { RoktDemoApp(viewModel) }
    }

    companion object {
        private const val PREVIEW_PARAM_EXTRA = "preview"
        private const val URI_QUERY_PARAM = "config"
    }
}
