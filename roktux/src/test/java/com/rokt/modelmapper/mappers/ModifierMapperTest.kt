package com.rokt.modelmapper.mappers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.rokt.modelmapper.uimodel.ContainerProperties
import com.rokt.modelmapper.uimodel.StateBlock
import com.rokt.network.model.BasicStateStylingBlock
import com.rokt.network.model.ContainerStylingProperties
import com.rokt.network.model.FlexChildStylingProperties
import com.rokt.network.model.Shadow
import com.rokt.network.model.SpacingStylingProperties
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

    @Test
    fun `oversized shadow blur radius is capped at 1000dp`() {
        listOf(Float.MAX_VALUE, 3.4e38f).forEach { blurRadius ->
            assertThat(shadowBlurOf(blurRadius)).isEqualTo(1000.dp)
        }
    }

    @Test
    fun `non-finite shadow blur radius becomes null`() {
        listOf(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN).forEach { blurRadius ->
            assertThat(shadowBlurOf(blurRadius)).isNull()
        }
    }

    @Test
    fun `oversized container blur is capped at 100`() {
        listOf(3.4e38f, Float.MAX_VALUE).forEach { blur ->
            assertThat(blurOf(blur)).isEqualTo(100f)
        }
    }

    @Test
    fun `container blur keeps zero and in-range values`() {
        // An explicit 0 must stay 0 so a larger breakpoint can switch a smaller breakpoint's blur off.
        assertThat(blurOf(0f)).isEqualTo(0f)
        assertThat(blurOf(12f)).isEqualTo(12f)
        assertThat(blurOf(null)).isNull()
    }

    @Test
    fun `negative container blur becomes zero`() {
        assertThat(blurOf(-1f)).isEqualTo(0f)
    }

    @Test
    fun `non-finite container blur becomes null`() {
        listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY).forEach { blur ->
            assertThat(blurOf(blur)).isNull()
        }
    }

    @Test
    fun `oversized padding and margin are capped at 1000dp`() {
        listOf("131072", "2147483647").forEach { value ->
            assertThat(sides(paddingOf(value))).containsExactly(1000.dp, 1000.dp, 1000.dp, 1000.dp)
            assertThat(sides(marginOf(value))).containsExactly(1000.dp, 1000.dp, 1000.dp, 1000.dp)
        }
    }

    @Test
    fun `oversized two value padding and margin are capped per axis`() {
        // sides order: top, right, bottom, left
        assertThat(sides(paddingOf("8388608 0"))).containsExactly(1000.dp, 0.dp, 1000.dp, 0.dp)
        assertThat(sides(marginOf("8388608 0"))).containsExactly(1000.dp, 0.dp, 1000.dp, 0.dp)
    }

    @Test
    fun `negative padding and margin become zero`() {
        assertThat(sides(paddingOf("-5"))).containsExactly(0.dp, 0.dp, 0.dp, 0.dp)
        assertThat(sides(marginOf("-5"))).containsExactly(0.dp, 0.dp, 0.dp, 0.dp)
    }

    @Test
    fun `in-range padding and margin are preserved`() {
        assertThat(sides(paddingOf("10 20 15 25"))).containsExactly(10.dp, 20.dp, 15.dp, 25.dp)
        assertThat(sides(marginOf("1000"))).containsExactly(1000.dp, 1000.dp, 1000.dp, 1000.dp)
    }

    @Test
    fun `negative offset is preserved`() {
        val result = transformModifier(
            spacingProperties = SpacingStylingProperties(offset = "-5 -10"),
            dimensionProperties = null,
            backgroundProperties = null,
        )
        assertThat(result.offset?.x).isEqualTo((-5).dp)
        assertThat(result.offset?.y).isEqualTo((-10).dp)
    }

    @Test
    fun `negative and oversized gaps are bounded to 0 to 1000`() {
        assertThat(gapOf(-1f)).isEqualTo(0f)
        assertThat(gapOf(-1e6f)).isEqualTo(0f)
        assertThat(gapOf(1e8f)).isEqualTo(1000f)
        assertThat(gapOf(Float.MAX_VALUE)).isEqualTo(1000f)
    }

    @Test
    fun `non-finite gaps become null`() {
        assertThat(gapOf(Float.NaN)).isNull()
        assertThat(gapOf(Float.POSITIVE_INFINITY)).isNull()
        assertThat(gapOf(Float.NEGATIVE_INFINITY)).isNull()
    }

    @Test
    fun `in-range gaps are preserved`() {
        listOf(0f, 8f, 1000f).forEach { gap ->
            assertThat(gapOf(gap)).isEqualTo(gap)
        }
        assertThat(gapOf(null)).isNull()
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

    private fun shadowBlurOf(blur: Float): Dp? = transformModifier(
        spacingProperties = null,
        dimensionProperties = null,
        backgroundProperties = null,
        borderProperties = null,
        containerProperties = ContainerStylingProperties(shadow = shadow(blur)),
    ).shadowBlurRadius

    private fun blurOf(blur: Float?): Float? = transformModifier(
        spacingProperties = null,
        dimensionProperties = null,
        backgroundProperties = null,
        borderProperties = null,
        containerProperties = ContainerStylingProperties(blur = blur),
    ).blurRadius

    private fun paddingOf(value: String): PaddingValues? = transformModifier(
        spacingProperties = SpacingStylingProperties(padding = value),
        dimensionProperties = null,
        backgroundProperties = null,
    ).padding

    private fun marginOf(value: String): PaddingValues? = transformModifier(
        spacingProperties = SpacingStylingProperties(margin = value),
        dimensionProperties = null,
        backgroundProperties = null,
    ).margin

    // Order: top, right, bottom, left.
    private fun sides(padding: PaddingValues?): List<Dp> = padding!!.let {
        listOf(
            it.calculateTopPadding(),
            it.calculateRightPadding(LayoutDirection.Ltr),
            it.calculateBottomPadding(),
            it.calculateLeftPadding(LayoutDirection.Ltr),
        )
    }

    // Checks the default and pressed blocks, which share the mapper.
    private fun gapOf(gap: Float?): Float? {
        val block = BasicStateStylingBlock<ContainerStylingProperties?>(
            default = ContainerStylingProperties(gap = gap),
            pressed = ContainerStylingProperties(gap = gap),
        )
        val result = persistentListOf(block).transformContainer(transformContainer = { it })
        assertThat(result?.first()?.pressed?.gap).isEqualTo(result?.first()?.default?.gap)
        return result?.first()?.default?.gap
    }

    private fun shadow(blurRadius: Float) = Shadow(0f, 0f, blurRadius, 0f, ThemeColor(light = "#000000"))
}
