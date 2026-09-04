package com.rokt.demoapp

import android.content.Context
import android.util.Log
import com.rokt.demoapp.util.openExternally
import com.rokt.demoapp.util.openInCustomTab
import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.roktux.event.RoktUxEvent

/**
 * Handles the UX events a host app has to deal with, mirroring `SampleViewModel.handleURL`
 * and the event switch in `SampleView` on iOS.
 *
 * @param onFinished invoked when the layout closes, so the host can dismiss its container.
 * @param onFailure invoked when the layout could not be shown.
 */
fun handleUxEvent(context: Context, event: RoktUxEvent, onFinished: () -> Unit, onFailure: () -> Unit) {
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
                OpenLinks.Externally -> context.openExternally(event.url)

                OpenLinks.Internally -> context.openInCustomTab(event.url)

                // Passthrough hands the URL back for the app to route itself — a deep link, or a
                // screen of its own. This sample has nowhere to route to, so it falls back.
                OpenLinks.Passthrough -> context.openInCustomTab(event.url)
            }
            opened.fold(
                onSuccess = { event.onClose(event.id) },
                onFailure = { event.onError(event.id, it) },
            )
        }

        else -> Unit // Remaining UX events are informational for this sample.
    }
}
