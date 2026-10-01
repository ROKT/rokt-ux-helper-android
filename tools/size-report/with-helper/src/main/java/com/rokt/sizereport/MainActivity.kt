package com.rokt.sizereport

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import com.rokt.roktux.RoktLayout
import com.rokt.roktux.RoktUxConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Text("Hello, world!")
                // Calls the rendering entry point so R8 keeps the layout pipeline, as a
                // partner integration would. The empty response is handled internally.
                RoktLayout(
                    experienceResponse = "",
                    location = "",
                    roktUxConfig = RoktUxConfig.builder().build(),
                    onUxEvent = { Log.d("SizeReport", "$it") },
                    onPlatformEvent = {},
                )
            }
        }
    }
}
