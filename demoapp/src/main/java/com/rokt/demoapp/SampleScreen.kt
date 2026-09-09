package com.rokt.demoapp

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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

    // BrowserSession.openInCustomTab/openExternally track "outstanding" state per SampleScreen
    // call; a fresh RoktLayout for a new experienceAsset should start with none.
    val browserSession = remember(experienceAsset) { BrowserSession() }

    // rememberLauncherForActivityResult resolves its host through LocalActivityResultRegistryOwner
    // rather than casting LocalContext.current to a ComponentActivity — SampleScreen is only ever
    // hosted inside MainActivity's setContent (see MainActivity.kt), which always provides one, so
    // this composable call is never conditional in practice. If SampleScreen is ever reused outside
    // a ComponentActivity host, this throws at composition time rather than silently degrading —
    // the check to add then is `LocalActivityResultRegistryOwner.current != null` around this call.
    val customTabLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        browserSession.onCustomTabResult()
    }

    // The external-browser path has no Activity result to observe — the host resuming is the
    // only signal that the user came back. onHostResumed() is a no-op unless openExternally
    // actually tracked a link first, so this doesn't fire on unrelated resumes (rotation,
    // notification dismissal, app switch).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, browserSession) {
        val observer = LifecycleEventObserver { _, lifecycleEvent ->
            if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
                browserSession.onHostResumed()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    RoktLayout(
        experienceResponse = experienceResponse,
        location = location,
        modifier = modifier,
        roktUxConfig = RoktUxConfig.builder().edgeToEdgeDisplay(false).build(),
        onUxEvent = { event ->
            handleUxEvent(
                context = context,
                event = event,
                browserSession = browserSession,
                customTabLauncher = customTabLauncher,
                onFinished = onFinished,
                onFailure = onFailure,
            )
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
