package com.rokt.demoapp.ui.screen.tutorials.eight

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.rokt.demoapp.R
import com.rokt.demoapp.ui.screen.tutorials.TutorialViewModel
import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.roktux.RoktLayoutView
import com.rokt.roktux.RoktUx
import com.rokt.roktux.RoktUxConfig
import com.rokt.roktux.event.RoktUxEvent
import kotlinx.coroutines.launch

class TutorialEightActivity : AppCompatActivity() {

    private val viewModel: TutorialViewModel by viewModels()
    private var currentUrlEvent: RoktUxEvent.OpenUrl? = null

    // Register ActivityResultLauncher for Custom Tabs
    private val customTabLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        currentUrlEvent?.onClose?.invoke(currentUrlEvent?.id ?: "")
        currentUrlEvent = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        val backButton: ImageView = findViewById(R.id.back_button)
        val loadingIndicator: ProgressBar = findViewById(R.id.loading_indicator)
        val errorMessage: TextView = findViewById(R.id.error_message)
        val roktLayoutView: RoktLayoutView = findViewById(R.id.roktLayoutView)

        backButton.setOnClickListener { this.onBackPressed() }

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                loadingIndicator.isVisible = state.loading
                errorMessage.isVisible = state.error != null
                errorMessage.text = state.error?.toString()

                if (state.hasData) {
                    roktLayoutView.loadLayout(
                        experienceResponse = state.data!!.experienceResponse,
                        roktUxConfig = RoktUxConfig.builder().build(),
                        onUxEvent = { event ->
                            println("RoktEvent: onUxEvent received $event")
                            handleRoktUxEvent(event)
                        },
                        onPlatformEvent = { platformEvent ->
                            println("RoktEvent: onPlatformEvent received $platformEvent")
                        },
                    )
                }
            }
        }

        val integrationInfo = RoktUx.getIntegrationConfig(this).toJsonString()
        lifecycleScope.launch {
            viewModel.handleInitialLoad(integrationInfo)
        }
    }

    private fun handleRoktUxEvent(event: RoktUxEvent) {
        if (event is RoktUxEvent.OpenUrl) {
            currentUrlEvent = event
            if (event.type != OpenLinks.Externally) {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(event.url)))
                } catch (e: Exception) {
                    currentUrlEvent = null
                    event.onError(event.id, e)
                }
            } else {
                // Launch Custom Tab
                val customTabsIntent = CustomTabsIntent.Builder().build()
                val intent = customTabsIntent.intent.apply {
                    data = Uri.parse(event.url)
                }
                customTabLauncher.launch(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        currentUrlEvent?.let { event ->
            event.onClose(event.id)
            currentUrlEvent = null
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, TutorialEightActivity::class.java)
            context.startActivity(intent)
        }
    }
}
