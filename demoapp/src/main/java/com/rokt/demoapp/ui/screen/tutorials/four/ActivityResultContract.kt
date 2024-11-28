package com.rokt.demoapp.ui.screen.tutorials.four

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.rokt.roktux.event.RoktUxEvent

internal class InternalActivityResultContract : ActivityResultContract<RoktUxEvent.OpenUrl, () -> Unit>() {
    private var id: String = ""
    private var onClose: (String) -> Unit = {}
    override fun createIntent(context: Context, input: RoktUxEvent.OpenUrl): Intent {
        val customTabsIntentBuilder = CustomTabsIntent.Builder()
        return customTabsIntentBuilder.build().intent.apply {
            try {
                data = input.url.toUri()
                id = input.id
                onClose = input.onClose
            } catch (e: Exception) {
                input.onError(input.id, e)
            }
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): () -> Unit = { onClose(id) }
}
