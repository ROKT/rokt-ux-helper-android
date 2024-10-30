package com.rokt.roktux.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.core.content.ContextCompat
import androidx.window.layout.WindowMetricsCalculator
import com.rokt.modelmapper.data.BindData
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.utils.CURRENT_POSITION_PLACEHOLDER
import com.rokt.modelmapper.utils.TOTAL_OFFERS_PLACEHOLDER
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.getOrElse
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.whileSelect
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.min

internal inline fun <reified T> Any?.tryCast(block: T.() -> Unit) {
    if (this is T) {
        block()
    }
}

internal fun String.replaceStates(currentOfferIndex: Int, lastOfferIndex: Int, viewableItems: Int): String {
    val currentProgression = ceil((currentOfferIndex + 1).toDouble() / viewableItems).toInt()
    val totalOffers = ceil((lastOfferIndex + 1).toDouble() / viewableItems).toInt()
    val supportedStates = mapOf(
        CURRENT_POSITION_PLACEHOLDER to
            min(currentProgression, totalOffers).toString(),
        TOTAL_OFFERS_PLACEHOLDER to totalOffers.toString(),
    )
    var replacedData = this
    supportedStates.forEach { (placeholder, value) ->
        replacedData = replacedData.replace(placeholder, value)
    }
    return replacedData
}

internal fun String.stripNonAscii(): String = filter { it.code in 0..127 }

internal fun String.safeCapitalize(): String = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString()
}

internal fun BindData.getValue(offerState: OfferUiState, viewableItems: Int): String? {
    return when (this) {
        is BindData.Value -> this.text.replaceStates(
            offerState.currentOfferIndex,
            offerState.lastOfferIndex,
            viewableItems,
        ) // TODO: Viewable items
        is BindData.State -> (offerState.currentOfferIndex + 1).toString()
        else -> null
    }
}

internal fun Context.getDeviceLocale(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales[0].toString()
    } else {
        @Suppress("DEPRECATION")
        resources.configuration.locale.toString()
    }
}

internal fun Context.getPackageVersion(): String {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionName.stripNonAscii()
    } catch (e: Exception) {
        ""
    }
}

/*
   "breakpoints": {
     "tablet": 512,
     "desktop": 640
    }
    width 0 to 511 Active breakpoint is default, so index to select is 0
    width 512 to 639 Active breakpoint is tablet, so index to select is 1
    width 640 and above Active breakpoint is desktop, so index to select is 2
 */

@Composable
internal fun Activity.calculateBreakpoint(
    breakpoints: ImmutableMap<String, Int>,
    density: Density = LocalDensity.current,
): Int = remember(breakpoints, LocalConfiguration.current) {
    val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
    val width = with(density) { metrics.bounds.toComposeRect().size.width.toDp().value.toInt() }
    getBreakpointIndex(width, breakpoints)
}

@Composable
internal fun Activity.getScreenHeightInPixels(): Int = remember(LocalConfiguration.current) {
    val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
    metrics.bounds.toComposeRect().size.height.toInt()
}

internal fun getBreakpointIndex(width: Int, breakpoints: ImmutableMap<String, Int>): Int {
    if (breakpoints.size == 1) return 0
    val sortedBreakpoints = breakpoints.toList().sortedBy { it.second }
    return (sortedBreakpoints.indexOfFirst { width < it.second }.takeIf { it != -1 } ?: sortedBreakpoints.size) - 1
}

internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Should be called in the context of an Activity")
}

@Composable
internal fun Modifier.fadeInOutAnimationModifier(
    animationState: AnimationState,
    duration: Int,
    onExit: () -> Unit,
): Modifier {
    val hideAlpha: Float by animateFloatAsState(
        targetValue = when (animationState) {
            AnimationState.Hide -> 0F
            AnimationState.Show -> 1F
        },
        animationSpec = tween(
            durationMillis = duration,
            easing = LinearEasing,
        ),
        finishedListener = {
            if (animationState == AnimationState.Hide) {
                onExit()
            }
        },
        label = "fadeInOut",
    )
    return this then Modifier.graphicsLayer(alpha = hideAlpha)
}

@Composable
internal fun Modifier.layoutExitAnimationModifier(
    model: LayoutSchemaUiModel,
    animationState: AnimationState,
    targetYOffset: Int,
    onExit: () -> Unit,
): Modifier {
    if (model.isEmbedded()) {
        if (animationState == AnimationState.Hide) {
            onExit()
        }
        return this
    }
    val offset by animateIntOffsetAsState(
        targetValue = when (animationState) {
            AnimationState.Hide -> IntOffset(0, targetYOffset)
            AnimationState.Show -> IntOffset.Zero
        },
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearEasing,
        ),
        finishedListener = {
            if (animationState == AnimationState.Hide) {
                onExit()
            }
        },
        label = "slideOutDownwards",
    )
    return this then Modifier.offset { offset }
}

internal fun Modifier.userInteractionDetector(model: LayoutSchemaUiModel, onTap: (() -> Unit)?): Modifier {
    // We apply this logic directly in BottomSheet as the pointerInput modifier is incompatible with ModalBottomSheet
    return if (model.isBottomSheet()) {
        this
    } else {
        this then Modifier.pointerInput(Unit) {
            interceptTap {
                onTap?.invoke()
            }
        }
    }
}

internal suspend fun PointerInputScope.interceptTap(
    pass: PointerEventPass = PointerEventPass.Initial,
    onTap: (() -> Unit)? = null,
) = coroutineScope {
    if (onTap == null) return@coroutineScope

    awaitEachGesture {
        val down = awaitFirstDown(pass = pass)

        do {
            val event = awaitPointerEvent(pass)
            val change = event.changes[0]

            if (change.id == down.id && !change.pressed) {
                // Purposefully don't consume the change here so it can be used by descendants
                // change.consume()
                onTap()
            }
        } while (event.changes.any { it.id == down.id && it.pressed })
    }
}

internal fun Modifier.componentVisibilityChange(
    callback: (identifier: Int, visible: Boolean) -> Unit,
    identifier: Int,
): Modifier {
    return composed {
        val view = LocalView.current
        var visibility = false
        onGloballyPositioned { coordinates ->
            val currentVisibility = getVisibilityRatio(coordinates, view) >= COMPONENT_VISIBILITY_THRESHOLD_RATIO
            if (currentVisibility != visibility) {
                visibility = currentVisibility
                callback(identifier, visibility)
            }
        }
    }
}

private fun getVisibilityRatio(coordinates: LayoutCoordinates, view: View): Float {
    // Return 0 if view or component is not attached to the window
    if (!view.isAttachedToWindow || !coordinates.isAttached) return 0f
    // Get the total area required to show the component
    val totalArea = (coordinates.size.height * coordinates.size.width).toFloat()
    // Return 0 of the total area is 0
    if (totalArea == 0f) return 0f

    // Get the window Rect
    val windowRect = android.graphics.Rect()
    view.getWindowVisibleDisplayFrame(windowRect)

    // Get the component's bounds in the window and intersect it with the window rect to find the visible area
    val componentRect = coordinates.boundsInWindow()
    val intersectRect = windowRect.toComposeRect().intersect(componentRect)

    if (intersectRect.height <= 0 || intersectRect.width <= 0) return 0f
    // Calculate the area of visible rect and find the ratio of it with the total area
    val visibleArea = intersectRect.height * intersectRect.width
    return visibleArea / totalArea
}

private fun LayoutSchemaUiModel.isBottomSheet(): Boolean {
    return this is LayoutSchemaUiModel.BottomSheetUiModel
}

internal fun LayoutSchemaUiModel.isEmbedded(): Boolean {
    return (this !is LayoutSchemaUiModel.OverlayUiModel) && (this !is LayoutSchemaUiModel.BottomSheetUiModel)
}

internal enum class AnimationState {
    Hide,
    Show,
}

@SuppressLint("QueryPermissionsNeeded")
internal fun Context.openUrl(
    url: String,
    id: String,
    successCallback: (id: String) -> Unit,
    errorCallback: (e: Throwable) -> Unit,
) {
    try {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        ContextCompat.startActivity(this, browserIntent, null)
        successCallback.invoke(id)
    } catch (e: Exception) {
        errorCallback(e)
    }
}

internal fun <T> Flow<T>.chunk(intervalMs: Long, maxSize: Int): Flow<List<T>> = channelFlow {
    coroutineScope {
        val upstreamCollection = Job()
        val upstream = produce<T>(capacity = maxSize) {
            collect { element -> channel.send(element) }
            upstreamCollection.complete()
        }

        whileSelect {
            upstreamCollection.onJoin {
                val chunk = upstream.drainAll(maxSize = maxSize)
                if (chunk.isNotEmpty()) send(chunk)
                false
            }

            onTimeout(intervalMs) {
                val chunk = upstream.drainAll(maxSize = maxSize)
                if (chunk.isNotEmpty()) send(chunk)
                true
            }
        }
    }
}

private tailrec fun <T> ReceiveChannel<T>.drainAll(
    accumulator: MutableList<T> = mutableListOf(),
    maxSize: Int,
): List<T> = if (accumulator.size == maxSize) {
    accumulator
} else {
    val nextValue = tryReceive().getOrElse { error: Throwable? -> error?.let { throw (it) } ?: return accumulator }
    accumulator.add(nextValue)
    drainAll(accumulator, maxSize)
}

private const val COMPONENT_VISIBILITY_THRESHOLD_RATIO = 0.5f
