package com.rokt.roktux.utils

import com.rokt.modelmapper.data.BindData
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.toImmutableMap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BindDataPlaceholderResolutionTest {
    @Test
    fun `catalogRuntime placeholders resolve from offer state`() {
        val value = BindData.Value("Total %^DATA.catalogRuntime.total | --^%").getValue(
            offerState = offerState(catalogRuntimeData = mapOf("total" to "$107.77")),
            viewableItems = 1,
        )

        assertEquals("Total $107.77", value)
    }

    @Test
    fun `catalogRuntime placeholders fall back to default literal when data is missing`() {
        val value = BindData.Value("%^DATA.catalogRuntime.tax | --^%").getValue(
            offerState = offerState(),
            viewableItems = 1,
        )

        assertEquals("--", value)
    }

    @Test
    fun `catalogRuntime placeholders resolve repeated tokens consistently`() {
        val value = BindData.Value(
            "%^DATA.catalogRuntime.x^%/%^DATA.catalogRuntime.x^%/%^DATA.catalogRuntime.x^%",
        ).getValue(
            offerState = offerState(catalogRuntimeData = mapOf("x" to "1")),
            viewableItems = 1,
        )

        assertEquals("1/1/1", value)
    }

    @Test
    fun `catalogRuntime resolution leaves non catalogRuntime placeholders untouched`() {
        val value = BindData.Value("Hi %^DATA.creativeCopy.name | friend^%").getValue(
            offerState = offerState(catalogRuntimeData = mapOf("name" to "ignored")),
            viewableItems = 1,
        )

        assertEquals("Hi %^DATA.creativeCopy.name | friend^%", value)
    }

    @Test
    fun `catalogRuntime resolution preserves token when no runtime value or fallback`() {
        val token = "%^DATA.catalogRuntime.x^%"
        val value = BindData.Value(token).getValue(
            offerState = offerState(),
            viewableItems = 1,
        )

        assertEquals(token, value)
    }

    @Test
    fun `getValue returns null for undefined bind data`() {
        assertNull(BindData.Undefined.getValue(offerState(), viewableItems = 1))
    }

    private fun offerState(
        catalogRuntimeData: Map<String, String> = emptyMap(),
    ): OfferUiState = OfferUiState(
        currentOfferIndex = 0,
        lastOfferIndex = 0,
        viewableItems = 1,
        creativeCopy = emptyMap<String, String>().toImmutableMap(),
        breakpoints = emptyMap<String, Int>().toImmutableMap(),
        customState = emptyMap<String, Int>().toImmutableMap(),
        catalogRuntimeData = catalogRuntimeData.toImmutableMap(),
    )
}
