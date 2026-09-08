package com.rokt.demoapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.rokt.demoapp.util.getExperienceResponse
import com.rokt.roktux.RoktLayoutView
import com.rokt.roktux.RoktUxConfig

/**
 * Renders a bundled experience through the View-system entry point, mirroring
 * `SampleViewController` on iOS.
 */
class SampleLayoutViewActivity : AppCompatActivity() {

    private val browserSession = BrowserSession()

    // Registering an ActivityResultLauncher must happen before the Activity reaches STARTED; a
    // field initializer is the standard way to guarantee that (see the Compose call site,
    // SampleScreen.kt, for the rememberLauncherForActivityResult equivalent).
    private val customTabLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        browserSession.onCustomTabResult()
    }

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
                    browserSession = browserSession,
                    customTabLauncher = customTabLauncher,
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

    // The external-browser path has no Activity result to observe — onResume is the only signal
    // that the user came back. browserSession.onHostResumed() is a no-op unless a link opened via
    // openExternally is actually outstanding, so this doesn't fire on unrelated resumes.
    override fun onResume() {
        super.onResume()
        browserSession.onHostResumed()
    }
}
