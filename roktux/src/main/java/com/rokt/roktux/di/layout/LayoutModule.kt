package com.rokt.roktux.di.layout

import coil.ImageLoader
import com.rokt.modelmapper.data.DataBinding
import com.rokt.modelmapper.data.DataBindingImpl
import com.rokt.modelmapper.mappers.ExperienceModelMapperImpl
import com.rokt.modelmapper.mappers.ModelMapper
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.di.core.Module
import com.rokt.roktux.di.core.get
import com.rokt.roktux.event.RoktPlatformEvent
import com.rokt.roktux.event.RoktUxEvent
import com.rokt.roktux.viewmodel.layout.LayoutViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class LayoutModule(
    private val experience: String,
    private val location: String,
    private val uxEvent: (uxEvent: RoktUxEvent) -> Unit,
    private val platformEvent: (platformEvents: List<RoktPlatformEvent>) -> Unit,
    private val imageLoader: ImageLoader,
    private val handleUrlByApp: Boolean,
    private val currentOffer: Int,
    private val customState: Map<String, Int>,
) : Module() {
    init {
        this.bind<DataBinding, DataBindingImpl>()
        this.bind<ModelMapper, ExperienceModelMapperImpl>()

        this.provideModuleScoped { DataBindingImpl() }
        this.provideModuleScoped { LayoutUiModelFactory() }
        this.provideModuleScoped { ExperienceModelMapperImpl(get(EXPERIENCE), get()) }
        this.provideModuleScoped(EXPERIENCE) { experience }
        this.provideModuleScoped(LOCATION) { location }
        this.provideModuleScoped<CoroutineDispatcher>(IO) { Dispatchers.IO }
        this.provideModuleScoped<CoroutineDispatcher>(MAIN) { Dispatchers.Main }
        this.provideModuleScoped {
            LayoutViewModel.RoktViewModelFactory(
                location = get(LOCATION),
                uxEvent = uxEvent,
                platformEvent = platformEvent,
                modelMapper = get(),
                ioDispatcher = get(IO),
                mainDispatcher = get(MAIN),
                handleUrlByApp = handleUrlByApp,
                currentOffer = currentOffer,
                customState = customState,
            )
        }
        this.provideModuleScoped {
            imageLoader
        }
    }

    companion object {
        const val EXPERIENCE = "EXPERIENCE"
        const val LOCATION = "Location"
        const val IO = "IO"
        const val MAIN = "MAIN"
    }
}
