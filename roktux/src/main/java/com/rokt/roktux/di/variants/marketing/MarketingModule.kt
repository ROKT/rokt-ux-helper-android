package com.rokt.roktux.di.variants.marketing

import com.rokt.roktux.di.core.Module
import com.rokt.roktux.di.core.get
import com.rokt.roktux.di.layout.LayoutModule.Companion.IO
import com.rokt.roktux.viewmodel.variants.MarketingViewModel

internal class MarketingModule(currentOffer: Int, customState: Map<String, Int>) : Module() {
    init {
        this.provideModuleScoped {
            MarketingViewModel.MarketingViewModelFactory(
                currentOffer,
                get(),
                get(IO),
                customState,
            )
        }
    }
}
