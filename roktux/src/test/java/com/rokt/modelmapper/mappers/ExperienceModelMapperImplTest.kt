package com.rokt.modelmapper.mappers

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.TestJsonLoader
import com.rokt.modelmapper.data.DataBindingImpl
import com.rokt.modelmapper.model.txn.SelectResponse
import com.rokt.roktux.utils.getBreakpointIndex
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExperienceModelMapperImplTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `transformResponse should return same model for string and parsed responses`() {
        // Arrange
        val experienceResponse = TestJsonLoader.loadJsonFromAssetsDirectory("Snapshot", "EmbeddedCompact.json")
        val parsedExperienceResponse = json.decodeFromString<SelectResponse>(experienceResponse)

        // Act
        val stringResult = ExperienceModelMapperImpl(experienceResponse, DataBindingImpl()).transformResponse()
        val parsedResult = ExperienceModelMapperImpl(null, parsedExperienceResponse, DataBindingImpl()).transformResponse()

        // Assert
        val stringModel = stringResult.getOrThrow()
        val parsedModel = parsedResult.getOrThrow()
        assertTrue(stringModel.sessionId == parsedModel.sessionId)
        assertTrue(stringModel.token == parsedModel.token)
        assertTrue(stringModel.pageId == parsedModel.pageId)
        assertTrue(stringModel.plugins.size == parsedModel.plugins.size)
        assertTrue(stringModel.plugins.first().id == parsedModel.plugins.first().id)
        assertTrue(stringModel.plugins.first().slots.size == parsedModel.plugins.first().slots.size)
    }

    @Test
    fun `transformResponse should not decode string when parsed response is provided`() {
        // Arrange
        val experienceResponse = TestJsonLoader.loadJsonFromAssetsDirectory("Snapshot", "EmbeddedCompact.json")
        val parsedExperienceResponse = json.decodeFromString<SelectResponse>(experienceResponse)

        // Act
        val result = ExperienceModelMapperImpl("{", parsedExperienceResponse, DataBindingImpl()).transformResponse()

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `buildBreakpoints does not let a payload-supplied 'default' entry replace the 0 sentinel`() {
        // Arrange: the response's breakpoints map declares its own "default" entry alongside
        // other breakpoints that are all larger than any plausible window width.
        val experienceResponse = TestJsonLoader.loadJsonFromAssetsDirectory(
            "ExperienceModelMapper",
            "Breakpoints_with_default_override.json",
        )

        // Act
        val result = ExperienceModelMapperImpl(experienceResponse, DataBindingImpl()).transformResponse()
        val breakpoints = result.getOrThrow().plugins.first().breakpoint

        // Assert: the sentinel used to select the smallest layout must remain 0 regardless of
        // what the payload contains, and resolving an index for a narrow window must never go
        // negative (which would otherwise crash any component indexing into a breakpoint list).
        assertEquals(0, breakpoints["default"])
        val resolvedIndex = getBreakpointIndex(width = 200, breakpoints = breakpoints)
        assertTrue("resolved breakpoint index must never be negative, was $resolvedIndex", resolvedIndex >= 0)
    }
}
