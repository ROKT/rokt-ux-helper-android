package com.rokt.roktux.di.layout

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.font.FontFamily
import coil.ImageLoader
import com.rokt.roktux.di.core.Component
import com.rokt.roktux.event.RoktPlatformEvent
import com.rokt.roktux.event.RoktUxEvent
import kotlinx.collections.immutable.ImmutableMap

internal class LayoutComponent(
    experienceResponse: String,
    location: String,
    onUxEvent: (event: RoktUxEvent) -> Unit,
    onPlatformEvent: (platformEvents: List<RoktPlatformEvent>) -> Unit,
    imageLoader: ImageLoader,
    handleUrlByApp: Boolean,
    currentOffer: Int,
    customState: Map<String, Int>,
) : Component(
    listOf(
        LayoutModule(
            experienceResponse,
            location,
            onUxEvent,
            onPlatformEvent,
            imageLoader,
            handleUrlByApp,
            currentOffer,
            customState,
        ),
    ),
)

internal val LocalLayoutComponent = compositionLocalOf<LayoutComponent> { error("No app provider found!") }
internal val LocalFontFamilyProvider =
    compositionLocalOf<ImmutableMap<String, FontFamily>> { error("No FontFamily found!") }
