package com.rokt.roktux.utils

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.rokt.roktux.ColorMode

internal fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER ?: "unknown"
    val model = Build.MODEL ?: "unknown"
    return if (model.startsWith(manufacturer)) {
        model.safeCapitalize()
    } else {
        manufacturer.safeCapitalize() + " " + model
    }.stripNonAscii()
}

@Composable
internal fun isSystemInDarkMode(colorMode: ColorMode?): Boolean = colorMode?.run {
    when (this) {
        ColorMode.LIGHT -> false
        ColorMode.DARK -> true
        else -> isSystemInDarkTheme()
    }
} ?: isSystemInDarkTheme()
