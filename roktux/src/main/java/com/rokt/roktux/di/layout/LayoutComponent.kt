package com.rokt.roktux.di.layout

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.font.FontFamily
import coil3.ImageLoader
import com.rokt.core.di.Component
import com.rokt.modelmapper.model.NetworkExperienceResponse
import com.rokt.roktux.RoktViewState
import com.rokt.roktux.event.RoktPlatformEvent
import com.rokt.roktux.event.RoktUxEvent
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.CoroutineDispatcher

internal class LayoutComponent(
    experienceResponse: String?,
    parsedExperienceResponse: NetworkExperienceResponse?,
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
    domainStates: Map<String, Int>,
    edgeToEdgeDisplay: Boolean,
    completedDevicePayCartItemIds: Set<String> = emptySet(),
    mainDispatcher: CoroutineDispatcher,
    ioDispatcher: CoroutineDispatcher,
) : Component(
    listOf(
        LayoutModule(
            experienceResponse,
            parsedExperienceResponse,
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
            domainStates,
            edgeToEdgeDisplay,
            completedDevicePayCartItemIds,
            mainDispatcher,
            ioDispatcher,
        ),
    ),
)

internal val LocalLayoutComponent = compositionLocalOf<LayoutComponent> { error("No app provider found!") }
internal val LocalFontFamilyProvider =
    compositionLocalOf<ImmutableMap<String, FontFamily>> { error("No FontFamily found!") }
