package com.rokt.demoapp.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

/** In-app browser, matching `SafariWebView` in the iOS sample. */
fun Context.openInCustomTab(url: String): Result<Unit> = runCatching {
    CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))
}

/** Hands the URL to the system browser, matching `UIApplication.shared.open` on iOS. */
fun Context.openExternally(url: String): Result<Unit> = runCatching {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}
