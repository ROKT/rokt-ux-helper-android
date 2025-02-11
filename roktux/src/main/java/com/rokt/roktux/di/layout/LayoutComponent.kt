package com.rokt.roktux.di.layout

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.font.FontFamily
import coil.ImageLoader
import com.rokt.core.di.Component
import com.rokt.roktux.RoktViewState
import com.rokt.roktux.event.RoktPlatformEvent
import com.rokt.roktux.event.RoktUxEvent
import kotlinx.collections.immutable.ImmutableMap

internal class LayoutComponent(
    experienceResponse: String,
    location: String,
    startTimeStamp: Long,
    onUxEvent: (event: RoktUxEvent) -> Unit,
    onPlatformEvent: (platformEvents: List<RoktPlatformEvent>) -> Unit,
    onViewStateChange: (state: RoktViewState) -> Unit,
    imageLoader: ImageLoader,
    handleUrlByApp: Boolean,
    currentOffer: Int,
    customStates: Map<String, Int>,
    offerCustomStates: Map<String, Map<String, Int>>,
) : Component(
    listOf(
        LayoutModule(
            experienceResponse,
            location,
            startTimeStamp,
            onUxEvent,
            onPlatformEvent,
            onViewStateChange,
            imageLoader,
            handleUrlByApp,
            currentOffer,
            customStates,
            offerCustomStates,
        ),
    ),
)

internal val LocalLayoutComponent = compositionLocalOf<LayoutComponent> { error("No app provider found!") }
internal val LocalFontFamilyProvider =
    compositionLocalOf<ImmutableMap<String, FontFamily>> { error("No FontFamily found!") }
