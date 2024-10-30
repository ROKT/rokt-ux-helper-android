package com.rokt.roktux.utils

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.rokt.roktux.viewmodel.layout.LayoutContract

internal class InternalActivityResultContract() :
    ActivityResultContract<LayoutContract.LayoutEffect.OpenUrlInternal, () -> Unit>() {
    private var id: String = ""
    private var onClose: (String) -> Unit = {}
    override fun createIntent(context: Context, input: LayoutContract.LayoutEffect.OpenUrlInternal): Intent {
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

    override fun parseResult(resultCode: Int, intent: Intent?): () -> Unit {
        return { onClose(id) }
    }
}
