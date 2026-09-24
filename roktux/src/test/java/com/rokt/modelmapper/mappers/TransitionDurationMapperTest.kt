package com.rokt.modelmapper.mappers

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.uimodel.TransitionUiModel
import com.rokt.modelmapper.uimodel.WhenUiTransition
import com.rokt.network.model.BasicTextModel
import com.rokt.network.model.BasicTextTransitions
import com.rokt.network.model.CarouselDistributionModel
import com.rokt.network.model.CarouselDistributionTransitions
import com.rokt.network.model.CloseButtonModel
import com.rokt.network.model.CloseButtonTransitions
import com.rokt.network.model.ColumnModel
import com.rokt.network.model.ColumnTransitions
import com.rokt.network.model.ConditionalStyleTransition
import com.rokt.network.model.FadeInOutTransitionSettings
import com.rokt.network.model.FadeInTransitionSettings
import com.rokt.network.model.FadeOutTransitionSettings
import com.rokt.network.model.GroupedDistributionModel
import com.rokt.network.model.GroupedDistributionTransitions
import com.rokt.network.model.InTransition
import com.rokt.network.model.LayoutSchemaModel
import com.rokt.network.model.LayoutStyle
import com.rokt.network.model.OneByOneDistributionModel
import com.rokt.network.model.OneByOneDistributionTransitions
import com.rokt.network.model.OutTransition
import com.rokt.network.model.StaticIconModel
import com.rokt.network.model.StaticIconTransitions
import com.rokt.network.model.Transition
import com.rokt.network.model.WhenModel
import com.rokt.network.model.WhenTransition
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Malformed layout payloads can carry a negative animation `duration`. That value is copied
 * as-is from the network model into the UI model at several mapping sites and eventually reaches
 * Compose's `tween()`. These tests confirm every one of those sites coerces the duration to a
 * non-negative value before it leaves the mapping layer, regardless of what the payload supplied.
 */
class TransitionDurationMapperTest {

    @Test
    fun `transformWhen coerces negative fadeIn and fadeOut durations to zero`() {
        val whenModel = LayoutSchemaModel.When(
            WhenModel(
                predicates = emptyList(),
                children = emptyList(),
                transition = WhenTransition(
                    inTransition = listOf(InTransition.FadeIn(FadeInTransitionSettings(duration = -500))),
                    outTransition = listOf(OutTransition.FadeOut(FadeOutTransitionSettings(duration = -750))),
                ),
                hide = null,
            ),
        )

        val result = transformWhen(whenModel, transformLayoutSchemaChildren = { null })

        assertThat(result.transition).isEqualTo(
            WhenUiTransition(
                inTransition = fadeIn(animationSpec = tween(durationMillis = 0)),
                outTransition = fadeOut(animationSpec = tween(durationMillis = 0)),
            ),
        )
    }

    @Test
    fun `transformWhen preserves a valid positive duration`() {
        val whenModel = LayoutSchemaModel.When(
            WhenModel(
                predicates = emptyList(),
                children = emptyList(),
                transition = WhenTransition(
                    inTransition = listOf(InTransition.FadeIn(FadeInTransitionSettings(duration = 300))),
                    outTransition = listOf(OutTransition.FadeOut(FadeOutTransitionSettings(duration = 450))),
                ),
                hide = null,
            ),
        )

        val result = transformWhen(whenModel, transformLayoutSchemaChildren = { null })

        assertThat(result.transition).isEqualTo(
            WhenUiTransition(
                inTransition = fadeIn(animationSpec = tween(durationMillis = 300)),
                outTransition = fadeOut(animationSpec = tween(durationMillis = 450)),
            ),
        )
    }

    @Test
    fun `transformColumn coerces a negative conditionalTransitions duration to zero`() {
        val column = LayoutSchemaModel.Column(
            ColumnModel(
                styles = LayoutStyle(
                    elements = null,
                    conditionalTransitions = ConditionalStyleTransition(
                        predicates = emptyList(),
                        duration = -100,
                        value = ColumnTransitions(),
                    ),
                ),
                children = emptyList(),
            ),
        )

        val result = transformColumn(column, isScrollable = false, transformLayoutSchemaChildren = { null })

        assertThat(result.conditionalTransitionModifiers?.duration).isEqualTo(0)
    }

    @Test
    fun `transformCloseButton coerces a negative conditionalTransitions duration to zero`() {
        val closeButton = LayoutSchemaModel.CloseButton(
            CloseButtonModel(
                styles = LayoutStyle(
                    elements = null,
                    conditionalTransitions = ConditionalStyleTransition(
                        predicates = emptyList(),
                        duration = -250,
                        value = CloseButtonTransitions(),
                    ),
                ),
                children = emptyList(),
                dismissalMethod = "ALL",
            ),
        )

        val result = transformCloseButton(closeButton, transformLayoutSchemaChildren = { null })

        assertThat(result.conditionalTransitionModifiers?.duration).isEqualTo(0)
    }

    @Test
    fun `transformStaticIcon coerces a negative conditionalTransitions duration to zero`() {
        val staticIcon = LayoutSchemaModel.StaticIcon(
            StaticIconModel(
                styles = LayoutStyle(
                    elements = null,
                    conditionalTransitions = ConditionalStyleTransition(
                        predicates = emptyList(),
                        duration = -300,
                        value = StaticIconTransitions(),
                    ),
                ),
                name = "star",
                description = null,
            ),
        )

        val result = transformStaticIcon(staticIcon)

        assertThat(result.conditionalTransitionModifiers?.duration).isEqualTo(0)
    }

    @Test
    fun `transformBasicText coerces a negative conditionalTransitions duration to zero for both modifier and text styling`() {
        val basicText = LayoutSchemaModel.BasicText(
            BasicTextModel(
                styles = LayoutStyle(
                    elements = null,
                    conditionalTransitions = ConditionalStyleTransition(
                        predicates = emptyList(),
                        duration = -999,
                        value = BasicTextTransitions(),
                    ),
                ),
                value = "hello",
            ),
        )

        val result = transformBasicText(basicText, bindData = { BindData.Value(it) })

        assertThat(result.conditionalTransitionModifiers?.duration).isEqualTo(0)
        assertThat(result.conditionalTransitionTextStyling?.duration).isEqualTo(0)
    }

    @Test
    fun `transformOneByOneDistribution coerces a negative fadeInOut duration to zero`() {
        val distribution = LayoutSchemaModel.OneByOneDistribution(
            OneByOneDistributionModel(
                styles = LayoutStyle(elements = null, conditionalTransitions = null),
                transition = Transition.FadeInOut(FadeInOutTransitionSettings(duration = -400)),
            ),
        )

        val result = transformOneByOneDistribution(distribution)

        assertThat((result.transition as TransitionUiModel.FadeInOutTransition).duration).isEqualTo(0)
    }

    @Test
    fun `transformGroupedDistribution coerces a negative fadeInOut duration to zero`() {
        val distribution = LayoutSchemaModel.GroupedDistribution(
            GroupedDistributionModel(
                viewableItems = emptyList(),
                transition = Transition.FadeInOut(FadeInOutTransitionSettings(duration = -200)),
                styles = LayoutStyle(elements = null, conditionalTransitions = null),
            ),
        )

        val result = transformGroupedDistribution(distribution)

        assertThat((result.transition as TransitionUiModel.FadeInOutTransition).duration).isEqualTo(0)
    }

    @Test
    fun `transformCarouselDistribution coerces a negative conditionalTransitions duration to zero`() {
        val distribution = LayoutSchemaModel.CarouselDistribution(
            CarouselDistributionModel(
                viewableItems = emptyList(),
                peekThroughSize = emptyList(),
                styles = LayoutStyle(
                    elements = null,
                    conditionalTransitions = ConditionalStyleTransition(
                        predicates = emptyList(),
                        duration = -600,
                        value = CarouselDistributionTransitions(),
                    ),
                ),
            ),
        )

        val result = transformCarouselDistribution(distribution)

        assertThat(result.conditionalTransitionModifiers?.duration).isEqualTo(0)
    }
}
