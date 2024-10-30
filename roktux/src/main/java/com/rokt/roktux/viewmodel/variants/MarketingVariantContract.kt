package com.rokt.roktux.viewmodel.variants

import com.rokt.roktux.viewmodel.base.BaseContract

internal class MarketingVariantContract {

    sealed interface LayoutVariantEvent : BaseContract.BaseEvent {
        data class OfferVisibilityChanged(val offerId: Int, val visible: Boolean) : LayoutVariantEvent
    }

    sealed interface LayoutVariantEffect : BaseContract.BaseEffect {
        data class SetSignalViewed(val offerId: Int) : LayoutVariantEffect
    }
}
