package com.rokt.modelmapper.mappers

import com.rokt.modelmapper.uimodel.PeekThroughSizeUiModel
import com.rokt.network.model.CarouselDistributionModel
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.PeekThroughSize
import com.rokt.network.model.WhenPredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DistributionModelMapperTest {

    @Test
    fun `transformCarouselDistribution coerces a negative fixed peek through size to zero`() {
        val carouselDistributionModel = LayoutSchemaModel.CarouselDistribution(
            CarouselDistributionModel<WhenPredicate>(
                viewableItems = listOf(1.toUByte()),
                peekThroughSize = listOf(PeekThroughSize.Fixed(-24f)),
            ),
        )

        val result = transformCarouselDistribution(carouselDistributionModel)

        val peekThroughSize = result.peekThroughSizeUiModel.single()
        check(peekThroughSize is PeekThroughSizeUiModel.Fixed)
        assertThat(peekThroughSize.value).isGreaterThanOrEqualTo(0f)
    }

    @Test
    fun `transformCarouselDistribution coerces a negative percentage peek through size to zero`() {
        val carouselDistributionModel = LayoutSchemaModel.CarouselDistribution(
            CarouselDistributionModel<WhenPredicate>(
                viewableItems = listOf(1.toUByte()),
                peekThroughSize = listOf(PeekThroughSize.Percentage(-50f)),
            ),
        )

        val result = transformCarouselDistribution(carouselDistributionModel)

        val peekThroughSize = result.peekThroughSizeUiModel.single()
        check(peekThroughSize is PeekThroughSizeUiModel.Percentage)
        assertThat(peekThroughSize.value).isGreaterThanOrEqualTo(0f)
    }

    @Test
    fun `transformCarouselDistribution coerces a percentage peek through size above 100 to 100`() {
        val carouselDistributionModel = LayoutSchemaModel.CarouselDistribution(
            CarouselDistributionModel<WhenPredicate>(
                viewableItems = listOf(1.toUByte()),
                peekThroughSize = listOf(PeekThroughSize.Percentage(250f)),
            ),
        )

        val result = transformCarouselDistribution(carouselDistributionModel)

        val peekThroughSize = result.peekThroughSizeUiModel.single()
        check(peekThroughSize is PeekThroughSizeUiModel.Percentage)
        assertThat(peekThroughSize.value).isLessThanOrEqualTo(100f)
    }
}
