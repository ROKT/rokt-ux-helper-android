package com.rokt.modelmapper.mappers

import androidx.compose.ui.unit.dp
import com.rokt.network.model.ContainerStylingProperties
import com.rokt.network.model.Shadow
import com.rokt.network.model.ThemeColor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ModifierMapperTest {

    @Test
    fun `transformModifier coerces a negative shadow blurRadius to zero`() {
        val containerProperties = ContainerStylingProperties(
            shadow = shadowWithBlurRadius(-1f),
        )

        val result = transformModifier(
            spacingProperties = null,
            dimensionProperties = null,
            backgroundProperties = null,
            containerProperties = containerProperties,
        )

        // A negative payload value must be clamped here, since downstream drawing code only
        // treats an exact 0.dp as "no blur".
        assertThat(result.shadowBlurRadius).isEqualTo(0.dp)
    }

    @Test
    fun `transformModifier keeps a zero shadow blurRadius as no-blur`() {
        val containerProperties = ContainerStylingProperties(
            shadow = shadowWithBlurRadius(0f),
        )

        val result = transformModifier(
            spacingProperties = null,
            dimensionProperties = null,
            backgroundProperties = null,
            containerProperties = containerProperties,
        )

        assertThat(result.shadowBlurRadius).isEqualTo(0.dp)
    }

    @Test
    fun `transformModifier preserves a positive shadow blurRadius`() {
        val containerProperties = ContainerStylingProperties(
            shadow = shadowWithBlurRadius(12f),
        )

        val result = transformModifier(
            spacingProperties = null,
            dimensionProperties = null,
            backgroundProperties = null,
            containerProperties = containerProperties,
        )

        assertThat(result.shadowBlurRadius).isEqualTo(12.dp)
    }

    private fun shadowWithBlurRadius(blurRadius: Float): Shadow = Shadow(
        offsetX = 0f,
        offsetY = 0f,
        blurRadius = blurRadius,
        spreadRadius = 0f,
        color = ThemeColor(light = "#000000"),
    )
}
