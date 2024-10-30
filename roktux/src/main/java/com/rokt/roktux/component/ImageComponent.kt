package com.rokt.roktux.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.roktux.di.layout.LocalLayoutComponent
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState

internal class ImageComponent(
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.ImageUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.ImageUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        var onImageError by remember { mutableStateOf(false) }
        if (!onImageError) {
            val url = if (isDarkModeEnabled) model.darkUrl ?: model.lightUrl else model.lightUrl
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true)
                    .build(),
                contentDescription = model.alt,
                imageLoader = LocalLayoutComponent.current[ImageLoader::class.java],
                modifier = modifier.then(
                    modifierFactory.createModifier(
                        modifierPropertiesList = model.ownModifiers,
                        conditionalTransitionModifier = model.conditionalTransitionModifiers,
                        breakpointIndex = breakpointIndex,
                        isPressed = isPressed,
                        isDarkModeEnabled = isDarkModeEnabled,
                        offerState = offerState,
                    ),
                ),
                onError = { onImageError = true },
            )
        }
    }
}