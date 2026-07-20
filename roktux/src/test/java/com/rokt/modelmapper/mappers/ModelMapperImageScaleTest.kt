package com.rokt.modelmapper.mappers

import androidx.compose.ui.layout.ContentScale
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.Module
import com.rokt.network.model.LayoutSchemaModel
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Ensures [transformStaticImage] / [transformDataImage] wire [image.scale] through to
 * [LayoutSchemaUiModel.ImageUiModel.scaleType] (same rules as [resolveLayoutImageContentScale]).
 */
class ModelMapperImageScaleTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `transformStaticImage maps schema image scale fit fill crop to scaleType`() {
        assertEquals(ContentScale.Fit, transformStaticImage(decodeStatic("fit")).scaleType)
        assertEquals(ContentScale.Crop, transformStaticImage(decodeStatic("fill")).scaleType)
        assertEquals(ContentScale.None, transformStaticImage(decodeStatic("crop")).scaleType)
    }

    @Test
    fun `transformDataImage maps schema image scale fit fill crop to scaleType`() {
        assertEquals(ContentScale.Fit, transformDataImage(decodeData("fit"), null, Module.AddToCart, 0).scaleType)
        assertEquals(ContentScale.Crop, transformDataImage(decodeData("fill"), null, Module.AddToCart, 0).scaleType)
        assertEquals(ContentScale.None, transformDataImage(decodeData("crop"), null, Module.AddToCart, 0).scaleType)
    }

    @Test
    fun `transformStaticImage uses dimension heuristic when image scale omitted`() {
        val model = json.decodeFromString<LayoutSchemaModel>(
            """{"type":"StaticImage","node":{"url":{"light":"https://example.com/x.png","dark":""},"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fit","value":"wrap-content"},"height":{"type":"fit","value":"wrap-content"}}}}]}}}}""",
        )
        check(model is LayoutSchemaModel.StaticImage)
        assertEquals(ContentScale.Crop, transformStaticImage(model).scaleType)
    }

    @Test
    fun `transformStaticImage uses fit heuristic when image scale omitted and height not wrap`() {
        val model = json.decodeFromString<LayoutSchemaModel>(
            """{"type":"StaticImage","node":{"url":{"light":"https://example.com/x.png","dark":""},"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fit","value":"wrap-content"},"height":{"type":"fixed","value":100}}}}]}}}}""",
        )
        check(model is LayoutSchemaModel.StaticImage)
        assertEquals(ContentScale.Fit, transformStaticImage(model).scaleType)
    }

    private fun decodeStatic(scale: String): LayoutSchemaModel.StaticImage {
        val model = json.decodeFromString<LayoutSchemaModel>(staticImageJson(scale))
        check(model is LayoutSchemaModel.StaticImage)
        return model
    }

    private fun decodeData(scale: String): LayoutSchemaModel.DataImage {
        val model = json.decodeFromString<LayoutSchemaModel>(dataImageJson(scale))
        check(model is LayoutSchemaModel.DataImage)
        return model
    }

    /** Shape matches snapshot fixtures; URLs shortened for tests. */
    private fun staticImageJson(scale: String) = """{"type":"StaticImage","node":{"url":{"light":"https://example.com/x.png","dark":""},"styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":120},"height":{"type":"fixed","value":80}},"image":{"scale":"$scale"}}}]}}}}"""

    private fun dataImageJson(scale: String) = """{"type":"DataImage","node":{"imageKey":"creativeImage","styles":{"elements":{"own":[{"default":{"dimension":{"width":{"type":"fixed","value":120},"height":{"type":"fixed","value":80}},"image":{"scale":"$scale"}}}]}}}}"""
}
