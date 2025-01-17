package com.rokt.demoapp.ui.screen.tutorials.six

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
import com.rokt.demoapp.ui.screen.tutorials.TutorialViewModel
import com.rokt.demoapp.ui.state.UiContent
import com.rokt.roktux.FontItemStyle
import com.rokt.roktux.FontItemWeight
import com.rokt.roktux.ResourceFontItem
import com.rokt.roktux.RoktLayoutView
import com.rokt.roktux.RoktUx
import com.rokt.roktux.RoktUxConfig
import kotlinx.coroutines.launch

class TutorialSixActivity : AppCompatActivity() {

    private val viewModel: TutorialViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        val backButton: ImageView = findViewById(R.id.back_button)
        val loadingIndicator: ProgressBar = findViewById(R.id.loading_indicator)
        val errorMessage: TextView = findViewById(R.id.error_message)
        val roktLayoutView: RoktLayoutView = findViewById(R.id.roktLayoutView)
        val fontFamilyMap = mapOf(
            "lato" to listOf(
                ResourceFontItem(R.font.lato_regular, FontItemWeight.Normal, FontItemStyle.Normal),
                ResourceFontItem(R.font.lato_bold, FontItemWeight.Bold, FontItemStyle.Normal),
            ),
        )
        /*
           If the fonts are stored in the assets folder, you can use the following code to load the fonts:
            val fontFamilyMap2 = mapOf(
            "lato" to listOf(
                AssetFontItem("lato_regular", FontItemWeight.Normal, FontItemStyle.Normal),
                AssetFontItem("lato_bold", FontItemWeight.Bold, FontItemStyle.Normal),
            ),
        )*/

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
                            roktUxConfig = RoktUxConfig.builder().xmlFontFamilyMap(xmlFontFamilyMap = fontFamilyMap)
                                .build(),
                            onUxEvent = { event ->
                                println("RoktEvent: onUxEvent received $event")
                            },
                            onPlatformEvent = { platformEvent ->
                                println("RoktEvent: onPlatformEvent received $platformEvent")
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
            val intent = Intent(context, TutorialSixActivity::class.java)
            context.startActivity(intent)
        }
    }
}
