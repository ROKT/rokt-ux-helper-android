package com.rokt.roktux.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints

/**
 * Renders [content], falling back to [fallback] if composing it throws.
 *
 * A composable call can't sit inside an ordinary try/catch (the Compose compiler rejects it), so
 * this instead subcomposes [content] inside [SubcomposeLayout]: `subcompose()` is a plain function
 * call made from the (non-composable) measure lambda, so a composition-time exception from it can
 * be caught normally. This also means the exception is caught wherever [content] actually runs —
 * unlike a check performed before entering [content], which can be bypassed if [content] composes
 * somewhere with different composition-local values, e.g. inside a `Popup`/`Dialog`.
 *
 * Motivating case: `OneByOneDistributionComponent`/`GroupedDistributionComponent` wrap `NavHost`
 * in this because navigation-compose 2.10+ requires a `NavigationEventDispatcher` from the host
 * Activity (androidx.activity 1.12+), which is outside roktux's control — and `NavHost` can throw
 * even when the host Activity does provide one, if it composes inside a `Popup`/`Dialog` (where
 * `LocalNavigationEventDispatcherOwner` doesn't propagate).
 *
 * [modifier] is applied to whichever of [content]/[fallback] is actually showing — the outer
 * [SubcomposeLayout] on success, a plain [Box] on fallback — rather than left for each to apply to
 * itself, so a `weight`/`align` ParentDataModifier set by a `Row`/`Column` parent always lands on
 * this composable's own direct child (as Row/Column requires) rather than one layer deeper inside
 * [content]/[fallback]'s own tree, where the parent can no longer see it.
 */
@Composable
internal fun ComposeErrorBoundary(
    modifier: Modifier = Modifier,
    onError: (Exception) -> Unit = {},
    fallback: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    var failure by remember { mutableStateOf<Exception?>(null) }
    val currentFailure = failure
    if (currentFailure != null) {
        // Reported once, from a proper effect rather than the measure pass that caught it —
        // measurement can run speculatively/more than once and shouldn't trigger side effects.
        LaunchedEffect(currentFailure) { onError(currentFailure) }
        Box(modifier) { fallback() }
        return
    }
    SubcomposeLayout(modifier.then(NoIntrinsicsModifier)) { constraints ->
        val placeables = try {
            subcompose(Unit, content).map { it.measure(constraints) }
        } catch (e: Exception) {
            failure = e
            return@SubcomposeLayout layout(0, 0) {}
        }
        val width = placeables.maxOfOrNull { it.width } ?: 0
        val height = placeables.maxOfOrNull { it.height } ?: 0
        layout(width, height) {
            placeables.forEach { it.place(0, 0) }
        }
    }
}

/**
 * [SubcomposeLayout] can't answer an intrinsic-size query: unlike a plain `Layout`, it doesn't
 * compose its content until measurement, so it has nothing to report and throws instead. A
 * `Row`/`Column` asks every child for its intrinsic size when *any* child (including a sibling)
 * uses `Stretch` alignment, so without overriding this, that query crashes before
 * [ComposeErrorBoundary]'s own try/catch ever runs. Reporting a fixed 0 opts this node out of
 * contributing to that computation — it still measures and renders normally afterward with
 * whatever size the query settles on — rather than the reverse of what's actually needed here:
 * catching a crash, not causing a new one.
 */
private val NoIntrinsicsModifier: Modifier = object : LayoutModifier {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int) = 0
    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int) = 0
    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int) = 0
    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int) = 0
}
