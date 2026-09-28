package com.rokt.modelmapper.mappers

import androidx.compose.ui.unit.dp
import com.rokt.modelmapper.uimodel.ContainerProperties
import com.rokt.modelmapper.uimodel.StateBlock
import com.rokt.network.model.BasicStateStylingBlock
import com.rokt.network.model.ContainerStylingProperties
import com.rokt.network.model.FlexChildStylingProperties
import com.rokt.network.model.Shadow
import com.rokt.network.model.ThemeColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ModifierMapperTest {
    @Test
    fun `negative shadow blur radius becomes zero`() {
        val result = transformModifier(
            spacingProperties = null,
            dimensionProperties = null,
            backgroundProperties = null,
            borderProperties = null,
            containerProperties = ContainerStylingProperties(shadow = shadow(-1f)),
        )
        assertThat(result.shadowBlurRadius).isEqualTo(0.dp)
    }

    @Test
    fun `zero shadow blur radius remains zero`() {
        val result = transformModifier(
            spacingProperties = null,
            dimensionProperties = null,
            backgroundProperties = null,
            borderProperties = null,
            containerProperties = ContainerStylingProperties(shadow = shadow(0f)),
        )
        assertThat(result.shadowBlurRadius).isEqualTo(0.dp)
    }

    @Test
    fun `positive shadow blur radius is preserved`() {
        val result = transformModifier(
            spacingProperties = null,
            dimensionProperties = null,
            backgroundProperties = null,
            borderProperties = null,
            containerProperties = ContainerStylingProperties(shadow = shadow(12f)),
        )
        assertThat(result.shadowBlurRadius).isEqualTo(12.dp)
    }

    @Test
    fun `invalid flex child weights become null`() {
        listOf(0f, -1f, Float.NaN, Float.POSITIVE_INFINITY).forEach { weight ->
            val result = transformContainerForWeight(block(weight, weight))
            assertThat(result?.first()?.default?.weight).isNull()
            assertThat(result?.first()?.pressed?.weight).isNull()
        }
    }

    @Test
    fun `positive flex child weights are preserved`() {
        val result = transformContainerForWeight(block(1.5f, 2f))
        assertThat(result?.first()?.default?.weight).isEqualTo(1.5f)
        assertThat(result?.first()?.pressed?.weight).isEqualTo(2f)
    }
    private fun transformContainerForWeight(
        block: BasicStateStylingBlock<FlexChildStylingProperties?>,
    ): ImmutableList<StateBlock<ContainerProperties>>? = persistentListOf(block).transformContainer(
        transformFlexChild = { it },
    )

    private fun block(
        defaultWeight: Float?,
        pressedWeight: Float?,
    ): BasicStateStylingBlock<FlexChildStylingProperties?> = BasicStateStylingBlock<FlexChildStylingProperties?>(
        default = FlexChildStylingProperties(defaultWeight, null, null),
        pressed = FlexChildStylingProperties(pressedWeight, null, null),
    )

    private fun shadow(blurRadius: Float) = Shadow(0f, 0f, blurRadius, 0f, ThemeColor(light = "#000000"))
}
