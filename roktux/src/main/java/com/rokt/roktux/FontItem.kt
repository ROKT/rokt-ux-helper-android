package com.rokt.roktux

/**
 * Represents a font item with a specific weight and style.
 *
 * @property weight The weight of the font item.
 * @property style The style of the font item.
 */
sealed class FontItem(
    val weight: FontItemWeight,
    val style: FontItemStyle,
)

/**
 * Represents a font item that is loaded from a resource.
 *
 * @property resId The resource ID of the font.
 * @param weight The weight of the font item. Defaults to Normal.
 * @param style The style of the font item. Defaults to Normal.
 */
class ResourceFontItem(
    val resId: Int,
    weight: FontItemWeight = FontItemWeight.Normal,
    style: FontItemStyle = FontItemStyle.Normal,
) : FontItem(weight = weight, style = style)

/**
 * Represents a font item that is loaded from an asset.
 *
 * @property path The path to the font asset.
 * @param weight The weight of the font item. Defaults to Normal.
 * @param style The style of the font item. Defaults to Normal.
 */
class AssetFontItem(
    val path: String,
    weight: FontItemWeight = FontItemWeight.Normal,
    style: FontItemStyle = FontItemStyle.Normal,
) : FontItem(weight = weight, style = style)

/**
 * Represents the style of a font item.
 *
 * @property value The internal value representing the style.
 */
class FontItemStyle private constructor(internal val value: Int) {
    companion object {
        @JvmField
        val Normal = FontItemStyle(0)

        @JvmField
        val Italic = FontItemStyle(1)
    }
}

/**
 * Represents the weight of a font item.
 *
 * @property weight The internal value representing the weight.
 */
class FontItemWeight private constructor(internal val weight: Int) {

    companion object {
        /** Thin weight (100) */
        @JvmField
        val W100 = FontItemWeight(100)

        /** ExtraLight weight (200) */
        @JvmField
        val W200 = FontItemWeight(200)

        /** Light weight (300) */
        @JvmField
        val W300 = FontItemWeight(300)

        /** Normal weight (400) */
        @JvmField
        val W400 = FontItemWeight(400)

        /** Medium weight (500) */
        @JvmField
        val W500 = FontItemWeight(500)

        /** SemiBold weight (600) */
        @JvmField
        val W600 = FontItemWeight(600)

        /** Bold weight (700) */
        @JvmField
        val W700 = FontItemWeight(700)

        /** ExtraBold weight (800) */
        @JvmField
        val W800 = FontItemWeight(800)

        /** Black weight (900) */
        @JvmField
        val W900 = FontItemWeight(900)

        /** Alias for Thin weight */
        @JvmField
        val Thin = W100

        /** Alias for ExtraLight weight */
        @JvmField
        val ExtraLight = W200

        /** Alias for Light weight */
        @JvmField
        val Light = W300

        /** The default font weight - alias for Normal weight */
        @JvmField
        val Normal = W400

        /** Alias for Medium weight */
        @JvmField
        val Medium = W500

        /** Alias for SemiBold weight */
        @JvmField
        val SemiBold = W600

        /** A commonly used font weight that is heavier than normal - alias for Bold weight */
        @JvmField
        val Bold = W700

        /** Alias for ExtraBold weight */
        @JvmField
        val ExtraBold = W800

        /** Alias for Black weight */
        @JvmField
        val Black = W900
    }
}
