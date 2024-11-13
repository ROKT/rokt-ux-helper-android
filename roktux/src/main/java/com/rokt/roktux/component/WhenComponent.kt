package com.rokt.roktux.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.rokt.modelmapper.uimodel.BooleanWhenUiCondition
import com.rokt.modelmapper.uimodel.EqualityWhenUiCondition
import com.rokt.modelmapper.uimodel.ExistenceWhenUiCondition
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.OrderableWhenUiCondition
import com.rokt.modelmapper.uimodel.WhenUiPredicate
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.ImmutableList

internal class WhenComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ComposableComponent<LayoutSchemaUiModel.WhenUiModel> {

    @Composable
    override fun Render(
        model: LayoutSchemaUiModel.WhenUiModel,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        val evaluationResult = evaluatePredicates(
            predicates = model.predicates,
            breakpointIndex = breakpointIndex,
            isDarkModeEnabled = isDarkModeEnabled,
            offerState = offerState,
        )
        var visible by remember(evaluationResult) {
            mutableStateOf(
                if (!evaluationResult) {
                    false
                } else {
                    !(
                        model.transition.inTransition != EnterTransition.None ||
                            model.transition.outTransition != EnterTransition.None
                        )
                },
            )
        }
        LaunchedEffect(evaluationResult) {
            if (evaluationResult && !visible) {
                visible = true
            }
        }

        model.children.forEach { child ->
            child?.let {
                AnimatedVisibility(
                    modifier = Modifier.then(modifier),
                    visible = visible,
                    enter = model.transition.inTransition,
                    exit = model.transition.outTransition,
                ) {
                    val childModifier: Modifier = Modifier
                    modifierFactory.createContainerUiProperties(
                        containerProperties = child.containerProperties,
                        index = breakpointIndex,
                        isPressed = isPressed,
                    )
                    factory.CreateComposable(
                        model = child,
                        modifier = childModifier,
                        isPressed = isPressed,
                        breakpointIndex = breakpointIndex,
                        offerState = offerState,
                        isDarkModeEnabled = isDarkModeEnabled,
                        onEventSent = onEventSent,
                    )
                }
            }
        }
    }
}

@Composable
internal fun evaluateWhenPredicates(
    predicates: ImmutableList<WhenUiPredicate>,
    breakpointIndex: Int,
    isDarkModeEnabled: Boolean,
    offerState: OfferUiState,
): Boolean = remember(
    breakpointIndex,
    isDarkModeEnabled,
    offerState,
) {
    evaluatePredicates(
        predicates = predicates,
        breakpointIndex = breakpointIndex,
        isDarkModeEnabled = isDarkModeEnabled,
        offerState = offerState,
    )
}

internal fun evaluatePredicates(
    predicates: ImmutableList<WhenUiPredicate>,
    breakpointIndex: Int,
    isDarkModeEnabled: Boolean,
    offerState: OfferUiState,
): Boolean {
    val currentOffer = offerState.currentOfferIndex
    for (predicate in predicates) {
        val leftValue = when (predicate) {
            is WhenUiPredicate.Breakpoint -> {
                breakpointIndex
            }

            is WhenUiPredicate.Progression -> {
                // Use the current page
                (currentOffer.toDouble() / offerState.viewableItems).toInt()
            }

            is WhenUiPredicate.Position -> {
                currentOffer
            }

            is WhenUiPredicate.CustomState -> {
                offerState.customState[predicate.key] ?: 0
            }

            is WhenUiPredicate.DarkMode -> {
                if (predicate.value) 1 else 0
            }

            else -> null
        }

        val rightValue: Int? = when (predicate) {
            is WhenUiPredicate.Breakpoint -> {
                offerState.breakpoints.toList().sortedBy { it.second }.indexOfFirst { it.first == predicate.value }
                    .takeIf { it != -1 }
            }

            is WhenUiPredicate.Progression -> {
                getNormalisedProgression(
                    (offerState.lastOfferIndex.toDouble() / offerState.viewableItems).toInt(),
                    predicate.value.toInt(),
                )
            }

            is WhenUiPredicate.Position -> {
                predicate.value?.let {
                    getNormalisedPosition(offerState.lastOfferIndex, it.toInt())
                }
            }

            is WhenUiPredicate.CustomState -> {
                predicate.value
            }

            else -> null
        }

        val evaluationResult = when (predicate) {
            is WhenUiPredicate.Breakpoint -> predicate.condition.evaluate(leftValue, rightValue)
            is WhenUiPredicate.DarkMode -> predicate.condition.evaluate(leftValue == 1, isDarkModeEnabled)
            is WhenUiPredicate.Position -> predicate.condition.evaluate(leftValue, rightValue)
            is WhenUiPredicate.Progression -> predicate.condition.evaluate(leftValue as Int, rightValue)
            is WhenUiPredicate.CreativeCopy -> predicate.condition.evaluate(offerState.creativeCopy[predicate.value])
            is WhenUiPredicate.StaticBoolean -> predicate.condition.evaluate(predicate.value)
            is WhenUiPredicate.CustomState -> predicate.condition.evaluate(leftValue, rightValue)
            is WhenUiPredicate.StaticString -> predicate.condition.evaluate(predicate.input, predicate.value)
        }

        if (!evaluationResult) {
            return false
        }
    }
    return true
}

internal fun findWrappedChild(child: LayoutSchemaUiModel): LayoutSchemaUiModel {
    return if (child is LayoutSchemaUiModel.WhenUiModel && child.children.isNotEmpty()) {
        child.children[0]?.let { findWrappedChild(it) } ?: child
    } else {
        child
    }
}

private fun OrderableWhenUiCondition.evaluate(leftValue: Int?, rightValue: Int?): Boolean {
    if (leftValue == null || rightValue == null) return false
    return when (this) {
        OrderableWhenUiCondition.Is -> leftValue == rightValue
        OrderableWhenUiCondition.IsNot -> leftValue != rightValue
        OrderableWhenUiCondition.IsBelow -> leftValue < rightValue
        OrderableWhenUiCondition.IsAbove -> leftValue > rightValue
    }
}

private fun EqualityWhenUiCondition.evaluate(leftValue: Boolean, rightValue: Boolean): Boolean {
    return when (this) {
        EqualityWhenUiCondition.Is -> leftValue == rightValue
        EqualityWhenUiCondition.IsNot -> leftValue != rightValue
    }
}

private fun EqualityWhenUiCondition.evaluate(leftValue: String, rightValue: String): Boolean {
    return when (this) {
        EqualityWhenUiCondition.Is -> leftValue == rightValue
        EqualityWhenUiCondition.IsNot -> leftValue != rightValue
    }
}

private fun ExistenceWhenUiCondition.evaluate(value: String?): Boolean {
    return when (this) {
        ExistenceWhenUiCondition.Exists -> !value.isNullOrEmpty()
        ExistenceWhenUiCondition.NotExists -> value.isNullOrEmpty()
    }
}

private fun BooleanWhenUiCondition.evaluate(value: Boolean): Boolean {
    return when (this) {
        BooleanWhenUiCondition.IsTrue -> value
        BooleanWhenUiCondition.IsFalse -> !value
    }
}

private fun getNormalisedPosition(lastOfferIndex: Int, predicateValue: Int): Int = if (predicateValue < 0) {
    lastOfferIndex + 1 + predicateValue
} else {
    predicateValue
}

private fun getNormalisedProgression(lastOfferIndex: Int, predicateValue: Int): Int = if (predicateValue < 0) {
    lastOfferIndex + 1 + predicateValue
} else {
    predicateValue
}
