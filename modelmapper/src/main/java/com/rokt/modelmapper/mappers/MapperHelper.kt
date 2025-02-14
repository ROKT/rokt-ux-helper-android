package com.rokt.modelmapper.mappers

import com.rokt.network.model.BackgroundStylingProperties
import com.rokt.network.model.BasicStateStylingBlock
import com.rokt.network.model.BorderStylingProperties
import com.rokt.network.model.ContainerStylingProperties
import com.rokt.network.model.DimensionStylingProperties
import com.rokt.network.model.FlexChildStylingProperties
import com.rokt.network.model.Shadow
import com.rokt.network.model.SpacingStylingProperties
import com.rokt.network.model.StatelessStylingBlock
import com.rokt.network.model.TextStylingProperties
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

// Helper function to create BasicStateStylingBlock
internal inline fun <T, reified R> BasicStateStylingBlock<T>.toBasicStateStylingBlock(
    transform: (T) -> R?,
): BasicStateStylingBlock<R?> = BasicStateStylingBlock(
    default = transform(this.default),
    pressed = this.pressed?.let(transform)?.stateDeepCopy(transform(this.default)),
)

internal fun <E> ImmutableList<StatelessStylingBlock<E>>?.toBasicStateStylingBlock(): ImmutableList<BasicStateStylingBlock<E>>? =
    this?.map {
        BasicStateStylingBlock(
            default = it.default,
        )
    }?.toImmutableList()

private inline fun <reified R> R.stateDeepCopy(other: R?): R = when (this) {
    is BorderStylingProperties -> this.deepCopy(other as? BorderStylingProperties) as R
    is TextStylingProperties -> this.deepCopy(other as? TextStylingProperties) as R
    is Shadow -> this.deepCopy(other as? Shadow) as R
    is DimensionStylingProperties -> this.deepCopy(other as? DimensionStylingProperties) as R
    is SpacingStylingProperties -> this.deepCopy(other as? SpacingStylingProperties) as R
    is FlexChildStylingProperties -> this.deepCopy(other as? FlexChildStylingProperties) as R
    is BackgroundStylingProperties -> this.deepCopy(other as? BackgroundStylingProperties) as R
    is ContainerStylingProperties -> this.deepCopy(other as? ContainerStylingProperties) as R
    else -> this
}

private fun BorderStylingProperties.deepCopy(other: BorderStylingProperties?): BorderStylingProperties =
    BorderStylingProperties(
        borderRadius = this.borderRadius ?: other?.borderRadius,
        borderColor = this.borderColor ?: other?.borderColor,
        borderWidth = this.borderWidth ?: other?.borderWidth,
        borderStyle = this.borderStyle ?: other?.borderStyle,
    )

private fun TextStylingProperties.deepCopy(other: TextStylingProperties?): TextStylingProperties =
    TextStylingProperties(
        this.textColor ?: other?.textColor,
        this.fontSize ?: other?.fontSize,
        this.fontFamily ?: other?.fontFamily,
        this.fontWeight ?: other?.fontWeight,
        this.lineHeight ?: other?.lineHeight,
        this.horizontalTextAlign ?: other?.horizontalTextAlign,
        this.baselineTextAlign ?: other?.baselineTextAlign,
        this.fontStyle ?: other?.fontStyle,
        this.textTransform ?: other?.textTransform,
        this.letterSpacing ?: other?.letterSpacing,
        this.textDecoration ?: other?.textDecoration,
        this.lineLimit ?: other?.lineLimit,
    )

private fun Shadow.deepCopy(other: Shadow?): Shadow = Shadow(
    this.offsetX ?: other?.offsetX,
    this.offsetY ?: other?.offsetY,
    this.blurRadius ?: other?.blurRadius,
    this.spreadRadius ?: other?.spreadRadius,
    this.color,
)

private fun DimensionStylingProperties.deepCopy(other: DimensionStylingProperties?) = DimensionStylingProperties(
    this.minWidth ?: other?.minWidth,
    this.maxWidth ?: other?.maxWidth,
    this.width ?: other?.width,
    this.minHeight ?: other?.minHeight,
    this.maxHeight ?: other?.maxHeight,
    this.height ?: other?.height,
    this.rotateZ ?: other?.rotateZ,
)

private fun SpacingStylingProperties.deepCopy(other: SpacingStylingProperties?) = SpacingStylingProperties(
    this.padding ?: other?.padding,
    this.margin ?: other?.margin,
    this.offset ?: other?.offset,
)

private fun FlexChildStylingProperties.deepCopy(other: FlexChildStylingProperties?) = FlexChildStylingProperties(
    this.weight ?: other?.weight,
    this.order ?: other?.order,
    this.alignSelf ?: other?.alignSelf,
)

private fun BackgroundStylingProperties.deepCopy(other: BackgroundStylingProperties?) = BackgroundStylingProperties(
    this.backgroundColor ?: other?.backgroundColor,
    this.backgroundImage ?: other?.backgroundImage,
)

private fun ContainerStylingProperties.deepCopy(other: ContainerStylingProperties?): ContainerStylingProperties =
    ContainerStylingProperties(
        this.justifyContent ?: other?.justifyContent,
        this.alignItems ?: other?.alignItems,
        this.shadow?.deepCopy(other?.shadow),
        this.overflow ?: other?.overflow,
    )
