package com.rokt.demoapp.ui.screen.tutorials.seven

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.rokt.demoapp.R
import com.rokt.demoapp.ui.state.UiContent
import com.rokt.roktux.RoktLayoutView
import com.rokt.roktux.RoktUx
import com.rokt.roktux.RoktUxConfig
import kotlinx.coroutines.launch

class TutorialSevenActivity : AppCompatActivity() {

    private val viewModel: TutorialSevenViewModel by viewModels()
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
                    val content = state.data
                    if (content is UiContent.ExperienceContent) {
                        roktLayoutView.loadLayout(
                            experienceResponse = content.experienceResponse,
                            roktUxConfig = RoktUxConfig.builder().build(),
                            onUxEvent = { event ->
                                println("RoktEvent: onUxEvent received $event")
                            },
                            onPlatformEvent = { platformEvents ->
                                println(
                                    "RoktEvent: onPlatformEvent received ${
                                        platformEvents.toJsonString()
                                    }",
                                )
                                viewModel.handlePlatformEvent(platformEvents.toJsonString())
                            },
                        )
                    }
                }
            }
        }

        val integrationInfo = RoktUx.getIntegrationConfig(this).toJsonString()
        lifecycleScope.launch {
            viewModel.handleInitialLoad(integrationInfo)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, TutorialSevenActivity::class.java)
            context.startActivity(intent)
        }
    }
}
