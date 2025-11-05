package com.rokt.modelmapper.uimodel

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.hmap.HMap
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed class LayoutSchemaUiModel(
    open val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>? = null,
    open val containerProperties: ImmutableList<StateBlock<ContainerProperties>>? = null,
    open val conditionalTransitionModifiers: ConditionalTransitionModifier? = null,
) {

    data class BasicTextUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val conditionalTransitionTextStyling: ConditionalTransitionTextStyling?,
        val textStyles: ImmutableList<StateBlock<TextStylingUiProperties>>?,
        val value: BindData,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class RichTextUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val conditionalTransitionTextStyling: ConditionalTransitionTextStyling?,
        val textStyles: ImmutableList<StateBlock<TextStylingUiProperties>>?,
        val linkStyles: ImmutableList<StateBlock<TextStylingUiProperties>>?,
        val openLinks: OpenLinks,
        val value: BindData,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class ColumnUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val isScrollable: Boolean,
        val children: ImmutableList<LayoutSchemaUiModel?>,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class RowUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val isScrollable: Boolean,
        val children: ImmutableList<LayoutSchemaUiModel?>,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class BoxUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val children: ImmutableList<LayoutSchemaUiModel?>,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    // Column
    data class CatalogStackedCollectionUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val children: ImmutableList<LayoutSchemaUiModel?>, // the type of it is either RowUiModel or ColumnUiModel
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class ProgressIndicatorUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val startPosition: Int,
        val accessibilityHidden: Boolean,
        val indicatorText: BindData,
        val indicator: ProgressIndicatorItemUiModel,
        val activeIndicator: ProgressIndicatorItemUiModel?,
        val seenIndicator: ProgressIndicatorItemUiModel?,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class ProgressIndicatorItemUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val textStyles: ImmutableList<StateBlock<TextStylingUiProperties>>?,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class CreativeResponseUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        override val children: ImmutableList<LayoutSchemaUiModel?>,
        val openLinks: OpenLinks,
        val responseOption: ResponseOptionModel?,
    ) : ButtonUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers, children)

    data class CloseButtonUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        override val children: ImmutableList<LayoutSchemaUiModel?>,
        val dismissalMethod: String?,
    ) : ButtonUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers, children)

    data class CatalogResponseButtonUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        override val children: ImmutableList<LayoutSchemaUiModel?>,
        val catalogItemModel: HMap?,
    ) : ButtonUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers, children)

    data class StaticLinkUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        override val children: ImmutableList<LayoutSchemaUiModel?>,
        val openLinks: OpenLinks,
        val src: String,
    ) : ButtonUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers, children)

    data class ToggleButtonStateTriggerUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        override val children: ImmutableList<LayoutSchemaUiModel?>,
        val customStateKey: String,
    ) : ButtonUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers, children)

    data class ProgressControlUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        override val children: ImmutableList<LayoutSchemaUiModel?>,
        val progressionDirection: ProgressUiDirection,
    ) : ButtonUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers, children)

    data class OneByOneDistributionUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val transition: TransitionUiModel,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class GroupedDistributionUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val viewableItems: ImmutableList<Int>,
        val transition: TransitionUiModel,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class CarouselDistributionUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val viewableItems: ImmutableList<Int>,
        val peekThroughSizeUiModel: ImmutableList<PeekThroughSizeUiModel>,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class MarketingUiModel(
        val preRenderMeasure: Boolean = false,
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>? = null,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>? = null,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier? = null,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class OverlayUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val allowBackdropToClose: Boolean,
        val child: ColumnUiModel,
        val edgeToEdgeDisplay: Boolean = true,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class BottomSheetUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val allowBackdropToClose: Boolean,
        val child: ColumnUiModel,
        val edgeToEdgeDisplay: Boolean = true,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class ImageUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val lightUrl: String,
        val darkUrl: String?,
        val title: String?,
        val scaleType: ContentScale?,
        val alt: String?,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class IconUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val textStyles: ImmutableList<StateBlock<TextStylingUiProperties>>?,
        val accessibilityHidden: Boolean,
        val value: String,
        val alt: String? = null,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class WhenUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>? = null,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>? = null,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier? = null,
        val predicates: ImmutableList<WhenUiPredicate>,
        val children: ImmutableList<LayoutSchemaUiModel?>,
        val transition: WhenUiTransition,
        val hide: WhenUiHidden?,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

    data class DataImageCarouselUiModel(
        override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>?,
        override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        override val conditionalTransitionModifiers: ConditionalTransitionModifier?,
        val images: Map<Int, ImageUiModel>,
        val duration: Long,
        val indicator: ProgressIndicatorItemUiModel?,
        val transition: DataImageTransition?,
        val activeIndicator: ProgressIndicatorItemUiModel?,
        val seenIndicator: ProgressIndicatorItemUiModel?,
        val progressIndicatorContainer: ProgressIndicatorItemUiModel?,
        val customStateKey: String,
    ) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)
}

sealed class ButtonUiModel(
    override val ownModifiers: ImmutableList<StateBlock<ModifierProperties>>? = null,
    override val containerProperties: ImmutableList<StateBlock<ContainerProperties>>? = null,
    override val conditionalTransitionModifiers: ConditionalTransitionModifier? = null,
    open val children: ImmutableList<LayoutSchemaUiModel?>,
) : LayoutSchemaUiModel(ownModifiers, containerProperties, conditionalTransitionModifiers)

@Immutable
data class ConditionalTransitionModifier(
    val modifier: ModifierProperties,
    override val predicates: ImmutableList<WhenUiPredicate>,
    override val duration: Int,
) : ConditionalTransition

@Immutable
data class ConditionalTransitionTextStyling(
    val textStyles: TextStylingUiProperties,
    override val predicates: ImmutableList<WhenUiPredicate>,
    override val duration: Int,
) : ConditionalTransition

@Immutable
interface ConditionalTransition {
    val predicates: ImmutableList<WhenUiPredicate>
    val duration: Int
}

@Immutable
data class StateBlock<T>(val default: T, val pressed: T? = null)

@Immutable
interface BaseTextStylingUiProperties {
    val textColor: ThemeColorUiModel?
    val textColorState: Color?
    val fontSize: Float?
    val fontFamily: String?
    val fontWeight: Int?
    val lineHeight: Float?
    val horizontalTextAlign: TextAlign?
    val baselineTextAlign: BaselineShift?
    val fontStyle: FontStyle?
    val textTransform: TextUiTransform?
    val letterSpacing: Float?
    val textDecoration: TextDecoration?
    val lineLimit: Int?
}

@Immutable
data class TextStylingUiProperties(
    override val textColor: ThemeColorUiModel? = null,
    override val textColorState: Color? = null,
    override val fontSize: Float? = null,
    override val fontFamily: String? = null,
    override val fontWeight: Int? = null,
    override val lineHeight: Float? = null,
    override val horizontalTextAlign: TextAlign? = null,
    override val baselineTextAlign: BaselineShift? = null,
    override val fontStyle: FontStyle? = null,
    override val textTransform: TextUiTransform? = null,
    override val letterSpacing: Float? = null,
    override val textDecoration: TextDecoration? = null,
    override val lineLimit: Int? = null,
) : BaseTextStylingUiProperties

@Immutable
class TransitionTextStylingUiProperties(
    override val textColor: ThemeColorUiModel? = null,
    override val fontFamily: String? = null,
    override val textTransform: TextUiTransform? = null,
    fontWeight: State<Int?>,
    textColorState: State<Color?>,
    fontSize: State<Float>,
    lineHeight: State<Float?>,
    horizontalTextAlign: State<TextAlign?>,
    baselineTextAlign: State<BaselineShift?>,
    fontStyle: State<FontStyle?>,
    letterSpacing: State<Float?>,
    textDecoration: State<TextDecoration?>,
    lineLimit: State<Int>,
) : BaseTextStylingUiProperties {
    override val fontWeight by fontWeight
    override val textColorState by textColorState
    override val fontSize by fontSize
    override val lineHeight by lineHeight
    override val horizontalTextAlign by horizontalTextAlign
    override val baselineTextAlign by baselineTextAlign
    override val fontStyle by fontStyle
    override val letterSpacing by letterSpacing
    override val textDecoration by textDecoration
    override val lineLimit by lineLimit
}

data class TextStyleUiState(
    val textStyle: TextStyle,
    val value: String,
    val textTransform: TextUiTransform,
    val lineLimit: Int,
)

enum class TextUiTransform(val string: String) {
    Capitalize("capitalize"),
    Uppercase("uppercase"),
    Lowercase("lowercase"),
    None("none"),
}

sealed class PeekThroughSizeUiModel(val value: Float) {
    class Fixed(value: Float) : PeekThroughSizeUiModel(value)
    class Percentage(value: Float) : PeekThroughSizeUiModel(value)
}

sealed class HeightUiModel {
    data class Fixed(val value: Float) : HeightUiModel()
    data class Percentage(val value: Float) : HeightUiModel()
    object MatchParent : HeightUiModel()
    object WrapContent : HeightUiModel()
}

sealed class WidthUiModel {
    data class Fixed(val value: Float) : WidthUiModel()
    data class Percentage(val value: Float) : WidthUiModel()
    object MatchParent : WidthUiModel()
    object WrapContent : WidthUiModel()
}

data class BackgroundImageUiModel(val url: ThemeColorUiModel, val position: Alignment, val scaleType: ContentScale)

@Immutable
interface BaseModifierProperties {
    val offset: DpOffset?
    val minHeight: Dp?
    val minWidth: Dp?
    val maxHeight: Dp?
    val maxWidth: Dp?
    val width: WidthUiModel?
    val height: HeightUiModel?
    val shadowColor: ThemeColorUiModel?
    val shadowBlurRadius: Dp?
    val shadowSpreadRadius: Float?
    val shadowOffset: DpOffset?
    val borderColor: ThemeColorUiModel?
    val borderRadius: Dp?
    val borderWidth: ImmutableList<Float>?
    val borderStyle: BorderStyleUiModel?
    val borderUseTopCornerRadius: Boolean?
    val blurRadius: Float?
    val backgroundColor: ThemeColorUiModel?
    val backgroundColorState: Color?
    var backgroundImage: BackgroundImageUiModel?
    val padding: PaddingValues?
    val margin: PaddingValues?
    val rotateZ: Float?
}

@Immutable
data class ModifierProperties(
    override val offset: DpOffset? = null,
    override val minHeight: Dp? = null,
    override val minWidth: Dp? = null,
    override val maxHeight: Dp? = null,
    override val maxWidth: Dp? = null,
    override val width: WidthUiModel? = null,
    override val height: HeightUiModel? = null,
    override val shadowColor: ThemeColorUiModel? = null,
    override val shadowBlurRadius: Dp? = null,
    override val shadowSpreadRadius: Float? = null,
    override val shadowOffset: DpOffset? = null,
    override val borderColor: ThemeColorUiModel? = null,
    override val borderRadius: Dp? = null,
    override val borderWidth: ImmutableList<Float>? = null,
    override val borderStyle: BorderStyleUiModel? = null,
    override val borderUseTopCornerRadius: Boolean? = null,
    override val blurRadius: Float? = null,
    override val backgroundColor: ThemeColorUiModel? = null,
    override val backgroundColorState: Color? = null,
    override var backgroundImage: BackgroundImageUiModel? = null,
    override val padding: PaddingValues? = null,
    override val margin: PaddingValues? = null,
    override val rotateZ: Float? = null,
) : BaseModifierProperties

@Immutable
data class ContainerProperties(
    val weight: Float? = null,
    val arrangementUiModel: ArrangementUiModel? = null,
    val alignmentUiModel: AlignmentUiModel? = null,
    val alignSelfVertical: AlignmentUiModel? = null,
    val alignSelfHorizontal: AlignmentUiModel? = null,
    val gap: Float? = null,
)

@Immutable
class TransitionModifierProperties(
    offset: State<DpOffset?>,
    minHeight: State<Dp?>,
    minWidth: State<Dp?>,
    maxHeight: State<Dp?>,
    maxWidth: State<Dp?>,
    width: State<WidthUiModel?>,
    height: State<HeightUiModel?>,
    override val shadowColor: ThemeColorUiModel?,
    override val shadowBlurRadius: Dp?,
    override val shadowSpreadRadius: Float?,
    override val shadowOffset: DpOffset?,
    override val borderColor: ThemeColorUiModel?,
    override val borderRadius: Dp?,
    override val borderWidth: ImmutableList<Float>?,
    override val borderStyle: BorderStyleUiModel?,
    override val borderUseTopCornerRadius: Boolean?,
    override val backgroundColor: ThemeColorUiModel? = null,
    override var backgroundImage: BackgroundImageUiModel? = null,
    blurRadius: State<Float?>,
    backgroundColorState: State<Color?>,
    padding: State<PaddingValues?>,
    margin: State<PaddingValues?>,
    rotateZ: State<Float?>,
) : BaseModifierProperties {
    override val offset by offset
    override val minHeight by minHeight
    override val minWidth by minWidth
    override val maxHeight by maxHeight
    override val maxWidth by maxWidth
    override val width by width
    override val height by height
    override val blurRadius by blurRadius
    override val backgroundColorState by backgroundColorState
    override val padding by padding
    override val margin by margin
    override val rotateZ by rotateZ
}

@Immutable
data class ContainerUiProperties(
    val weight: Float? = null,
    val selfAlignmentBias: Float? = null,
    val alignmentBias: Float,
    val arrangementBias: Float,
    val horizontalArrangement: Arrangement.Horizontal,
    val verticalArrangement: Arrangement.Vertical,
    val gap: Dp? = null,
)

@Immutable
sealed interface BorderStyleUiModel {
    object Solid : BorderStyleUiModel
    object Dashed : BorderStyleUiModel
}

sealed class AlignmentUiModel(val bias: Float) {
    object Center : AlignmentUiModel(bias = 0F)
    object Start : AlignmentUiModel(bias = -1F)
    object End : AlignmentUiModel(bias = 1F)
    object Stretch : AlignmentUiModel(bias = 2F)
}

sealed class ArrangementUiModel(val bias: Float) {
    object Center : ArrangementUiModel(bias = 0F)
    object Start : ArrangementUiModel(bias = -1F)
    object End : ArrangementUiModel(bias = 1F)
}

@Immutable
data class ThemeColorUiModel(
    val light: String? = null,
    val dark: String? = null,
    var isDarkModeEnabled: Boolean = false,
)

sealed class WhenUiPredicate {
    data class Breakpoint(val condition: OrderableWhenUiCondition, val value: String) : WhenUiPredicate()
    data class Position(val condition: OrderableWhenUiCondition, val value: String?) : WhenUiPredicate()
    data class Progression(val condition: OrderableWhenUiCondition, val value: String) : WhenUiPredicate()
    data class CreativeCopy(val condition: ExistenceWhenUiCondition, val value: String) : WhenUiPredicate()
    data class StaticBoolean(val condition: BooleanWhenUiCondition, val value: Boolean) : WhenUiPredicate()
    data class DarkMode(val condition: EqualityWhenUiCondition, val value: Boolean) : WhenUiPredicate()
    data class CustomState(val condition: OrderableWhenUiCondition, val key: String, val value: Int) : WhenUiPredicate()
    data class StaticString(val condition: EqualityWhenUiCondition, val input: String, val value: String) :
        WhenUiPredicate()
}

enum class OrderableWhenUiCondition {
    Is,
    IsNot,
    IsBelow,
    IsAbove,
}

enum class EqualityWhenUiCondition {
    Is,
    IsNot,
}

enum class ExistenceWhenUiCondition {
    Exists,
    NotExists,
}

enum class BooleanWhenUiCondition {
    IsTrue,
    IsFalse,
}

enum class OpenLinks {
    Internally,
    Externally,
    Passthrough,
}

data class DataImageTransition(val type: Type = Type.None, val settings: Settings? = null) {
    enum class Type {
        FadeInOut,
        SlideInOut,
        None,
    }

    data class Settings(val speed: String? = null) {
        fun durationMillis(): Int = when (speed?.lowercase()) {
            "slow" -> 1500
            "medium" -> 800
            "fast" -> 300
            else -> 500
        }
    }
}

data class WhenUiTransition(
    val inTransition: EnterTransition = EnterTransition.None,
    val outTransition: ExitTransition = ExitTransition.None,
)

sealed class InUiTransition {
    data class FadeInTransition(val duration: Int) : InUiTransition()
}

sealed class OutUiTransition {
    data class FadeOutTransition(val duration: Int) : OutUiTransition()
}

enum class WhenUiHidden {
    Visually,
    Functionally,
}

sealed class TransitionUiModel {
    data class FadeInOutTransition(val duration: Int) : TransitionUiModel()
}

enum class ProgressUiDirection {
    Forward,
    Backward,
}

enum class ConditionalStyleState { Normal, Transition }
