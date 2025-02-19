package com.rokt.modelmapper.mappers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.rokt.modelmapper.uimodel.AlignmentUiModel
import com.rokt.modelmapper.uimodel.ArrangementUiModel
import com.rokt.modelmapper.uimodel.BackgroundImageUiModel
import com.rokt.modelmapper.uimodel.BorderStyleUiModel
import com.rokt.modelmapper.uimodel.ContainerProperties
import com.rokt.modelmapper.uimodel.HeightUiModel
import com.rokt.modelmapper.uimodel.ModifierProperties
import com.rokt.modelmapper.uimodel.OpenLinks
import com.rokt.modelmapper.uimodel.StateBlock
import com.rokt.modelmapper.uimodel.ThemeColorUiModel
import com.rokt.modelmapper.uimodel.WidthUiModel
import com.rokt.network.model.BackgroundImage
import com.rokt.network.model.BackgroundImagePosition
import com.rokt.network.model.BackgroundImageScale
import com.rokt.network.model.BackgroundStylingProperties
import com.rokt.network.model.BasicStateStylingBlock
import com.rokt.network.model.BorderStyle
import com.rokt.network.model.BorderStylingProperties
import com.rokt.network.model.ContainerStylingProperties
import com.rokt.network.model.DimensionHeightFitValue
import com.rokt.network.model.DimensionHeightValue
import com.rokt.network.model.DimensionStylingProperties
import com.rokt.network.model.DimensionWidthFitValue
import com.rokt.network.model.DimensionWidthValue
import com.rokt.network.model.FlexAlignment
import com.rokt.network.model.FlexChildStylingProperties
import com.rokt.network.model.FlexJustification
import com.rokt.network.model.LinkOpenTarget
import com.rokt.network.model.SpacingStylingProperties
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private const val paddingArraySize: Int = 4
private const val offsetArraySize: Int = 2
private const val top = 0
private const val end = 1
private const val bottom = 2
private const val start = 3

internal fun <T : BasicStateStylingBlock<*>> ImmutableList<T>?.transformModifier(
    transformSpacing: (T) -> BasicStateStylingBlock<SpacingStylingProperties?> = { BasicStateStylingBlock(null) },
    transformDimension: (T) -> BasicStateStylingBlock<DimensionStylingProperties?> = { BasicStateStylingBlock(null) },
    transformBackground: (T) -> BasicStateStylingBlock<BackgroundStylingProperties?>,
    transformBorder: (T) -> BasicStateStylingBlock<BorderStylingProperties?> =
        { BasicStateStylingBlock(null) },
    transformContainer: (T) -> BasicStateStylingBlock<ContainerStylingProperties?> =
        { BasicStateStylingBlock(null) },
): ImmutableList<StateBlock<ModifierProperties>>? {
    val spacingProperties = this?.map { transformSpacing(it) }?.toImmutableList()
    val dimensionProperties = this?.map { transformDimension(it) }?.toImmutableList()
    val backgroundProperties = this?.map { transformBackground(it) }?.toImmutableList()
    val borderProperties = this?.map { transformBorder(it) }?.toImmutableList()
    val containerProperties = this?.map { transformContainer(it) }?.toImmutableList()
    return this?.takeIf { it.isNotEmpty() }?.let {
        transformModifierList(
            spacingProperties,
            dimensionProperties,
            backgroundProperties,
            borderProperties,
            containerProperties,
            this.size,
        )
    }
}

private fun transformModifierList(
    spacingProperties: ImmutableList<BasicStateStylingBlock<SpacingStylingProperties?>>?,
    dimensionProperties: ImmutableList<BasicStateStylingBlock<DimensionStylingProperties?>>?,
    backgroundProperties: ImmutableList<BasicStateStylingBlock<BackgroundStylingProperties?>>?,
    borderProperties: ImmutableList<BasicStateStylingBlock<BorderStylingProperties?>>?,
    containerProperties: ImmutableList<BasicStateStylingBlock<ContainerStylingProperties?>>?,
    breakpoints: Int,
): ImmutableList<StateBlock<ModifierProperties>> = List(breakpoints) { i ->
    StateBlock(
        default = transformModifier(
            spacingProperties?.getOrNull(i)?.default,
            dimensionProperties?.getOrNull(i)?.default,
            backgroundProperties?.getOrNull(i)?.default,
            borderProperties?.getOrNull(i)?.default,
            containerProperties?.getOrNull(i)?.default,
        ),
        pressed = transformModifier(
            spacingProperties?.getOrNull(i)?.pressed,
            dimensionProperties?.getOrNull(i)?.pressed,
            backgroundProperties?.getOrNull(i)?.pressed,
            borderProperties?.getOrNull(i)?.pressed,
            containerProperties?.getOrNull(i)?.pressed,
        ),
    )
}.toImmutableList()

internal fun transformModifier(
    spacingProperties: SpacingStylingProperties?,
    dimensionProperties: DimensionStylingProperties?,
    backgroundProperties: BackgroundStylingProperties?,
    borderProperties: BorderStylingProperties? = null,
    containerProperties: ContainerStylingProperties? = null,
): ModifierProperties = ModifierProperties(
    minHeight = dimensionProperties?.minHeight?.dp,
    minWidth = dimensionProperties?.minWidth?.dp,
    maxHeight = dimensionProperties?.maxHeight?.dp,
    maxWidth = dimensionProperties?.maxWidth?.dp,
    height = transformHeight(dimensionProperties?.height),
    width = transformWidth(dimensionProperties?.width),
    padding = spacingProperties?.padding?.let { transformPadding(it) },
    margin = spacingProperties?.margin?.let { transformPadding(it) },
    offset = spacingProperties?.offset?.let { transformOffset(it) },
    rotateZ = dimensionProperties?.rotateZ,
    shadowColor = containerProperties?.shadow?.color?.let { ThemeColorUiModel(it.light, it.dark) },
    shadowOffset = containerProperties?.shadow?.let { DpOffset(it.offsetX?.dp ?: 0.dp, it.offsetY?.dp ?: 0.dp) },
    shadowBlurRadius = containerProperties?.shadow?.blurRadius?.dp,
    shadowSpreadRadius = containerProperties?.shadow?.spreadRadius,
    borderColor = borderProperties?.borderColor?.let { ThemeColorUiModel(it.light, it.dark) },
    borderRadius = borderProperties?.borderRadius?.dp,
    borderWidth = borderProperties?.borderWidth?.let { transformBorderWidth(it) },
    borderStyle = if (borderProperties?.borderStyle ==
        BorderStyle.Dashed
    ) {
        BorderStyleUiModel.Dashed
    } else {
        BorderStyleUiModel.Solid
    },
    borderUseTopCornerRadius = false,
    blurRadius = containerProperties?.blur,
    backgroundColor = backgroundProperties?.backgroundColor?.let { ThemeColorUiModel(it.light, it.dark) },
    backgroundImage = transformBackgroundImage(backgroundProperties?.backgroundImage),
)

private fun transformHeight(height: DimensionHeightValue?): HeightUiModel? = height?.let { heightValue ->
    when (heightValue) {
        is DimensionHeightValue.Fit -> if (heightValue.value == DimensionHeightFitValue.WrapContent) {
            HeightUiModel.WrapContent
        } else {
            HeightUiModel.MatchParent
        }

        is DimensionHeightValue.Fixed -> HeightUiModel.Fixed(heightValue.value)
        is DimensionHeightValue.Percentage -> HeightUiModel.Percentage(heightValue.value.div(100))
    }
}

private fun transformWidth(width: DimensionWidthValue?): WidthUiModel? = width?.let { widthValue ->
    when (widthValue) {
        is DimensionWidthValue.Fit -> if (widthValue.value == DimensionWidthFitValue.WrapContent) {
            WidthUiModel.WrapContent
        } else {
            WidthUiModel.MatchParent
        }

        is DimensionWidthValue.Fixed -> WidthUiModel.Fixed(widthValue.value)
        is DimensionWidthValue.Percentage -> WidthUiModel.Percentage(widthValue.value.div(100))
    }
}

private fun transformBackgroundImage(backgroundImage: BackgroundImage?): BackgroundImageUiModel? =
    backgroundImage?.let { image ->
        BackgroundImageUiModel(
            url = ThemeColorUiModel(image.url.light, image.url.dark),
            position = when (image.position) {
                BackgroundImagePosition.Bottom -> Alignment.BottomCenter
                BackgroundImagePosition.Center -> Alignment.Center
                BackgroundImagePosition.Top -> Alignment.TopCenter
                BackgroundImagePosition.BottomLeft -> Alignment.BottomStart
                BackgroundImagePosition.Left -> Alignment.CenterStart
                BackgroundImagePosition.TopLeft -> Alignment.TopStart
                BackgroundImagePosition.BottomRight -> Alignment.BottomEnd
                BackgroundImagePosition.Right -> Alignment.CenterEnd
                BackgroundImagePosition.TopRight -> Alignment.TopEnd
                else -> Alignment.TopStart
            },
            scaleType = when (image.scale) {
                BackgroundImageScale.Crop -> ContentScale.None
                BackgroundImageScale.Fill -> ContentScale.Crop
                BackgroundImageScale.Fit -> ContentScale.Fit
                else -> ContentScale.Fit
            },
        )
    }

internal fun <T : BasicStateStylingBlock<*>> ImmutableList<T>?.transformContainer(
    transformFlexChild: (T) -> BasicStateStylingBlock<FlexChildStylingProperties?> = { BasicStateStylingBlock(null) },
    transformContainer: (T) -> BasicStateStylingBlock<ContainerStylingProperties?> =
        { BasicStateStylingBlock(null) },
): ImmutableList<StateBlock<ContainerProperties>>? {
    val containerProperties = this?.map { transformContainer(it) }?.toImmutableList()
    val flexChildProperties = this?.map { transformFlexChild(it) }?.toImmutableList()
    return this?.takeIf { it.isNotEmpty() }?.let {
        transformContainerPropertiesList(
            flexChildProperties,
            containerProperties,
            this.size,
        )
    }
}

private fun transformContainerPropertiesList(
    flexChildProperties: ImmutableList<BasicStateStylingBlock<FlexChildStylingProperties?>>?,
    containerProperties: ImmutableList<BasicStateStylingBlock<ContainerStylingProperties?>>?,
    breakpoints: Int,
): ImmutableList<StateBlock<ContainerProperties>> = List(breakpoints) { i ->
    StateBlock(
        default = transformContainerProperties(
            flexChildProperties?.getOrNull(i)?.default,
            containerProperties?.getOrNull(i)?.default,
        ),
        pressed = transformContainerProperties(
            flexChildProperties?.getOrNull(i)?.pressed,
            containerProperties?.getOrNull(i)?.pressed,
        ),
    )
}.toImmutableList()

private fun transformContainerProperties(
    flexChildProperties: FlexChildStylingProperties?,
    containerProperties: ContainerStylingProperties?,
): ContainerProperties = ContainerProperties(
    weight = flexChildProperties?.weight,
    arrangementUiModel = transformArrangement(containerProperties?.justifyContent),
    alignmentUiModel = transformAlignment(containerProperties?.alignItems),
    gap = containerProperties?.gap,
    alignSelfVertical = transformSelfAlignment(flexChildProperties?.alignSelf),
    alignSelfHorizontal = transformSelfAlignment(flexChildProperties?.alignSelf),
)

private fun transformArrangement(arrangementModel: FlexJustification?) = arrangementModel?.let { arrangement ->
    when (arrangement) {
        FlexJustification.Center -> ArrangementUiModel.Center
        FlexJustification.FlexStart -> ArrangementUiModel.Start
        FlexJustification.FlexEnd -> ArrangementUiModel.End
    }
}

private fun transformAlignment(alignmentModel: FlexAlignment?) = alignmentModel?.let { alignment ->
    when (alignment) {
        FlexAlignment.Center -> AlignmentUiModel.Center
        FlexAlignment.FlexStart -> AlignmentUiModel.Start
        FlexAlignment.FlexEnd -> AlignmentUiModel.End
        FlexAlignment.Stretch -> AlignmentUiModel.Stretch
    }
}

private fun transformSelfAlignment(alignSelf: FlexAlignment?): AlignmentUiModel? = alignSelf?.run {
    when (this) {
        FlexAlignment.Center -> AlignmentUiModel.Center
        FlexAlignment.FlexStart -> AlignmentUiModel.Start
        FlexAlignment.FlexEnd -> AlignmentUiModel.End
        FlexAlignment.Stretch -> AlignmentUiModel.Stretch
    }
}

private fun transformBorderWidth(width: String): ImmutableList<Float> {
    val split = try {
        width.split(" ").map(String::toFloat).toImmutableList()
    } catch (e: Exception) {
        persistentListOf(0F, 0F, 0F, 0F)
    }
    // Array is of order top right bottom left
    return when (split.size) {
        4 -> split
        3 -> persistentListOf(split[0], split[1], split[2], split[1])
        2 -> persistentListOf(split[0], split[1], split[0], split[1])
        1 -> persistentListOf(split[0], split[0], split[0], split[0])
        else -> persistentListOf(0F, 0F, 0F, 0F)
    }
}

private fun transformOffset(offset: String): DpOffset {
    val values = splitValues(offset, offsetArraySize)
    return DpOffset(
        values.getOrNull(0) ?: 0.dp,
        values.getOrNull(1) ?: 0.dp,
    )
}

private fun transformPadding(padding: String?): PaddingValues {
    val defaultPadding = PaddingValues(0.dp)
    return padding?.let {
        val values = splitValues(it, paddingArraySize)
        when (values.size) {
            1 -> PaddingValues(values[top])
            2 -> PaddingValues(values[end], values[top])
            3 -> PaddingValues(values[end], values[top], values[end], values[bottom])
            4 -> PaddingValues(values[start], values[top], values[end], values[bottom])
            else -> defaultPadding
        }
    } ?: defaultPadding
}

internal fun transformOpenLinks(openTarget: LinkOpenTarget?): OpenLinks = when (openTarget) {
    LinkOpenTarget.Internally -> OpenLinks.Internally
    else -> OpenLinks.Externally
}

private fun splitValues(value: String, size: Int): ImmutableList<Dp> = value.split(' ').take(size).map {
    it.toIntOrNull()?.dp ?: 0.dp
}.toImmutableList()

private fun DimensionHeightValue?.toDimensionFitValue() = (this as? DimensionHeightValue.Fit)?.value?.let {
    if (it == DimensionHeightFitValue.WrapContent) 0F else 1F
}

private fun DimensionWidthValue?.toDimensionFitValue() = (this as? DimensionWidthValue.Fit)?.value?.let {
    if (it == DimensionWidthFitValue.WrapContent) 0F else 1F
}
