package com.rokt.roktux.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout

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
 */
@Composable
internal fun ComposeErrorBoundary(
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
        fallback()
        return
    }
    SubcomposeLayout(Modifier) { constraints ->
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
