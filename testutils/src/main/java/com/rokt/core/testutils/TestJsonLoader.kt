package com.rokt.core.testutils

import androidx.test.platform.app.InstrumentationRegistry

/**
 * Utility class for loading JSON files from test assets.
 */
object TestJsonLoader {
    /**
     * Loads a JSON file from the test assets directory and returns its content as a string.
     *
     * @param fileName The name of the JSON file in the test assets directory (e.g., "example.json")
     * @return The content of the JSON file as a string
     * @throws IllegalArgumentException if the file cannot be found
     */
    fun loadJsonFromAssets(fileName: String): String = try {
        InstrumentationRegistry.getInstrumentation().targetContext.assets.open(fileName).use {
            it.reader().readText()
        }
    } catch (e: Exception) {
        throw IllegalArgumentException("Could not find JSON file: $fileName", e)
    }

    /**
     * Loads a JSON file from a specific test assets directory and returns its content as a string.
     *
     * @param directoryPath The path to the directory containing the JSON file (e.g., "json/responses")
     * @param fileName The name of the JSON file (e.g., "example.json")
     * @return The content of the JSON file as a string
     * @throws IllegalArgumentException if the file cannot be found
     */
    fun loadJsonFromAssetsDirectory(directoryPath: String, fileName: String): String {
        val path = if (directoryPath.endsWith("/")) {
            "$directoryPath$fileName"
        } else {
            "$directoryPath/$fileName"
        }
        return loadJsonFromAssets(path)
    }
}
