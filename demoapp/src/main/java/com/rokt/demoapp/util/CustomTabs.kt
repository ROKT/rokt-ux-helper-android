package com.rokt.demoapp.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

/** In-app browser, matching `SafariWebView` in the iOS sample. */
fun Context.openInCustomTab(url: String): Result<Unit> = runCatching {
    CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))
}

/**
 * Builds (without starting) the Custom Tabs launch intent, so a caller can route it through an
 * `ActivityResultLauncher` and observe the result Chrome sends back when the tab is dismissed.
 * [openInCustomTab] above calls `launchUrl` directly and has no way to signal that; this is for
 * callers — such as [com.rokt.demoapp.BrowserSession] — that need the dismissal signal.
 */
fun customTabsIntent(url: String): Intent = CustomTabsIntent.Builder().build().intent.apply {
    data = Uri.parse(url)
}

/** Hands the URL to the system browser, matching `UIApplication.shared.open` on iOS. */
fun Context.openExternally(url: String): Result<Unit> = runCatching {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}
