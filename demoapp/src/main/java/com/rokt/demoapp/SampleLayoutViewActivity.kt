package com.rokt.demoapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rokt.demoapp.util.getExperienceResponse
import com.rokt.roktux.RoktLayoutView
import com.rokt.roktux.RoktUxConfig

/**
 * Renders a bundled experience through the View-system entry point, mirroring
 * `SampleViewController` on iOS.
 */
class SampleLayoutViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val destination = SampleDestination.ViewSystem
        val layoutView = RoktLayoutView(this, location = destination.location)
        setContentView(layoutView)

        layoutView.loadLayout(
            experienceResponse = getExperienceResponse(destination.asset),
            roktUxConfig = RoktUxConfig.builder().edgeToEdgeDisplay(false).build(),
            onUxEvent = { event ->
                handleUxEvent(
                    context = this,
                    event = event,
                    onFinished = { finish() },
                    onFailure = {
                        Toast.makeText(this, R.string.layout_failed_to_load, Toast.LENGTH_LONG).show()
                        finish()
                    },
                )
            },
            onPlatformEvent = {
                // A production integration forwards these to the Rokt API. This sample renders offline.
            },
        )
    }
}
