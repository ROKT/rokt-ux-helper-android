package com.rokt.modelmapper.mappers

import androidx.compose.ui.layout.ContentScale
import com.rokt.network.model.DimensionHeightFitValue
import com.rokt.network.model.DimensionHeightValue
import com.rokt.network.model.DimensionWidthFitValue
import com.rokt.network.model.DimensionWidthValue
import com.rokt.network.model.ImageScale
import org.junit.Assert.assertEquals
import org.junit.Test

class ImageScaleMapperTest {

    @Test
    fun `resolveLayoutImageContentScale uses schema fill as crop`() {
        assertEquals(ContentScale.Crop, resolveLayoutImageContentScale(ImageScale.Fill, null, null))
    }

    @Test
    fun `resolveLayoutImageContentScale uses schema fit`() {
        assertEquals(ContentScale.Fit, resolveLayoutImageContentScale(ImageScale.Fit, null, null))
    }

    @Test
    fun `resolveLayoutImageContentScale uses schema crop as none`() {
        assertEquals(ContentScale.None, resolveLayoutImageContentScale(ImageScale.Crop, null, null))
    }

    @Test
    fun `resolveLayoutImageContentScale falls back to dimension heuristic when scale null`() {
        assertEquals(
            ContentScale.Crop,
            resolveLayoutImageContentScale(
                schemaScale = null,
                width = DimensionWidthValue.Fit(DimensionWidthFitValue.WrapContent),
                height = DimensionHeightValue.Fit(DimensionHeightFitValue.WrapContent),
            ),
        )
        assertEquals(
            ContentScale.Fit,
            resolveLayoutImageContentScale(
                schemaScale = null,
                width = DimensionWidthValue.Fit(DimensionWidthFitValue.WrapContent),
                height = DimensionHeightValue.Fixed(100f),
            ),
        )
    }
}
