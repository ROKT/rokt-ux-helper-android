package com.rokt.demoapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.rokt.demoapp.util.getExperienceResponse
import com.rokt.roktux.RoktLayout
import com.rokt.roktux.RoktUxConfig

/**
 * Renders a bundled experience response, mirroring `SampleView` on iOS.
 *
 * @param experienceAsset asset name without the `.json` extension.
 * @param location the element RoktUX targets, matching `target_element_selector` in the response.
 */
@Composable
fun SampleScreen(
    experienceAsset: String,
    location: String,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {},
    onFailure: () -> Unit = {},
) {
    val context = LocalContext.current
    val experienceResponse = remember(experienceAsset) { context.getExperienceResponse(experienceAsset) }

    RoktLayout(
        experienceResponse = experienceResponse,
        location = location,
        modifier = modifier,
        roktUxConfig = RoktUxConfig.builder().edgeToEdgeDisplay(false).build(),
        onUxEvent = { event ->
            handleUxEvent(context = context, event = event, onFinished = onFinished, onFailure = onFailure)
        },
        onPlatformEvent = {
            // A production integration forwards these to the Rokt API. This sample renders offline.
        },
    )
}

/** The experience responses bundled in `assets/`, shared with the iOS sample. */
object ExperienceAssets {
    /** Overlay layout carrying a `target_element_selector`. */
    const val TARGETED = "experience"

    /** Overlay layout with an empty selector. */
    const val OVERLAY = "experience-overlay"

    /** Bottom-sheet layout with an empty selector. */
    const val BOTTOM_SHEET = "experience-bottomsheet"

    /** Matches `target_element_selector` in [TARGETED]. */
    const val TARGETED_LOCATION = "RoktTest"
}
