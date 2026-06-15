package com.rokt.roktux.testutil

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.rokt.core.testutils.annotations.DCUI_COMPONENT_TAG
import com.rokt.modelmapper.utils.ROKT_ICONS_FONT_FAMILY
import com.rokt.roktux.R
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.di.layout.LayoutComponent
import com.rokt.roktux.di.layout.LocalFontFamilyProvider
import com.rokt.roktux.di.layout.LocalLayoutComponent
import com.rokt.roktux.imagehandler.NetworkStrategy
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Dispatchers

internal class MutableOfferStateController {
    private var updateCustomState: ((String, Int) -> Unit)? = null

    fun setCustomState(key: String, value: Int) {
        updateCustomState?.invoke(key, value) ?: error("Mutable offer state renderer is not ready")
    }

    internal fun bind(updateCustomState: (String, Int) -> Unit) {
        this.updateCustomState = updateCustomState
    }
}

internal fun BaseDcuiEspressoTest.renderParsedModelWithMutableOfferState(
    initialCustomState: PersistentMap<String, Int> = persistentMapOf(),
    initialOfferCustomStates: PersistentMap<String, PersistentMap<String, Int>> = persistentMapOf(),
    initialActiveCatalogItemIndex: Int = 0,
    testTag: String = DCUI_COMPONENT_TAG,
): MutableOfferStateController {
    val controller = MutableOfferStateController()
    val factory = LayoutUiModelFactory()
    composeTestRule.setContent {
        var customState by remember { mutableStateOf(initialCustomState) }
        var offerCustomStates by remember { mutableStateOf(initialOfferCustomStates) }
        var activeCatalogItemIndex by remember { mutableIntStateOf(initialActiveCatalogItemIndex) }
        SideEffect {
            controller.bind { key, value ->
                customState = customState.put(key, value)
            }
        }

        CompositionLocalProvider(
            LocalLayoutComponent provides LayoutComponent(
                experienceResponse = "",
                parsedExperienceResponse = null,
                location = "",
                startTimeStamp = System.currentTimeMillis(),
                onUxEvent = {},
                onPlatformEvent = {},
                onViewStateChange = {},
                imageLoader = NetworkStrategy().getImageLoader(LocalContext.current),
                handleUrlByApp = true,
                currentOffer = 0,
                customStates = mapOf(),
                offerCustomStates = mapOf(),
                domainStates = mapOf(),
                edgeToEdgeDisplay = false,
                mainDispatcher = Dispatchers.Main,
                ioDispatcher = Dispatchers.IO,
            ),
            LocalFontFamilyProvider provides persistentMapOf(
                "roboto" to FontFamily.Default,
                ROKT_ICONS_FONT_FAMILY to FontFamily(Font(resId = R.font.rokt_icons)),
            ),
        ) {
            factory.CreateComposable(
                model = dcuiComponentRule.uiModel,
                modifier = Modifier.testTag(testTag),
                isPressed = false,
                offerState = OfferUiState(
                    currentOfferIndex = 0,
                    lastOfferIndex = 0,
                    viewableItems = 1,
                    creativeCopy = persistentMapOf(),
                    breakpoints = persistentMapOf("default" to 0),
                    customState = customState,
                    offerCustomStates = offerCustomStates,
                    activeCatalogItemIndex = activeCatalogItemIndex,
                ),
                isDarkModeEnabled = false,
                breakpointIndex = 0,
            ) { event ->
                dcuiComponentRule.capturedEvents.add(event)
                when (event) {
                    is LayoutContract.LayoutEvent.SetCustomState -> {
                        customState = customState.put(event.key, event.value)
                    }

                    is LayoutContract.LayoutEvent.SetOfferCustomState -> {
                        offerCustomStates = offerCustomStates.put(
                            event.offerId.toString(),
                            event.customState.toPersistentMap(),
                        )
                    }

                    is LayoutContract.LayoutEvent.SetActiveCatalogItem -> {
                        activeCatalogItemIndex = event.index
                    }

                    else -> Unit
                }
            }
        }
    }
    return controller
}
