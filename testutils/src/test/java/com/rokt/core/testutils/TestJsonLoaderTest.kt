package com.rokt.core.testutils

import android.content.res.AssetManager
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream

@RunWith(RobolectricTestRunner::class)
class TestJsonLoaderTest {

    private val mockAssetManager = mockk<AssetManager>()

    @Before
    fun setup() {
        mockkStatic(InstrumentationRegistry::class)
        every {
            InstrumentationRegistry.getInstrumentation().targetContext.assets
        } returns mockAssetManager
    }

    @Test
    fun `loadJsonFromAssets loads JSON file successfully`() {
        // Given
        val jsonContent = """{"message": "test message"}"""
        every {
            mockAssetManager.open("test.json")
        } returns ByteArrayInputStream(jsonContent.toByteArray())

        // When
        val result = TestJsonLoader.loadJsonFromAssets("test.json")

        // Then
        assertThat(result).isEqualTo(jsonContent)
    }

    @Test
    fun `loadJsonFromAssets throws exception for non-existent file`() {
        // Given
        every {
            mockAssetManager.open("non-existent.json")
        } throws Exception("File not found")

        // When/Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            TestJsonLoader.loadJsonFromAssets("non-existent.json")
        }
        assertThat(exception.message).contains("Could not find JSON file")
    }

    @Test
    fun `loadJsonFromAssetsDirectory loads JSON file successfully`() {
        // Given
        val jsonContent = """{"message": "test message"}"""
        every {
            mockAssetManager.open("test/dir/file.json")
        } returns ByteArrayInputStream(jsonContent.toByteArray())

        // When
        val result = TestJsonLoader.loadJsonFromAssetsDirectory("test/dir", "file.json")

        // Then
        assertThat(result).isEqualTo(jsonContent)
    }

    @Test
    fun `loadJsonFromAssetsDirectory handles directory path with trailing slash`() {
        // Given
        val jsonContent = """{"message": "test message"}"""
        every {
            mockAssetManager.open("test/dir/file.json")
        } returns ByteArrayInputStream(jsonContent.toByteArray())

        // When
        val result = TestJsonLoader.loadJsonFromAssetsDirectory("test/dir/", "file.json")

        // Then
        assertThat(result).isEqualTo(jsonContent)
    }

    @Test
    fun `loadJsonFromAssetsDirectory throws exception for non-existent file`() {
        // Given
        every {
            mockAssetManager.open("test/dir/non-existent.json")
        } throws Exception("File not found")

        // When/Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            TestJsonLoader.loadJsonFromAssetsDirectory("test/dir", "non-existent.json")
        }
        assertThat(exception.message).contains("Could not find JSON file")
    }
}
