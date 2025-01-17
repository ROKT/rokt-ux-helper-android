package com.rokt.roktux.viewmodel.variants

import com.rokt.roktux.viewmodel.base.BaseContract
import com.rokt.roktux.viewmodel.layout.LayoutContract

internal class MarketingVariantContract {

    sealed interface LayoutVariantEffect : BaseContract.BaseEffect {
        data class SetSignalViewed(val offerId: Int) : LayoutVariantEffect
        data class PropagateEvent(val event: LayoutContract.LayoutEvent) : LayoutVariantEffect
    }
}
