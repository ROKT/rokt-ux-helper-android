package com.rokt.roktux.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.os.Build
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.times
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toIntSize
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.rokt.modelmapper.uimodel.ArrangementUiModel
import com.rokt.modelmapper.uimodel.BaseModifierProperties
import com.rokt.modelmapper.uimodel.BaseTextStylingUiProperties
import com.rokt.modelmapper.uimodel.BorderStyleUiModel
import com.rokt.modelmapper.uimodel.ConditionalStyleState
import com.rokt.modelmapper.uimodel.ConditionalTransitionModifier
import com.rokt.modelmapper.uimodel.ConditionalTransitionTextStyling
import com.rokt.modelmapper.uimodel.ContainerProperties
import com.rokt.modelmapper.uimodel.ContainerUiProperties
import com.rokt.modelmapper.uimodel.HeightUiModel
import com.rokt.modelmapper.uimodel.ModifierProperties
import com.rokt.modelmapper.uimodel.StateBlock
import com.rokt.modelmapper.uimodel.TextStyleUiState
import com.rokt.modelmapper.uimodel.TextStylingUiProperties
import com.rokt.modelmapper.uimodel.TextUiTransform
import com.rokt.modelmapper.uimodel.ThemeColorUiModel
import com.rokt.modelmapper.uimodel.TransitionModifierProperties
import com.rokt.modelmapper.uimodel.TransitionTextStylingUiProperties
import com.rokt.modelmapper.uimodel.WidthUiModel
import com.rokt.roktux.di.layout.LocalFontFamilyProvider
import com.rokt.roktux.di.layout.LocalLayoutComponent
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.min
import kotlin.math.pow

@Immutable
internal class ModifierFactory {

    @SuppressLint("ComposeUnstableReceiver")
    @Composable
    fun createModifier(
        modifierPropertiesList: ImmutableList<StateBlock<ModifierProperties>>?,
        conditionalTransitionModifier: ConditionalTransitionModifier?,
        breakpointIndex: Int,
        isPressed: Boolean,
        isDarkModeEnabled: Boolean,
        offerState: OfferUiState,
        basePropertiesList: ImmutableList<StateBlock<ModifierProperties>>? = null,
    ): Modifier {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val imageLoader = LocalLayoutComponent.current[ImageLoader::class.java]
        val transitionStyleState = evaluateState(
            predicates = conditionalTransitionModifier?.predicates,
            breakpointIndex = breakpointIndex,
            isDarkModeEnabled = isDarkModeEnabled,
            offerState = offerState,
        )
        return if (transitionStyleState != null && conditionalTransitionModifier != null) {
            val transitionData = updateTransitionData(
                modifierPropertiesList = modifierPropertiesList,
                breakpointIndex = breakpointIndex,
                isPressed = isPressed,
                isDarkModeEnabled = isDarkModeEnabled,
                transitionModifier = conditionalTransitionModifier.modifier,
                transitionDuration = conditionalTransitionModifier.duration,
                transitionStyleState = transitionStyleState,
            )
            Modifier.applyProperties(transitionData, isDarkModeEnabled, coroutineScope, context, imageLoader)
        } else {
            remember(modifierPropertiesList, breakpointIndex, isPressed, isDarkModeEnabled, basePropertiesList) {
                val modifierProperty =
                    createModifierProperties(isPressed, breakpointIndex, modifierPropertiesList, basePropertiesList)
                Modifier.applyProperties(modifierProperty, isDarkModeEnabled, coroutineScope, context, imageLoader)
            }
        }
    }

    @Composable
    fun updateTransitionData(
        modifierPropertiesList: ImmutableList<StateBlock<ModifierProperties>>?,
        breakpointIndex: Int,
        isPressed: Boolean,
        isDarkModeEnabled: Boolean,
        transitionModifier: ModifierProperties,
        transitionDuration: Int,
        transitionStyleState: ConditionalStyleState,
    ): TransitionModifierProperties {
        val modifierProperty =
            createModifierProperties(isPressed, breakpointIndex, modifierPropertiesList)
        val transition = updateTransition(transitionStyleState, label = "transitionStyle state")

        val backgroundColor = transition.animateColor({ tween(transitionDuration) }, label = "color") { state ->
            when (state) {
                ConditionalStyleState.Normal -> modifierProperty.backgroundColor?.let {
                    getUiThemeColor(
                        it,
                        isDarkModeEnabled,
                    )
                }

                ConditionalStyleState.Transition ->
                    transitionModifier.backgroundColor?.let {
                        getUiThemeColor(
                            it,
                            isDarkModeEnabled,
                        )
                    } ?: modifierProperty.backgroundColor?.let {
                        getUiThemeColor(
                            it,
                            isDarkModeEnabled,
                        )
                    }
            } ?: Color.Unspecified
        }

        val padding: State<PaddingValues?> =
            transition.animateValue(paddingToVector, { tween(transitionDuration) }, label = "padding") { state ->
                when (state) {
                    ConditionalStyleState.Normal -> modifierProperty.padding
                    ConditionalStyleState.Transition -> transitionModifier.padding ?: modifierProperty.padding
                }
            }

        val margin: State<PaddingValues?> =
            transition.animateValue(paddingToVector, { tween(transitionDuration) }, label = "margin") { state ->
                when (state) {
                    ConditionalStyleState.Normal -> modifierProperty.margin
                    ConditionalStyleState.Transition -> transitionModifier.margin ?: modifierProperty.margin
                }
            }

        val offset = transition.animateValue(
            DpOffset.VectorConverter,
            {
                tween(transitionDuration)
            },
            label = "offset",
        ) { state ->
            when (state) {
                ConditionalStyleState.Normal -> modifierProperty.offset
                ConditionalStyleState.Transition ->
                    transitionModifier.offset ?: modifierProperty.offset
            } ?: DpOffset(Dp.Hairline, Dp.Hairline)
        }

        val minWidth = transition.animateDp({ tween(transitionDuration) }, label = "minWidth") { state ->
            when (state) {
                ConditionalStyleState.Normal -> modifierProperty.minWidth
                ConditionalStyleState.Transition ->
                    transitionModifier.minWidth
            } ?: Dp.Hairline
        }

        val minHeight = transition.animateDp({ tween(transitionDuration) }, label = "minHeight") { state ->
            when (state) {
                ConditionalStyleState.Normal -> modifierProperty.minHeight
                ConditionalStyleState.Transition ->
                    transitionModifier.minHeight ?: modifierProperty.minHeight
            } ?: Dp.Hairline
        }

        val maxWidth = transition.animateDp({ tween(transitionDuration) }, label = "maxWidth") { state ->
            when (state) {
                ConditionalStyleState.Normal -> modifierProperty.maxWidth
                ConditionalStyleState.Transition ->
                    transitionModifier.maxWidth ?: modifierProperty.maxWidth
            } ?: Dp.Hairline
        }

        val maxHeight = transition.animateDp({ tween(transitionDuration) }, label = "maxHeight") { state ->
            when (state) {
                ConditionalStyleState.Normal -> modifierProperty.maxHeight ?: Dp.Hairline
                ConditionalStyleState.Transition ->
                    transitionModifier.maxHeight ?: modifierProperty.maxHeight
            } ?: Dp.Hairline
        }

        val height: State<HeightUiModel?> =
            transition.animateValue(heightToVectorConverter, { tween(transitionDuration) }, label = "height") { state ->
                when (state) {
                    ConditionalStyleState.Normal -> modifierProperty.height
                    ConditionalStyleState.Transition -> transitionModifier.height ?: modifierProperty.height
                }
            }

        val width: State<WidthUiModel?> =
            transition.animateValue(widthToVectorConverter, { tween(transitionDuration) }, label = "width") { state ->
                when (state) {
                    ConditionalStyleState.Normal -> modifierProperty.width
                    ConditionalStyleState.Transition -> transitionModifier.width ?: modifierProperty.width
                }
            }
        val rotation = transition.animateFloat({ tween(transitionDuration) }, label = "rotation") { state ->
            when (state) {
                ConditionalStyleState.Normal -> modifierProperty.rotateZ
                ConditionalStyleState.Transition ->
                    transitionModifier.rotateZ ?: modifierProperty.rotateZ
            } ?: Float.MIN_VALUE
        }

        val blurRadius = transition.animateFloat({ tween(transitionDuration) }, label = "blurRadius") { state ->
            when (state) {
                ConditionalStyleState.Normal -> modifierProperty.blurRadius
                ConditionalStyleState.Transition ->
                    transitionModifier.blurRadius ?: modifierProperty.blurRadius
            } ?: Float.MIN_VALUE
        }

        val shadowColor = when (transitionStyleState) {
            ConditionalStyleState.Normal -> modifierProperty.shadowColor
            ConditionalStyleState.Transition -> transitionModifier.shadowColor ?: modifierProperty.shadowColor
        }

        val shadowOffset = when (transitionStyleState) {
            ConditionalStyleState.Normal -> modifierProperty.shadowOffset
            ConditionalStyleState.Transition -> transitionModifier.shadowOffset ?: modifierProperty.shadowOffset
        }

        val shadowBlurRadius = when (transitionStyleState) {
            ConditionalStyleState.Normal -> modifierProperty.shadowBlurRadius
            ConditionalStyleState.Transition -> transitionModifier.shadowBlurRadius ?: modifierProperty.shadowBlurRadius
        }

        val shadowSpreadRadius = when (transitionStyleState) {
            ConditionalStyleState.Normal -> modifierProperty.shadowSpreadRadius
            ConditionalStyleState.Transition ->
                transitionModifier.shadowSpreadRadius
                    ?: modifierProperty.shadowSpreadRadius
        }

        val borderColor = when (transitionStyleState) {
            ConditionalStyleState.Normal -> modifierProperty.borderColor
            ConditionalStyleState.Transition -> transitionModifier.borderColor ?: modifierProperty.borderColor
        }

        val borderRadius = when (transitionStyleState) {
            ConditionalStyleState.Normal -> modifierProperty.borderRadius
            ConditionalStyleState.Transition -> transitionModifier.borderRadius ?: modifierProperty.borderRadius
        }

        val borderWidth = when (transitionStyleState) {
            ConditionalStyleState.Normal -> modifierProperty.borderWidth
            ConditionalStyleState.Transition -> transitionModifier.borderWidth ?: modifierProperty.borderWidth
        }

        val borderStyle = when (transitionStyleState) {
            ConditionalStyleState.Normal -> modifierProperty.borderStyle
            ConditionalStyleState.Transition -> transitionModifier.borderStyle ?: modifierProperty.borderStyle
        }

        return remember(transition) {
            TransitionModifierProperties(
                offset = offset,
                minHeight = minHeight,
                minWidth = minWidth,
                maxHeight = maxHeight,
                maxWidth = maxWidth,
                width = width,
                height = height,
                shadowColor = shadowColor,
                shadowOffset = shadowOffset,
                shadowBlurRadius = shadowBlurRadius,
                shadowSpreadRadius = shadowSpreadRadius,
                borderColor = borderColor,
                borderRadius = borderRadius,
                borderWidth = borderWidth,
                borderStyle = borderStyle,
                borderUseTopCornerRadius = modifierProperty.borderUseTopCornerRadius,
                blurRadius = blurRadius,
                backgroundColorState = backgroundColor,
                padding = padding,
                margin = margin,
                rotateZ = rotation,
            )
        }
    }

    private fun createModifierProperties(
        isPressed: Boolean,
        index: Int,
        modifierPropertiesList: ImmutableList<StateBlock<ModifierProperties>>?,
        basePropertiesList: ImmutableList<StateBlock<ModifierProperties>>? = null,
    ): ModifierProperties {
        var modifierProperty = ModifierProperties()

        fun applyPropertiesFromModifierList(modifierProperties: ImmutableList<StateBlock<ModifierProperties>>?) {
            for (iteration in 0..1) {
                val usePressed = isPressed && iteration == 0
                for (i in index downTo 0) {
                    modifierProperties?.getOrNull(i)?.let { stateBlock ->
                        val properties = if (usePressed) stateBlock.pressed else stateBlock.default
                        properties?.let {
                            modifierProperty = modifierProperty.applyProperties(it)
                            if (usePressed && i == 0) {
                                modifierProperty = modifierProperty.applyProperties(stateBlock.default)
                            }
                        }
                    }
                }
                if (!usePressed) break
            }
        }

        applyPropertiesFromModifierList(modifierPropertiesList)
        applyPropertiesFromModifierList(basePropertiesList)
        return modifierProperty
    }

    private fun Modifier.applyProperties(
        properties: BaseModifierProperties,
        isDarkModeEnabled: Boolean,
        coroutineScope: CoroutineScope,
        context: Context,
        imageLoader: ImageLoader,
    ): Modifier {
        val shape = createBackgroundShape(properties = properties)
        return this
            .then(properties.margin?.let { Modifier.padding(it) } ?: Modifier)
            .then(properties.offset?.let { Modifier.offset(it.x, it.y) } ?: Modifier)
            .then(properties.minHeight?.let { Modifier.heightIn(min = it) } ?: Modifier)
            .then(properties.minWidth?.let { Modifier.widthIn(min = it) } ?: Modifier)
            .then(
                properties.maxHeight
                    ?.takeIf { it > Dp.Hairline }
                    ?.let { Modifier.heightIn(max = it) } ?: Modifier,
            )
            .then(
                properties.maxWidth
                    ?.takeIf { it > Dp.Hairline }
                    ?.let { Modifier.widthIn(max = it) } ?: Modifier,
            )
            .then(
                properties.width?.let {
                    when (it) {
                        is WidthUiModel.Fixed -> Modifier.width(it.value.dp)
                        WidthUiModel.MatchParent -> Modifier.fillMaxWidth()
                        is WidthUiModel.Percentage -> Modifier.fillMaxWidth(it.value)
                        WidthUiModel.WrapContent -> Modifier.wrapContentWidth()
                    }
                } ?: Modifier,
            )
            .then(
                properties.height?.let {
                    when (it) {
                        is HeightUiModel.Fixed -> Modifier.height(it.value.dp)
                        HeightUiModel.MatchParent -> Modifier.fillMaxHeight()
                        is HeightUiModel.Percentage -> Modifier.fillMaxHeight(it.value)
                        HeightUiModel.WrapContent -> Modifier.wrapContentHeight()
                    }
                } ?: Modifier,
            )
            .then(properties.rotateZ?.let { Modifier.rotate(it) } ?: Modifier)
            .then(
                properties.shadowColor?.let {
                    Modifier.coloredShadow(
                        color = getUiThemeColor(it, isDarkModeEnabled),
                        blurRadius = properties.shadowBlurRadius ?: 0.dp,
                        spread = properties.shadowSpreadRadius ?: 0f,
                        offsetX = properties.shadowOffset?.x ?: 0.dp,
                        offsetY = properties.shadowOffset?.y ?: 0.dp,
                        cornerRadiusDp = properties.borderRadius ?: 0.dp,
                    )
                } ?: Modifier,
            )
            .then(
                properties.borderWidth?.let {
                    Modifier.multiDimensionalBorder(
                        it,
                        getUiThemeColor(properties.borderColor, isDarkModeEnabled),
                        properties.borderRadius ?: 0.dp,
                        properties.borderStyle ?: BorderStyleUiModel.Solid,
                        shape,
                    )
                } ?: Modifier,
            )
            .then(
                properties.backgroundColorState?.let {
                    Modifier.background(color = it, shape = shape)
                } ?: properties.backgroundColor?.let {
                    Modifier.background(color = getUiThemeColor(it, isDarkModeEnabled), shape = shape)
                } ?: Modifier,
            )
            .then(
                properties.backgroundImage?.let {
                    Modifier.backgroundImage(
                        url = getUiThemeUrl(it.url, isDarkModeEnabled),
                        alignment = it.position,
                        scale = it.scaleType,
                        coroutineScope = coroutineScope,
                        context = context,
                        imageLoader = imageLoader,
                    )
                } ?: Modifier,
            )
            .then(
                properties.blurRadius?.let {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        Modifier.alpha(blurRadiusToAlpha(it))
                    } else {
                        Modifier.blur(it.dp)
                    }
                } ?: Modifier,
            )
            .then(properties.padding?.let { Modifier.padding(it) } ?: Modifier)
    }

    private fun ModifierProperties.applyProperties(properties: ModifierProperties): ModifierProperties = copy(
        margin = margin ?: properties.margin,
        offset = offset ?: properties.offset,
        minHeight = minHeight ?: properties.minHeight,
        minWidth = minWidth ?: properties.minWidth,
        maxHeight = maxHeight ?: properties.maxHeight,
        maxWidth = maxWidth ?: properties.maxWidth,
        width = width ?: properties.width,
        height = height ?: properties.height,
        rotateZ = rotateZ ?: properties.rotateZ,
        shadowColor = shadowColor ?: properties.shadowColor,
        shadowBlurRadius = shadowBlurRadius ?: properties.shadowBlurRadius,
        shadowOffset = shadowOffset ?: properties.shadowOffset,
        shadowSpreadRadius = shadowSpreadRadius ?: properties.shadowSpreadRadius,
        borderColor = borderColor ?: properties.borderColor,
        borderRadius = borderRadius ?: properties.borderRadius,
        borderWidth = borderWidth ?: properties.borderWidth,
        borderStyle = borderStyle ?: properties.borderStyle,
        borderUseTopCornerRadius = borderUseTopCornerRadius ?: properties.borderUseTopCornerRadius,
        backgroundColor = backgroundColor ?: properties.backgroundColor,
        backgroundImage = backgroundImage ?: properties.backgroundImage,
        blurRadius = blurRadius ?: properties.blurRadius,
        padding = padding ?: properties.padding,
    )

    private fun Modifier.coloredShadow(
        color: Color,
        blurRadius: Dp,
        offsetY: Dp,
        offsetX: Dp,
        spread: Float,
        cornerRadiusDp: Dp,
    ) = drawBehind {
        drawIntoCanvas {
            val paint = Paint()
            val spreadPixel = spread.dp.toPx()
            val leftPixel = (0f - spreadPixel) + offsetX.toPx()
            val topPixel = (0f - spreadPixel) + offsetY.toPx()
            val rightPixel = (this.size.width + spreadPixel + offsetX.toPx())
            val bottomPixel = (this.size.height + spreadPixel + offsetY.toPx())
            paint.asFrameworkPaint().apply {
                this.color = color.toArgb()
                if (blurRadius != 0.dp) {
                    this.maskFilter = (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
                }
            }

            clipPath(
                path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(0f, 0f, size.width, size.height),
                            cornerRadius = CornerRadius(cornerRadiusDp.toPx(), cornerRadiusDp.toPx()),
                        ),
                    )
                },
                clipOp = ClipOp.Difference,
            ) {
                it.drawPath(
                    path = Path().apply {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(leftPixel, topPixel, rightPixel, bottomPixel),
                                cornerRadius = CornerRadius(cornerRadiusDp.toPx(), cornerRadiusDp.toPx()),
                            ),
                        )
                    },
                    paint = paint,
                )
            }
        }
    }

    private fun Modifier.backgroundImage(
        url: String,
        alignment: Alignment,
        scale: ContentScale,
        coroutineScope: CoroutineScope,
        context: Context,
        imageLoader: ImageLoader,
    ): Modifier = this then Modifier.clipToBounds() then BackgroundImageElement(
        url,
        alignment,
        scale,
        coroutineScope,
        context,
        imageLoader,
    )

    private class BackgroundImageNode(var bitmap: Bitmap?, var alignment: Alignment, var scale: ContentScale) :
        Modifier.Node(),
        DrawModifierNode {
        override fun ContentDrawScope.draw() {
            var offset = IntOffset.Zero
            bitmap?.let {
                val srcSize = Size(it.width.toFloat(), it.height.toFloat())
                val targetSize = srcSize.times(scale.computeScaleFactor(srcSize, this.size))
                offset = alignment.align(
                    size = targetSize.toIntSize(),
                    space = this.size.toIntSize(),
                    layoutDirection = layoutDirection,
                )
                Bitmap.createScaledBitmap(it, targetSize.width.toInt(), targetSize.height.toInt(), false)
            }?.asImageBitmap()?.let {
                it.prepareToDraw()
                translate(offset.x.toFloat(), offset.y.toFloat()) {
                    drawImage(it)
                }
            }
            drawContent()
        }
    }

    private data class BackgroundImageElement(
        val url: String,
        val alignment: Alignment,
        val scale: ContentScale,
        val coroutineScope: CoroutineScope,
        val context: Context,
        val imageLoader: ImageLoader,
    ) : ModifierNodeElement<BackgroundImageNode>() {
        private var bitmap: Bitmap? = null
        private val node = BackgroundImageNode(bitmap, alignment, scale)

        override fun InspectorInfo.inspectableProperties() {
            "url" to url
            "alignment" to alignment
            "scale" to scale
        }

        override fun create(): BackgroundImageNode {
            coroutineScope.launch {
                bitmap = getImage(context)
                update(node)
                node.invalidateDraw()
            }
            return node
        }

        override fun update(node: BackgroundImageNode) {
            node.bitmap = bitmap
            node.alignment = alignment
            node.scale = scale
        }

        private suspend fun getImage(context: Context): Bitmap? {
            val request = ImageRequest.Builder(context)
                .allowHardware(false)
                .data(url)
                .build()
            return imageLoader.execute(request).drawable?.toBitmap()
        }
    }

    @SuppressLint("ComposeModifierComposed")
    private fun Modifier.multiDimensionalBorder(
        strokeWidths: List<Float>,
        color: Color,
        cornerRadiusDp: Dp,
        strokeStyle: BorderStyleUiModel,
        shape: Shape,
    ): Modifier {
        if (strokeWidths.size != 4) {
            return clip(shape).then(Modifier.border(0.dp, color, shape))
        }
        // If all widths are the same use default borders
        if (strokeWidths.all { it == strokeWidths[0] }) {
            val strokeWidth = if (strokeWidths[0] != 0f) strokeWidths[0].dp else Dp.Unspecified
            return when (strokeStyle) {
                BorderStyleUiModel.Dashed -> clip(shape).then(Modifier.dashedBorder(strokeWidth, color, cornerRadiusDp))
                else -> clip(shape).then(Modifier.border(strokeWidth, color, shape))
            }
        }
        return clip(shape).then(
            composed(
                factory = {
                    val density = LocalDensity.current
                    val strokeWidthsPx = strokeWidths.map { density.run { it.dp.toPx() } }
                    val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }
                    val pathEffect = when (strokeStyle) {
                        BorderStyleUiModel.Dashed -> PathEffect.dashPathEffect(
                            floatArrayOf(
                                DASHED_WIDTH,
                                DASHED_SPACING,
                            ),
                            0f,
                        )

                        else -> null
                    }

                    this.then(
                        Modifier.drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                val width = size.width
                                val height = size.height

                                drawLine(
                                    color = if (strokeWidthsPx[0] > 0) color else Color.Transparent,
                                    start = Offset(
                                        x = cornerRadiusPx + (strokeWidthsPx[3] / 2),
                                        y = (strokeWidthsPx[0] / 2),
                                    ),
                                    end = Offset(
                                        x = width - cornerRadiusPx - (strokeWidthsPx[1] / 2),
                                        y = (strokeWidthsPx[0] / 2),
                                    ),
                                    strokeWidth = strokeWidthsPx[0],
                                    pathEffect = pathEffect,
                                )

                                drawArc(
                                    color = color,
                                    startAngle = 270f,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    topLeft = Offset(
                                        x = width - (cornerRadiusPx * 2) - (strokeWidthsPx[1] / 2),
                                        y = (strokeWidthsPx[0] / 2),
                                    ),
                                    size = Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
                                    style = Stroke(
                                        width = min(strokeWidthsPx[0], strokeWidthsPx[1]),
                                        pathEffect = pathEffect,
                                    ),
                                )

                                drawLine(
                                    color = if (strokeWidthsPx[1] > 0) color else Color.Transparent,
                                    start = Offset(x = width - (strokeWidthsPx[1] / 2), y = cornerRadiusPx),
                                    end = Offset(x = width - (strokeWidthsPx[1] / 2), y = height - cornerRadiusPx),
                                    strokeWidth = strokeWidthsPx[1],
                                    pathEffect = pathEffect,
                                )

                                drawArc(
                                    color = color,
                                    startAngle = 0f,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    topLeft = Offset(
                                        x = width - (cornerRadiusPx * 2) - (strokeWidthsPx[1] / 2),
                                        y = height - (cornerRadiusPx * 2) - (strokeWidthsPx[2] / 2),
                                    ),
                                    size = Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
                                    style = Stroke(
                                        width = min(strokeWidthsPx[1], strokeWidthsPx[2]),
                                        pathEffect = pathEffect,
                                    ),
                                )

                                drawLine(
                                    color = if (strokeWidthsPx[2] > 0) color else Color.Transparent,
                                    start = Offset(
                                        x = width - cornerRadiusPx - (strokeWidthsPx[1] / 2),
                                        y = height - (strokeWidthsPx[2] / 2),
                                    ),
                                    end = Offset(
                                        x = cornerRadiusPx + (strokeWidthsPx[3] / 2),
                                        y = height - (strokeWidthsPx[2] / 2),
                                    ),
                                    strokeWidth = strokeWidthsPx[2],
                                    pathEffect = pathEffect,
                                )

                                drawArc(
                                    color = color,
                                    startAngle = 90f,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    topLeft = Offset(
                                        x = (strokeWidthsPx[3] / 2),
                                        y = height - (cornerRadiusPx * 2) - (strokeWidthsPx[2] / 2),
                                    ),
                                    size = Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
                                    style = Stroke(
                                        width = min(strokeWidthsPx[2], strokeWidthsPx[3]),
                                        pathEffect = pathEffect,
                                    ),
                                )

                                drawLine(
                                    color = if (strokeWidthsPx[3] > 0) color else Color.Transparent,
                                    start = Offset(x = strokeWidthsPx[3] / 2, y = height - cornerRadiusPx),
                                    end = Offset(x = strokeWidthsPx[3] / 2, y = cornerRadiusPx),
                                    strokeWidth = strokeWidthsPx[3],
                                    pathEffect = pathEffect,
                                )

                                drawArc(
                                    color = color,
                                    startAngle = 180F,
                                    sweepAngle = 90F,
                                    useCenter = false,
                                    topLeft = Offset(x = (strokeWidthsPx[3] / 2), y = (strokeWidthsPx[0] / 2)),
                                    size = Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
                                    style = Stroke(
                                        width = min(strokeWidthsPx[3], strokeWidthsPx[0]),
                                        pathEffect = pathEffect,
                                    ),
                                )
                            }
                        },
                    )
                },
            ),
        )
    }

    @SuppressLint("ComposeModifierComposed")
    private fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadiusDp: Dp) = composed(
        factory = {
            val density = LocalDensity.current
            val strokeWidthPx = density.run { strokeWidth.toPx() }
            val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }

            this.then(
                Modifier.drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        val stroke = Stroke(
                            width = strokeWidthPx,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(
                                    DASHED_WIDTH,
                                    DASHED_SPACING,
                                ),
                                0f,
                            ),
                        )

                        drawRoundRect(
                            color = color,
                            style = stroke,
                            cornerRadius = CornerRadius(cornerRadiusPx),
                        )
                    }
                },
            )
        },
    )

    private fun getUiThemeColor(themeColor: ThemeColorUiModel?, isDarkMode: Boolean): Color = themeColor?.run {
        if (isDarkMode && dark != null) {
            dark?.color ?: Color.Unspecified
        } else {
            light?.color ?: Color.Unspecified
        }
    } ?: Color.Unspecified

    private fun getUiThemeUrl(themeColor: ThemeColorUiModel?, isDarkMode: Boolean): String = themeColor?.run {
        if (isDarkMode && dark != null) {
            dark
        } else {
            light
        }
    } ?: ""

    private val String.color
        get() = try {
            Color(parseColor(this))
        } catch (e: RuntimeException) {
            Color.Unspecified
        }

    private fun parseColor(hexColor: String): Int {
        if (hexColor.isEmpty() || hexColor[0] != '#') {
            throw IllegalArgumentException("Invalid hex color format: $hexColor")
        }

        val hex = hexColor.substring(1)
        val normalizedHex = when (hex.length) {
            3 -> hex.map { "$it$it" }.joinToString(separator = "", prefix = "ff") // RGB -> AARRGGBB
            4 -> hex.map { "$it$it" }.joinToString(separator = "") // ARGB -> AARRGGBB
            6 -> "ff$hex" // RRGGBB -> AARRGGBB
            8 -> hex // AARRGGBB (already correct)
            else -> throw IllegalArgumentException("Invalid hex color length: ${hex.length}")
        }

        return normalizedHex.toLong(16).toInt()
    }

    private fun blurRadiusToAlpha(radius: Float): Float {
        // Arbitrary decay function
        return 1 * (1 - 0.25).pow(radius.toDouble()).toFloat()
    }

    @SuppressLint("ComposeUnstableReceiver")
    @Composable
    fun createContainerUiProperties(
        containerProperties: ImmutableList<StateBlock<ContainerProperties>>?,
        index: Int,
        isPressed: Boolean,
        baseProperties: ImmutableList<StateBlock<ContainerProperties>>? = null,
    ): ContainerUiProperties = remember(containerProperties, index, isPressed, baseProperties) {
        var arrangementBias: Float? = null
        var alignmentBias: Float? = null
        var gap: Float? = null
        var weight: Float? = null
        var selfAlignmentBias: Float? = null

        fun applyPropertiesFromPropertiesList(propertiesList: ImmutableList<StateBlock<ContainerProperties>>?) {
            if (propertiesList != null) {
                for (iteration in 0..1) {
                    val usePressed = isPressed && iteration == 0
                    for (i in index downTo 0) {
                        val stateBlock = propertiesList.getOrNull(i)
                        val properties = if (usePressed) stateBlock?.pressed else stateBlock?.default
                        properties?.arrangementUiModel?.bias?.let {
                            if (arrangementBias == null) arrangementBias = it
                        }
                        properties?.gap?.let {
                            if (gap == null) gap = it
                        }
                        properties?.alignmentUiModel?.let {
                            if (alignmentBias == null) alignmentBias = it.bias
                        }
                        properties?.weight?.let {
                            if (weight == null) weight = it
                        }
                        properties?.alignSelfVertical?.let {
                            if (selfAlignmentBias == null) selfAlignmentBias = it.bias
                        }
                        if (arrangementBias != null &&
                            gap != null &&
                            alignmentBias != null &&
                            weight != null &&
                            selfAlignmentBias != null
                        ) {
                            break
                        }
                    }
                    if (!usePressed ||
                        (
                            arrangementBias != null &&
                                gap != null &&
                                alignmentBias != null &&
                                weight != null &&
                                selfAlignmentBias != null
                            )
                    ) {
                        break
                    }
                }
            }
        }

        applyPropertiesFromPropertiesList(containerProperties)
        applyPropertiesFromPropertiesList(baseProperties)

        val verticalArrangement = when {
            gap != null -> Arrangement.spacedBy(
                gap?.toInt()?.dp ?: 0.dp,
                BiasAlignment.Vertical(arrangementBias ?: ArrangementUiModel.Start.bias),
            )

            arrangementBias == ArrangementUiModel.End.bias -> Arrangement.Bottom
            arrangementBias == ArrangementUiModel.Center.bias -> Arrangement.Center
            else -> Arrangement.Top
        }
        val horizontalArrangement = when {
            gap != null -> Arrangement.spacedBy(
                gap?.toInt()?.dp ?: 0.dp,
                BiasAlignment.Horizontal(arrangementBias ?: ArrangementUiModel.Start.bias),
            )

            arrangementBias == ArrangementUiModel.End.bias -> Arrangement.End
            arrangementBias == ArrangementUiModel.Center.bias -> Arrangement.Center
            else -> Arrangement.Start
        }
        ContainerUiProperties(
            alignmentBias = alignmentBias ?: -1F,
            arrangementBias = arrangementBias ?: -1F,
            verticalArrangement = verticalArrangement,
            horizontalArrangement = horizontalArrangement,
            selfAlignmentBias = selfAlignmentBias,
            weight = weight,
            gap = gap?.dp,
        )
    }

    @SuppressLint("ComposeUnstableReceiver")
    @Composable
    fun createBackground(
        modifierProperties: ImmutableList<StateBlock<ModifierProperties>>?,
        index: Int,
        isPressed: Boolean,
        isDarkModeEnabled: Boolean,
    ): Color? = remember(modifierProperties, index, isPressed) {
        var backgroundColor: ThemeColorUiModel? = null
        if (modifierProperties != null) {
            for (iteration in 0..1) {
                val usePressed = isPressed && iteration == 0
                for (i in index downTo 0) {
                    val stateBlock = modifierProperties.getOrNull(i)
                    val properties = if (usePressed) stateBlock?.pressed else stateBlock?.default
                    properties?.backgroundColor?.let {
                        backgroundColor = it
                    }
                    if (backgroundColor != null) break
                }
                if (!usePressed || (backgroundColor != null)) break
            }
        }
        backgroundColor?.let { getUiThemeColor(it, isDarkModeEnabled) }
    }

    @SuppressLint("ComposeUnstableReceiver")
    @Composable
    fun createTextStyle(
        text: String,
        textStyles: ImmutableList<StateBlock<TextStylingUiProperties>>?,
        breakpointIndex: Int,
        isPressed: Boolean,
        isDarkModeEnabled: Boolean,
        offerState: OfferUiState,
        fontFamilyMap: ImmutableMap<String, FontFamily> = LocalFontFamilyProvider.current,
        defaultFontFamily: String? = null,
        conditionalTransitionTextStyling: ConditionalTransitionTextStyling? = null,
        baseStyles: ImmutableList<StateBlock<TextStylingUiProperties>>? = null,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ): TextStyleUiState {
        val transitionStyleState = evaluateState(
            predicates = conditionalTransitionTextStyling?.predicates,
            breakpointIndex = breakpointIndex,
            isDarkModeEnabled = isDarkModeEnabled,
            offerState = offerState,
        )
        val resolver = LocalFontFamilyResolver.current
        return if (transitionStyleState != null && conditionalTransitionTextStyling != null) {
            val transitionData = updateTextStyleTransitionData(
                textStyles = textStyles,
                breakpointIndex = breakpointIndex,
                isPressed = isPressed,
                isDarkModeEnabled = isDarkModeEnabled,
                transitionTextStyling = conditionalTransitionTextStyling.textStyles,
                transitionDuration = conditionalTransitionTextStyling.duration,
                transitionStyleState = transitionStyleState,
            )
            applyTextProperties(
                transitionData,
                text,
                isDarkModeEnabled,
                fontFamilyMap,
                defaultFontFamily,
                resolver,
                onEventSent,
            )
        } else {
            remember(text, textStyles, breakpointIndex, isPressed, isDarkModeEnabled, baseStyles) {
                val stylingUiProperties = createTextProperties(
                    isPressed,
                    breakpointIndex,
                    textStyles,
                    baseStyles,
                )
                applyTextProperties(
                    stylingUiProperties,
                    text,
                    isDarkModeEnabled,
                    fontFamilyMap,
                    defaultFontFamily,
                    resolver,
                    onEventSent,
                )
            }
        }
    }

    fun createBackgroundShape(properties: BaseModifierProperties): Shape = properties.borderRadius?.let {
        if (it > 0.dp) {
            if (properties.borderUseTopCornerRadius == true) {
                RoundedCornerShape(topStart = it, topEnd = it)
            } else {
                RoundedCornerShape(it)
            }
        } else {
            RectangleShape
        }
    } ?: RectangleShape

    private fun createTextProperties(
        isPressed: Boolean,
        breakpointIndex: Int,
        textStyles: ImmutableList<StateBlock<TextStylingUiProperties>>?,
        baseStyles: ImmutableList<StateBlock<TextStylingUiProperties>>? = null,
    ): TextStylingUiProperties {
        var stylingUiProperties = TextStylingUiProperties()
        fun applyStylesFromStylesList(styles: ImmutableList<StateBlock<TextStylingUiProperties>>?) {
            for (iteration in 0..1) {
                val usePressed = isPressed && iteration == 0
                for (i in breakpointIndex downTo 0) {
                    val stateBlock = styles?.getOrNull(i)
                    val properties = if (usePressed) stateBlock?.pressed else stateBlock?.default
                    properties?.let {
                        stylingUiProperties = stylingUiProperties.applyProperties(it)
                    }
                }
                if (!usePressed) break
            }
        }

        applyStylesFromStylesList(textStyles)
        applyStylesFromStylesList(baseStyles)
        return stylingUiProperties
    }

    private fun applyTextProperties(
        stylingUiProperties: BaseTextStylingUiProperties,
        text: String,
        isDarkModeEnabled: Boolean,
        fontFamilyMap: ImmutableMap<String, FontFamily>,
        defaultFontFamily: String?,
        resolver: FontFamily.Resolver,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ): TextStyleUiState {
        val transformedText = stylingUiProperties.textTransform?.run {
            when (this) {
                TextUiTransform.Capitalize -> text.split(" ").joinToString(" ") { value ->
                    value.replaceFirstChar {
                        if (it.isLowerCase()) {
                            it.titlecase(
                                Locale.getDefault(),
                            )
                        } else {
                            it.toString()
                        }
                    }
                }

                TextUiTransform.Uppercase -> text.uppercase()
                TextUiTransform.Lowercase -> text.lowercase()
                TextUiTransform.None -> text
            }
        } ?: text
        val fontFamily = try {
            val family = fontFamilyMap[stylingUiProperties.fontFamily ?: defaultFontFamily]
            if (family == null && stylingUiProperties.fontFamily != null) {
                throw IllegalStateException("Could not load font ${stylingUiProperties.fontFamily}")
            }
            resolver.resolve(
                family,
                stylingUiProperties.fontWeight?.let { FontWeight(it) } ?: FontWeight.Normal,
                stylingUiProperties.fontStyle ?: FontStyle.Normal,
            )
            fontFamilyMap[stylingUiProperties.fontFamily ?: defaultFontFamily] ?: FontFamily.Default
        } catch (e: Exception) {
            onEventSent(LayoutContract.LayoutEvent.UiException(e))
            FontFamily.Default
        }
        return TextStyleUiState(
            TextStyle(
                color = stylingUiProperties.textColorState ?: getUiThemeColor(
                    stylingUiProperties.textColor,
                    isDarkModeEnabled,
                ),
                fontSize = stylingUiProperties.fontSize?.takeIf { it != Float.MIN_VALUE }?.sp
                    ?: TextUnit.Unspecified,
                fontFamily = fontFamily,
                fontWeight = stylingUiProperties.fontWeight?.let { FontWeight(it) },
                fontStyle = stylingUiProperties.fontStyle,
                textAlign = stylingUiProperties.horizontalTextAlign ?: TextAlign.Start,
                baselineShift = stylingUiProperties.baselineTextAlign,
                letterSpacing = stylingUiProperties.letterSpacing?.takeIf { it != Float.MIN_VALUE }?.sp
                    ?: TextUnit.Unspecified,
                textDecoration = stylingUiProperties.textDecoration,
                lineHeight = stylingUiProperties.lineHeight?.takeIf { it != Float.MIN_VALUE }?.sp
                    ?: TextUnit.Unspecified,
            ),
            transformedText,
            stylingUiProperties.textTransform ?: TextUiTransform.None,
            stylingUiProperties.lineLimit ?: Int.MAX_VALUE,
        )
    }

    private fun TextStylingUiProperties.applyProperties(properties: TextStylingUiProperties): TextStylingUiProperties =
        copy(
            textColor = textColor ?: properties.textColor,
            fontSize = fontSize ?: properties.fontSize,
            fontWeight = fontWeight ?: properties.fontWeight,
            fontStyle = fontStyle ?: properties.fontStyle,
            fontFamily = fontFamily ?: properties.fontFamily,
            letterSpacing = letterSpacing ?: properties.letterSpacing,
            lineHeight = lineHeight ?: properties.lineHeight,
            horizontalTextAlign = horizontalTextAlign ?: properties.horizontalTextAlign,
            baselineTextAlign = baselineTextAlign ?: properties.baselineTextAlign,
            textTransform = textTransform ?: properties.textTransform,
            textDecoration = textDecoration ?: properties.textDecoration,
            lineLimit = lineLimit ?: properties.lineLimit,
        )

    private val paddingToVector: TwoWayConverter<PaddingValues?, AnimationVector4D> =
        TwoWayConverter(
            convertToVector = {
                AnimationVector4D(
                    it?.calculateTopPadding()?.value ?: 0F,
                    it?.calculateTopPadding()?.value ?: 0F,
                    it?.calculateTopPadding()?.value ?: 0F,
                    it?.calculateTopPadding()?.value ?: 0F,
                )
            },
            convertFromVector = {
                if (it.v1.dp == 0.dp && it.v2.dp == 0.dp && it.v3.dp == 0.dp && it.v4.dp == 0.dp) {
                    null
                } else {
                    PaddingValues(it.v1.dp, it.v2.dp, it.v3.dp, it.v4.dp)
                }
            },
        )

    private val heightToVectorConverter: TwoWayConverter<HeightUiModel?, AnimationVector1D> =
        TwoWayConverter(
            convertToVector = {
                when (it) {
                    is HeightUiModel.Fixed -> AnimationVector(it.value)
                    is HeightUiModel.Percentage -> AnimationVector(it.value)
                    HeightUiModel.MatchParent -> AnimationVector(MATCH_PARENT)
                    HeightUiModel.WrapContent -> AnimationVector(WRAP_CONTENT)
                    null -> AnimationVector(0F)
                }
            },
            convertFromVector = {
                when (it.value) {
                    0F -> null
                    MATCH_PARENT -> HeightUiModel.MatchParent
                    WRAP_CONTENT -> HeightUiModel.WrapContent
                    else -> if (it.value > 1f) HeightUiModel.Fixed(it.value) else HeightUiModel.Percentage(it.value)
                }
            },
        )

    private val widthToVectorConverter: TwoWayConverter<WidthUiModel?, AnimationVector1D> =
        TwoWayConverter(
            convertToVector = {
                when (it) {
                    is WidthUiModel.Fixed -> AnimationVector(it.value)
                    is WidthUiModel.Percentage -> AnimationVector(it.value)
                    WidthUiModel.MatchParent -> AnimationVector(MATCH_PARENT)
                    WidthUiModel.WrapContent -> AnimationVector(WRAP_CONTENT)
                    null -> AnimationVector(0F)
                }
            },
            convertFromVector = {
                when (it.value) {
                    0F -> null
                    MATCH_PARENT -> WidthUiModel.MatchParent
                    WRAP_CONTENT -> WidthUiModel.WrapContent
                    else -> if (it.value > 1f) WidthUiModel.Fixed(it.value) else WidthUiModel.Percentage(it.value)
                }
            },
        )

    @Composable
    fun updateTextStyleTransitionData(
        textStyles: ImmutableList<StateBlock<TextStylingUiProperties>>?,
        breakpointIndex: Int,
        isPressed: Boolean,
        isDarkModeEnabled: Boolean,
        transitionTextStyling: TextStylingUiProperties,
        transitionDuration: Int,
        transitionStyleState: ConditionalStyleState,
    ): TransitionTextStylingUiProperties {
        val textStylesProperty =
            createTextProperties(isPressed, breakpointIndex, textStyles)
        val transition = updateTransition(transitionStyleState, label = "transitionStyle state")

        val textColorState = transition.animateColor({ tween(transitionDuration) }, label = "color") { state ->
            when (state) {
                ConditionalStyleState.Normal -> textStylesProperty.textColor?.let {
                    getUiThemeColor(
                        it,
                        isDarkModeEnabled,
                    )
                }

                ConditionalStyleState.Transition ->
                    transitionTextStyling.textColor?.let {
                        getUiThemeColor(
                            it,
                            isDarkModeEnabled,
                        )
                    } ?: textStylesProperty.textColor?.let {
                        getUiThemeColor(
                            it,
                            isDarkModeEnabled,
                        )
                    }
            } ?: Color.Unspecified
        }

        val fontWeight = transition.animateInt({ tween(transitionDuration) }, label = "fontWeight") { state ->
            when (state) {
                ConditionalStyleState.Normal -> textStylesProperty.fontWeight
                ConditionalStyleState.Transition -> transitionTextStyling.fontWeight ?: textStylesProperty.fontWeight
            } ?: FontWeight.Normal.weight
        }

        val fontSize = transition.animateFloat({ tween(transitionDuration) }, label = "fontSize") { state ->
            when (state) {
                ConditionalStyleState.Normal -> textStylesProperty.fontSize
                ConditionalStyleState.Transition -> transitionTextStyling.fontSize ?: textStylesProperty.fontSize
            } ?: Float.MIN_VALUE
        }

        val lineHeight = transition.animateFloat({ tween(transitionDuration) }, label = "lineHeight") { state ->
            when (state) {
                ConditionalStyleState.Normal -> textStylesProperty.lineHeight
                ConditionalStyleState.Transition -> transitionTextStyling.lineHeight ?: textStylesProperty.lineHeight
            } ?: Float.MIN_VALUE
        }

        val letterSpacing = transition.animateFloat({ tween(transitionDuration) }, label = "letterSpacing") { state ->
            when (state) {
                ConditionalStyleState.Normal -> textStylesProperty.letterSpacing
                ConditionalStyleState.Transition ->
                    transitionTextStyling.letterSpacing
                        ?: textStylesProperty.letterSpacing
            } ?: Float.MIN_VALUE
        }

        val lineLimit = transition.animateInt({ tween(transitionDuration) }, label = "lineLimit") { state ->
            when (state) {
                ConditionalStyleState.Normal -> textStylesProperty.lineLimit
                ConditionalStyleState.Transition -> transitionTextStyling.lineLimit ?: textStylesProperty.lineLimit
            } ?: Int.MAX_VALUE
        }

        val textColor = when (transitionStyleState) {
            ConditionalStyleState.Normal -> textStylesProperty.textColor
            ConditionalStyleState.Transition -> transitionTextStyling.textColor ?: textStylesProperty.textColor
        }
        val fontFamily = when (transitionStyleState) {
            ConditionalStyleState.Normal -> textStylesProperty.fontFamily
            ConditionalStyleState.Transition -> transitionTextStyling.fontFamily ?: textStylesProperty.fontFamily
        }

        val textTransform = when (transitionStyleState) {
            ConditionalStyleState.Normal -> textStylesProperty.textTransform
            ConditionalStyleState.Transition -> transitionTextStyling.textTransform ?: textStylesProperty.textTransform
        }

        val horizontalTextAlign =
            transition.animateValue(
                textAlignToVectorConverter,
                { tween(transitionDuration) },
                label = "textAlign",
            ) { state ->
                when (state) {
                    ConditionalStyleState.Normal -> textStylesProperty.horizontalTextAlign
                    ConditionalStyleState.Transition ->
                        transitionTextStyling.horizontalTextAlign
                            ?: textStylesProperty.horizontalTextAlign
                } ?: TextAlign.Start
            }

        val baselineTextAlign =
            transition.animateValue(
                baselineShiftToVectorConverter,
                { tween(transitionDuration) },
                label = "baselineTextAlign",
            ) { state ->
                when (state) {
                    ConditionalStyleState.Normal -> textStylesProperty.baselineTextAlign
                    ConditionalStyleState.Transition ->
                        transitionTextStyling.baselineTextAlign
                            ?: textStylesProperty.baselineTextAlign
                } ?: BaselineShift.None
            }

        val fontStyle =
            transition.animateValue(
                fontStyleToVectorConverter,
                { tween(transitionDuration) },
                label = "fontStyle",
            ) { state ->
                when (state) {
                    ConditionalStyleState.Normal -> textStylesProperty.fontStyle
                    ConditionalStyleState.Transition ->
                        transitionTextStyling.fontStyle
                            ?: textStylesProperty.fontStyle
                } ?: FontStyle.Normal
            }

        val textDecoration =
            transition.animateValue(
                textDecorationToVectorConverter,
                { tween(transitionDuration) },
                label = "textDecoration",
            ) { state ->
                when (state) {
                    ConditionalStyleState.Normal -> textStylesProperty.textDecoration
                    ConditionalStyleState.Transition ->
                        transitionTextStyling.textDecoration
                            ?: textStylesProperty.textDecoration
                } ?: TextDecoration.None
            }

        return remember(transition) {
            TransitionTextStylingUiProperties(
                textColor = textColor,
                fontFamily = fontFamily,
                textTransform = textTransform,
                textColorState = textColorState,
                fontWeight = fontWeight,
                fontSize = fontSize,
                lineHeight = lineHeight,
                horizontalTextAlign = horizontalTextAlign,
                baselineTextAlign = baselineTextAlign,
                fontStyle = fontStyle,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                lineLimit = lineLimit,
            )
        }
    }

    private val textAlignToVectorConverter: TwoWayConverter<TextAlign, AnimationVector1D> =
        TwoWayConverter(
            convertToVector = {
                when (it) {
                    TextAlign.Left, TextAlign.Start -> AnimationVector1D(1F)
                    TextAlign.Right, TextAlign.End -> AnimationVector1D(3F)
                    TextAlign.Center -> AnimationVector(2F)
                    else -> AnimationVector(1F)
                }
            },
            convertFromVector = {
                when (it.value) {
                    1F -> TextAlign.Start
                    2F -> TextAlign.Center
                    3F -> TextAlign.End
                    else -> TextAlign.Start
                }
            },
        )

    private val fontStyleToVectorConverter: TwoWayConverter<FontStyle, AnimationVector1D> =
        TwoWayConverter(
            convertToVector = {
                when (it) {
                    FontStyle.Normal -> AnimationVector1D(0F)
                    else -> AnimationVector(1F)
                }
            },
            convertFromVector = {
                when (it.value) {
                    0F -> FontStyle.Normal
                    else -> FontStyle.Italic
                }
            },
        )

    private val textDecorationToVectorConverter: TwoWayConverter<TextDecoration, AnimationVector1D> =
        TwoWayConverter(
            convertToVector = {
                when (it) {
                    TextDecoration.None -> AnimationVector1D(0F)
                    TextDecoration.Underline -> AnimationVector(1F)
                    else -> AnimationVector(-1F)
                }
            },
            convertFromVector = {
                when (it.value) {
                    0F -> TextDecoration.None
                    1F -> TextDecoration.Underline
                    else -> TextDecoration.LineThrough
                }
            },
        )

    private val baselineShiftToVectorConverter: TwoWayConverter<BaselineShift, AnimationVector1D> =
        TwoWayConverter(
            convertToVector = {
                when (it) {
                    BaselineShift.None -> AnimationVector1D(0F)
                    BaselineShift.Superscript -> AnimationVector(0.5F)
                    else -> AnimationVector(-0.5F)
                }
            },
            convertFromVector = {
                when (it.value) {
                    0F -> BaselineShift.None
                    0.5F -> BaselineShift.Superscript
                    else -> BaselineShift.Subscript
                }
            },
        )

    companion object {
        private const val DASHED_WIDTH = 10f
        private const val DASHED_SPACING = 10f
        private const val WRAP_CONTENT = -2f
        private const val MATCH_PARENT = -1f
    }
}
