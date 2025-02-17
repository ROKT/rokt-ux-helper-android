package com.rokt.roktux

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.rokt.roktux.event.RoktPlatformEventsWrapper
import com.rokt.roktux.event.RoktUxEvent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentList

/**
 * View that integrates with Rokt UX.
 *
 * @constructor Creates a RoktLayoutView.
 * @param context The context of the view.
 * @param attrs The attribute set.
 * @param defStyle The default style
 * @param location The location of view.
 */
class RoktLayoutView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    location: String? = null,
) : AbstractComposeView(context, attrs, defStyle) {

    // Mutable state to hold the experience response.
    private var experienceResponse by mutableStateOf("")

    // Event handlers for UX and platform events.
    private var uxEvent: ((event: RoktUxEvent) -> Unit) = {}
    private var platformEvents: ((platformEvents: RoktPlatformEventsWrapper) -> Unit) = {}

    // Configuration for Rokt UX.
    private var roktUxConfig: RoktUxConfig? = null

    // Location attribute.
    private var location: String? = null

    init {
        // Obtain styled attributes and set the location.
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoktLayoutView)
        this.location = location // Use constructor parameter if available
            ?: typedArray.getString(R.styleable.RoktLayoutView_location)
        typedArray.recycle()
    }

    /**
     * Composable function to render the content.
     */
    @Composable
    override fun Content() {
        if (experienceResponse.isNotEmpty()) {
            val roktConfig: RoktUxConfig = remember(roktUxConfig) {
                val composeFontMap =
                    roktUxConfig?.xmlFontFamilyMap?.mapValues { getFontFamily(it.value.toPersistentList()) }
                        ?.toImmutableMap() ?: persistentMapOf()
                RoktUxConfig.Builder().apply {
                    composeFontMap(composeFontMap)
                    roktUxConfig?.imageHandlingStrategy?.let { imageHandlingStrategy(it) }
                    roktUxConfig?.colorMode?.let { colorMode(it) }
                    roktUxConfig?.viewStateConfig?.let { viewStateConfig(it) }
                    roktUxConfig?.edgeToEdgeDisplay?.let { edgeToEdgeDisplay(it) }
                }.build()
            }
            RoktLayout(
                experienceResponse = experienceResponse,
                location = location.orEmpty(),
                roktUxConfig = roktConfig,
                onUxEvent = uxEvent,
                onPlatformEvent = platformEvents,
            )
        }
    }

    /**
     * Loads the layout with the given parameters.
     *
     * @param experienceResponse The experience response.
     * @param roktUxConfig The Rokt UX configuration.
     * @param onUxEvent The UX event handler.
     * @param onPlatformEvent The platform event handler.
     */
    fun loadLayout(
        experienceResponse: String,
        roktUxConfig: RoktUxConfig,
        onUxEvent: ((event: RoktUxEvent) -> Unit),
        onPlatformEvent: ((platformEvents: RoktPlatformEventsWrapper) -> Unit),
    ) {
        this.uxEvent = onUxEvent
        this.platformEvents = onPlatformEvent
        this.experienceResponse = experienceResponse
        this.roktUxConfig = roktUxConfig
    }

    /**
     * Retrieves a FontFamily from a list of FontItems.
     *
     * @param fontItems The list of FontItems.
     * @return The FontFamily.
     */
    private fun getFontFamily(fontItems: ImmutableList<FontItem>): FontFamily = fontItems.mapNotNull { fontItem ->
        try {
            when (fontItem) {
                is AssetFontItem -> Font(
                    path = fontItem.path,
                    assetManager = context.assets,
                    weight = FontWeight(fontItem.weight.weight),
                    style = fontItem.style.toFontStyle(),
                )

                is ResourceFontItem -> Font(
                    resId = fontItem.resId,
                    weight = FontWeight(fontItem.weight.weight),
                    style = fontItem.style.toFontStyle(),
                )
            }
        } catch (e: Exception) {
            null
        }
    }.takeIf { it.isNotEmpty() }?.let {
        return FontFamily(it)
    } ?: FontFamily.Default
}

private fun FontItemStyle.toFontStyle(): FontStyle = when (this) {
    FontItemStyle.Normal -> FontStyle.Normal
    FontItemStyle.Italic -> FontStyle.Italic
    else -> FontStyle.Normal
}
