package com.rokt.roktux

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontFamily
import com.rokt.roktux.imagehandler.ImageHandlingStrategy
import com.rokt.roktux.imagehandler.NetworkStrategy

/**
 * Immutable data class representing the configuration for Rokt UX.
 *
 * @property xmlFontFamilyMap Map of font families to be used in the Android XML view.
 * @property composeFontMap Map of font families to be used in the Jetpack compose view.
 * @property imageHandlingStrategy Strategy for handling images in the UX.
 * @property colorMode The color mode configuration to use in the UX.
 */
@Immutable
class RoktUxConfig private constructor(
    val xmlFontFamilyMap: Map<String, List<FontItem>>? = null,
    val composeFontMap: Map<String, FontFamily>? = null,
    val imageHandlingStrategy: ImageHandlingStrategy,
    val colorMode: ColorMode,
    val viewStateConfig: ViewStateConfig? = null,
    val handleUrlByApp: Boolean = true,
) {
    /**
     * Builder class for RoktUxConfig.
     *
     * @property xmlFontFamilyMap Map of font families to be used in the Android XML view.
     * @property composeFontMap Map of font families to be used in the Jetpack compose view.
     * @property imageHandlingStrategy Strategy for handling images in the UX.
     * @property colorMode The color mode configuration to use in the UX.
     * @property handleUrlByApp Flag to determine if the URL should be handled by the app.
     * @property viewStateConfig The cache configuration to use in the UX.
     */
    data class Builder internal constructor(
        private var xmlFontFamilyMap: Map<String, List<FontItem>>? = null,
        private var composeFontMap: Map<String, FontFamily>? = null,
        private var imageHandlingStrategy: ImageHandlingStrategy = NetworkStrategy(),
        private var colorMode: ColorMode = ColorMode.SYSTEM,
        private var handleUrlByApp: Boolean = true,
        private var viewStateConfig: ViewStateConfig? = null,
    ) {
        /**
         * Sets the XML font family map.
         *
         * @param xmlFontFamilyMap Map of font families to be used in the Android XML view.
         * @return The Builder instance.
         */
        fun xmlFontFamilyMap(xmlFontFamilyMap: Map<String, List<FontItem>>) = apply {
            this.xmlFontFamilyMap = xmlFontFamilyMap
        }

        /**
         * Sets the compose font map.
         *
         * @param composeFontMap Map of font families to be used in the Jetpack compose view.
         * @return The Builder instance.
         */
        fun composeFontMap(composeFontMap: Map<String, FontFamily>?) = apply { this.composeFontMap = composeFontMap }

        /**
         * Sets the image handling strategy.
         *
         * @param imageHandlingStrategy Strategy for handling images in the UX.
         * @return The Builder instance.
         */
        fun imageHandlingStrategy(imageHandlingStrategy: ImageHandlingStrategy) = apply {
            this.imageHandlingStrategy = imageHandlingStrategy
        }

        /**
         * Sets the color mode.
         *
         * @param colorMode The color mode configuration to use in the UX.
         * @return The Builder instance.
         */
        fun colorMode(colorMode: ColorMode) = apply { this.colorMode = colorMode }

        /**
         * Sets the cache configuration.
         *
         * @param viewStateConfig The cache configuration to use in the UX.
         */
        fun viewStateConfig(viewStateConfig: ViewStateConfig) = apply { this.viewStateConfig = viewStateConfig }

        /**
         * Builds the RoktUxConfig instance.
         *
         * @return The RoktUxConfig instance.
         */
        fun build(): RoktUxConfig = RoktUxConfig(
            xmlFontFamilyMap = xmlFontFamilyMap,
            composeFontMap = composeFontMap,
            imageHandlingStrategy = imageHandlingStrategy,
            colorMode = colorMode,
            handleUrlByApp = handleUrlByApp,
            viewStateConfig = viewStateConfig,
        )
    }

    companion object {
        fun builder(): Builder = Builder()
    }
}

/**
 * Enum representing the color modes.
 */
@Immutable
public enum class ColorMode { LIGHT, DARK, SYSTEM }
