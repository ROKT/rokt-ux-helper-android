package com.rokt.roktux.di.variants.marketing

import com.rokt.roktux.di.core.Component
import com.rokt.roktux.di.layout.LayoutComponent

internal class MarketingComponent(component: LayoutComponent, currentOffer: Int, customStates: Map<String, Int>) :
    Component(
        listOf(
            MarketingModule(currentOffer, customStates),
        ),
        listOf(component),
    )
