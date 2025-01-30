package com.rokt.roktux.viewmodel.layout

import com.rokt.modelmapper.hmap.HMap
import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.roktux.viewmodel.base.BaseContract

internal class LayoutContract {

    sealed interface LayoutEvent : BaseContract.BaseEvent {
        object LayoutInitialised : LayoutEvent
        object LayoutReady : LayoutEvent
        object LayoutInteractive : LayoutEvent
        object FirstOfferLoaded : LayoutEvent
        object UserInteracted : LayoutEvent
        data class CloseSelected(val isDismissed: Boolean) : LayoutEvent
        data class ResponseOptionSelected(
            val currentOffer: Int,
            val openLinks: OpenLinks,
            val responseOptionProperties: HMap,
            val shouldProgress: Boolean = false,
        ) : LayoutEvent

        data class UrlSelected(val url: String, val linkOpenTarget: OpenLinks) : LayoutEvent
        data class LayoutVariantSwiped(val currentOffer: Int) : LayoutEvent
        data class ViewableItemsChanged(val viewableItems: Int) : LayoutEvent
        data class SetCustomState(val key: String, val value: Int) : LayoutEvent
        data class SetOfferCustomState(val offerId: Int, val customState: Map<String, Int>) : LayoutEvent
        data class LayoutVariantNavigated(val targetOffer: Int) : LayoutEvent
        data class SetCurrentOffer(val currentOffer: Int) : LayoutEvent
        data class SignalViewed(val offerId: Int) : LayoutEvent
        data class OfferVisibilityChanged(val offerId: Int, val visible: Boolean) : LayoutEvent
        data class UiException(val throwable: Throwable) : LayoutEvent
    }

    sealed interface LayoutEffect : BaseContract.BaseEffect {
        object CloseLayout : LayoutEffect
        class OpenUrlExternal(
            val url: String,
            val id: String,
            val onClose: (id: String) -> Unit,
            val onError: (id: String, throwable: Throwable) -> Unit,
        ) : LayoutEffect

        class OpenUrlInternal(
            val url: String,
            val id: String,
            val onClose: (id: String) -> Unit,
            val onError: (id: String, throwable: Throwable) -> Unit,
        ) : LayoutEffect
    }
}
