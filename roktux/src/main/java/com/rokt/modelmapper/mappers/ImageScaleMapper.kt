package com.rokt.modelmapper.mappers

import androidx.compose.ui.layout.ContentScale
import com.rokt.network.model.DimensionHeightValue
import com.rokt.network.model.DimensionWidthValue
import com.rokt.network.model.ImageScale

/**
 * Maps DCUI [ImageScale] to Compose [ContentScale], aligned with schema semantics:
 * - [ImageScale.Fit]: letterbox inside bounds
 * - [ImageScale.Fill]: aspect-fill / cover bounds
 * - [ImageScale.Crop]: intrinsic size; overflow clipped by bounds ([ContentScale.None])
 */
internal fun ImageScale.toContentScale(): ContentScale = when (this) {
    ImageScale.Crop -> ContentScale.None
    ImageScale.Fit -> ContentScale.Fit
    ImageScale.Fill -> ContentScale.Crop
}

/**
 * Uses explicit schema [ImageScale] when present; otherwise preserves the prior
 * dimension-only heuristic (both dimensions `Fit` → crop-like behaviour).
 */
internal fun resolveLayoutImageContentScale(
    schemaScale: ImageScale?,
    width: DimensionWidthValue?,
    height: DimensionHeightValue?,
): ContentScale {
    schemaScale?.let { return it.toContentScale() }
    return when {
        width is DimensionWidthValue.Fit && height is DimensionHeightValue.Fit -> ContentScale.Crop
        else -> ContentScale.Fit
    }
}
