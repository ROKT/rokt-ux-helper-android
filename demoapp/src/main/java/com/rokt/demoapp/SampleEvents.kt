package com.rokt.demoapp

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.rokt.demoapp.util.customTabsIntent
import com.rokt.demoapp.util.openExternally
import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.roktux.event.RoktUxEvent

/**
 * Tracks the single [RoktUxEvent.OpenUrl] a host is waiting on a browser-dismissal signal for,
 * so that signal can be routed back to the right event's `onClose` — and ignored otherwise.
 *
 * There is at most one outstanding link at a time: this sample only ever has one layout on
 * screen, and RoktUX doesn't fire a second `OpenUrl` while a browser is already open for the
 * first.
 */
private class OutstandingOpenUrl {
    private var outstanding: RoktUxEvent.OpenUrl? = null

    fun track(event: RoktUxEvent.OpenUrl) {
        outstanding = event
    }

    /** Consumes and returns the outstanding event, or null if nothing is outstanding. */
    fun consume(): RoktUxEvent.OpenUrl? = outstanding.also { outstanding = null }
}

/**
 * Owns the "was the browser actually dismissed" bookkeeping for a single host (an Activity, or a
 * Composable's lifetime). Two mechanisms feed it, because Custom Tabs and the external browser
 * give back fundamentally different signals:
 *
 * - Custom Tabs runs in the same task, so an `ActivityResultLauncher` gets a real Activity
 *   result when it closes. Route that callback to [onCustomTabResult].
 * - The external browser runs in a separate task — there is no Activity result at all. The only
 *   signal is the host resuming. Route the host's `onResume` (Activity override, or a Compose
 *   `ON_RESUME` lifecycle observer) to [onHostResumed]. Because [OutstandingOpenUrl.consume]
 *   returns null unless [openExternally] actually tracked a link first, ordinary foregrounding —
 *   configuration change, notification dismissal, switching apps and back — is a no-op instead
 *   of a false `onClose`.
 */
class BrowserSession {
    private val pendingCustomTab = OutstandingOpenUrl()
    private val pendingExternal = OutstandingOpenUrl()

    /** Launches [event] in Custom Tabs via [customTabLauncher] and tracks it for [onCustomTabResult]. */
    fun openInCustomTab(customTabLauncher: ActivityResultLauncher<Intent>, event: RoktUxEvent.OpenUrl): Result<Unit> =
        runCatching { customTabLauncher.launch(customTabsIntent(event.url)) }
            .onSuccess { pendingCustomTab.track(event) }

    /** Hands [event] to the external browser and tracks it for [onHostResumed]. */
    fun openExternally(context: Context, event: RoktUxEvent.OpenUrl): Result<Unit> =
        context.openExternally(event.url).onSuccess { pendingExternal.track(event) }

    /** Call from the host's Custom Tabs `ActivityResultLauncher` callback, regardless of result code. */
    fun onCustomTabResult() {
        pendingCustomTab.consume()?.let { it.onClose(it.id) }
    }

    /**
     * Call from the host's `onResume` (Activity) or an `ON_RESUME` lifecycle observer (Compose).
     * See the class doc for why this is safe against unrelated resumes.
     */
    fun onHostResumed() {
        pendingExternal.consume()?.let { it.onClose(it.id) }
    }
}

/**
 * Handles the UX events a host app has to deal with, mirroring `SampleViewModel.handleURL`
 * and the event switch in `SampleView` on iOS.
 *
 * @param browserSession the host's [BrowserSession], tracking outstanding `OpenUrl` events across
 *   both `onCustomTabResult` and `onHostResumed` — see that class for how the actual dismissal
 *   signal reaches it.
 * @param customTabLauncher the host's `ActivityResultLauncher` for `ActivityResultContracts.StartActivityForResult`,
 *   whose callback must call `browserSession.onCustomTabResult()`.
 * @param onFinished invoked when the layout closes, so the host can dismiss its container.
 * @param onFailure invoked when the layout could not be shown.
 */
fun handleUxEvent(
    context: Context,
    event: RoktUxEvent,
    browserSession: BrowserSession,
    customTabLauncher: ActivityResultLauncher<Intent>,
    onFinished: () -> Unit,
    onFailure: () -> Unit,
) {
    when (event) {
        is RoktUxEvent.LayoutCompleted, is RoktUxEvent.LayoutClosed -> onFinished()

        is RoktUxEvent.LayoutFailure -> {
            // NoOffers is a normal outcome, not an integration bug — quote sessionId when reporting
            // the others to your account manager.
            Log.d("RoktSample", "Layout failed: ${event.reason}, session ${event.sessionId}")
            onFailure()
        }

        is RoktUxEvent.OpenUrl -> {
            val opened = when (event.type) {
                OpenLinks.Externally -> browserSession.openExternally(context, event)

                OpenLinks.Internally -> browserSession.openInCustomTab(customTabLauncher, event)

                // Passthrough hands the URL back for the app to route itself — a deep link, or a
                // screen of its own. This sample has nowhere to route to, so it falls back.
                OpenLinks.Passthrough -> browserSession.openInCustomTab(customTabLauncher, event)
            }
            // onClose now fires when the browser is actually dismissed — via
            // BrowserSession.onCustomTabResult or onHostResumed — not here on launch success.
            // Launch success only rules out onError; it says nothing about dismissal.
            opened.onFailure { event.onError(event.id, it) }
        }

        else -> Unit // Remaining UX events are informational for this sample.
    }
}
