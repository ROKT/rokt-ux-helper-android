package com.rokt.modelmapper.mappers

import com.rokt.modelmapper.uimodel.ContainerProperties
import com.rokt.modelmapper.uimodel.StateBlock
import com.rokt.network.model.BasicStateStylingBlock
import com.rokt.network.model.FlexChildStylingProperties
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ModifierMapperTest {

    @Test
    fun `transformContainer maps non-positive or non-finite flexChild weight to null in both default and pressed blocks`() {
        val zeroBlock = flexChildBlock(defaultWeight = 0f, pressedWeight = 0f)
        val negativeBlock = flexChildBlock(defaultWeight = -1f, pressedWeight = -1f)
        val nanBlock = flexChildBlock(defaultWeight = Float.NaN, pressedWeight = Float.NaN)
        val infiniteBlock = flexChildBlock(
            defaultWeight = Float.POSITIVE_INFINITY,
            pressedWeight = Float.NEGATIVE_INFINITY,
        )

        listOf(zeroBlock, negativeBlock, nanBlock, infiniteBlock).forEach { block ->
            val result = transformContainerForWeight(block)

            assertThat(result?.first()?.default?.weight)
                .describedAs("default weight for $block")
                .isNull()
            assertThat(result?.first()?.pressed?.weight)
                .describedAs("pressed weight for $block")
                .isNull()
        }
    }

    @Test
    fun `transformContainer keeps a normal positive flexChild weight unchanged in both default and pressed blocks`() {
        val block = flexChildBlock(defaultWeight = 1.5f, pressedWeight = 2f)

        val result = transformContainerForWeight(block)

        assertThat(result?.first()?.default?.weight).isEqualTo(1.5f)
        assertThat(result?.first()?.pressed?.weight).isEqualTo(2f)
    }

    private fun transformContainerForWeight(
        block: BasicStateStylingBlock<FlexChildStylingProperties?>,
    ): ImmutableList<StateBlock<ContainerProperties>>? = persistentListOf(block).transformContainer(
        transformFlexChild = { it },
    )

    private fun flexChildBlock(
        defaultWeight: Float?,
        pressedWeight: Float?,
    ): BasicStateStylingBlock<FlexChildStylingProperties?> = BasicStateStylingBlock(
        default = FlexChildStylingProperties(weight = defaultWeight, order = null, alignSelf = null),
        pressed = FlexChildStylingProperties(weight = pressedWeight, order = null, alignSelf = null),
    )
}
