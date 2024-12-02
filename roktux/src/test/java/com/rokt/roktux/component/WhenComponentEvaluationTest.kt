package com.rokt.roktux.component

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import com.rokt.modelmapper.uimodel.BooleanWhenUiCondition
import com.rokt.modelmapper.uimodel.EqualityWhenUiCondition
import com.rokt.modelmapper.uimodel.ExistenceWhenUiCondition
import com.rokt.modelmapper.uimodel.LayoutSchemaUiModel
import com.rokt.modelmapper.uimodel.OrderableWhenUiCondition
import com.rokt.modelmapper.uimodel.WhenUiHidden
import com.rokt.modelmapper.uimodel.WhenUiPredicate
import com.rokt.modelmapper.uimodel.WhenUiTransition
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WhenComponentEvaluationTest {

    @Test
    fun `given predicate target is breakpoint and breakpoint value is missing in the map, then evaluate should return false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.Is, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is breakpoint and condition is 'equals', when the breakpoint index is same as the sorted Landscape breakpoint index, then evaluate should return true`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560)
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.Is, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is breakpoint and condition is 'equals', when the breakpoint index is below the sorted Landscape breakpoint index, then evaluate should return false`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560, "Mobile" to 100)
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.Is, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is breakpoint and condition is 'equals', when the breakpoint index is above the sorted Landscape breakpoint index, then evaluate should return false`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560, "Desktop" to 1000)
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.Is, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 1,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is breakpoint and condition is 'below', when the breakpoint index is below Landscape breakpoint index, then evaluate should return true`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560, "Mobile" to 100)
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsBelow, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is breakpoint and condition is 'below', when the breakpint index is above the sorted Landscape breakpoint index, then evaluate should return false`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560, "Desktop" to 1000)
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsBelow, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 1,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is breakpoint and condition is 'below', when the breakpoint index is equal to the sorted Landscape breakpoint index, then evaluate should return false`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560, "Mobile" to 100)
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsBelow, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 1,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is breakpoint and condition is 'above', when the breakpoint index is above the sorted Landscape breakpoint index, then evaluate should return true`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560, "Desktop" to 1000)
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsAbove, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 1,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is breakpoint and condition is 'above', when the breakpoint index is below the sorted Landscape breakpoint index, then evaluate should return false`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560, "Mobile" to 100)
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsAbove, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is breakpoint and condition is 'above', when the breakpoint index is equal to the sorted Landscape breakpoint index, then evaluate should return false`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560, "Desktop" to 1000)
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsAbove, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is breakpoint and condition is 'not equal', when the breakpoint index is above the sorted Landscape breakpoint index, then evaluate should return true`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560, "Desktop" to 1000)
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsAbove, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 1,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is breakpoint and condition is 'not equal', when the breakpoint index is below the sorted Landscape breakpoint index, then evaluate should return true`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560, "Mobile" to 100)
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsNot, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is breakpoint and condition is 'not equal', when the screen width is equal to Landscape breakpoint value, then evaluate should return false`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560)
        val predicate =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsNot, value = "Landscape")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given two breakpoint predicates are there and one predicate fails, then evaluate should return false`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560, "Mobile" to 700)
        val predicate1 =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsAbove, value = "Landscape")
        val predicate2 =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsBelow, value = "Mobile")
        val uiModel = createWhenUiModel(predicate1, predicate2)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given two breakpoint predicates are there and all the predicates pass, then evaluate should return true`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560, "Mobile" to 700, "Desktop" to 1000)
        val predicate1 =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsAbove, value = "Landscape")
        val predicate2 =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsBelow, value = "Desktop")
        val uiModel = createWhenUiModel(predicate1, predicate2)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 1,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is progression and condition is 'equals', when the current offer position is equal to the value, then evaluate should return true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.Progression(condition = OrderableWhenUiCondition.Is, value = "1")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is progression and condition is 'not equal', when the current offer position is equal to the value, then evaluate should return false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.Progression(condition = OrderableWhenUiCondition.IsNot, value = "1")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is progression and condition is 'above', when the current offer position is above the value, then evaluate should return true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.Progression(condition = OrderableWhenUiCondition.IsAbove, value = "1")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 2,
                lastOfferIndex = 2,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is progression and condition is 'above', when the current offer position is below the value, then evaluate should return false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.Progression(condition = OrderableWhenUiCondition.IsAbove, value = "1")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is progression and condition is 'below', when the current offer position is below the value, then evaluate should return false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.Progression(condition = OrderableWhenUiCondition.IsBelow, value = "1")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is progression and condition is 'below', when the current offer position is above the value, then evaluate should return true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.Progression(condition = OrderableWhenUiCondition.IsBelow, value = "1")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given one breakpoint predicate and one progression predicate are there and all the predicates pass, then evaluate should return true`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560)
        val predicate1 =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.Is, value = "Landscape")
        val predicate2 =
            WhenUiPredicate.Progression(condition = OrderableWhenUiCondition.Is, value = "1")
        val uiModel = createWhenUiModel(predicate1, predicate2)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given one breakpoint predicate and one progression predicate are there and progression predicate fails, then evaluate should return false`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560)
        val predicate1 =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.Is, value = "Landscape")
        val predicate2 =
            WhenUiPredicate.Progression(condition = OrderableWhenUiCondition.Is, value = "1")
        val uiModel = createWhenUiModel(predicate1, predicate2)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 2,
                lastOfferIndex = 2,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given one breakpoint predicate and one progression predicate are there and breakpoint predicate fails, then evaluate should return false`() {
        // Arrange
        val breakPoints = persistentMapOf("Landscape" to 560)
        val predicate1 =
            WhenUiPredicate.Breakpoint(condition = OrderableWhenUiCondition.IsAbove, value = "Landscape")
        val predicate2 =
            WhenUiPredicate.Progression(condition = OrderableWhenUiCondition.Is, value = "1")
        val uiModel = createWhenUiModel(predicate1, predicate2)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = breakPoints,
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is progression and condition is 'is', target is 1, viewable items is 2, and the current offer position is 1, then evaluate should return true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.Progression(condition = OrderableWhenUiCondition.Is, value = "0")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 0,
                lastOfferIndex = 3,
                viewableItems = 2,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is progression and condition is 'is', target is 1, viewable items is 2, and the current offer position is 2, then evaluate should return false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.Progression(condition = OrderableWhenUiCondition.Is, value = "0")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 2,
                lastOfferIndex = 3,
                viewableItems = 2,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is darkMode and condition is 'Is' with value true, when the dark mode is enabled, then evaluate should return true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.DarkMode(condition = EqualityWhenUiCondition.Is, value = true)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = true,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is darkMode and condition is 'Is' with value false, when the dark mode is enabled, then evaluate should return false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.DarkMode(condition = EqualityWhenUiCondition.Is, value = false)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = true,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is darkMode and condition is 'IsNot' with value true, when the dark mode is enabled, then evaluate should return false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.DarkMode(condition = EqualityWhenUiCondition.IsNot, value = true)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = true,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is darkMode and condition is 'Is' with value true, when the dark mode is disabled, then evaluate should return false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.DarkMode(condition = EqualityWhenUiCondition.Is, value = true)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is darkMode and condition is 'IsNot' with value true, when the dark mode is disabled, then evaluate should return true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.DarkMode(condition = EqualityWhenUiCondition.IsNot, value = true)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is staticBoolean and condition is 'isTrue', when the value is true, then evaluate should return true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.StaticBoolean(condition = BooleanWhenUiCondition.IsTrue, value = true)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is staticBoolean and condition is 'isTrue', when the value is false, then evaluate should return false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.StaticBoolean(condition = BooleanWhenUiCondition.IsTrue, value = false)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is staticBoolean and condition is 'isFalse', when the value is true, then evaluate should return false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.StaticBoolean(condition = BooleanWhenUiCondition.IsFalse, value = true)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is staticBoolean and condition is 'isFalse, when the value is false, then evaluate should return true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.StaticBoolean(condition = BooleanWhenUiCondition.IsFalse, value = false)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is creativeCopy and condition is 'Exists', when the value is creativetest and the copy map contains the key, then evaluate should return true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.CreativeCopy(condition = ExistenceWhenUiCondition.Exists, value = "creativetest")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult =
            evaluatePredicates(
                predicates = uiModel.predicates,
                breakpointIndex = 0,
                isDarkModeEnabled = false,
                offerState = OfferUiState(
                    currentOfferIndex = 1,
                    lastOfferIndex = 1,
                    viewableItems = 1,
                    creativeCopy = persistentMapOf("creativetest" to "test"),
                    breakpoints = persistentMapOf(),
                    customState = persistentMapOf(),
                ),
            )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is creativeCopy and condition is 'NotExists', when the value is creativetest and the copy map is empty, then evaluate should return true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.CreativeCopy(condition = ExistenceWhenUiCondition.NotExists, value = "creativetest")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult =
            evaluatePredicates(
                predicates = uiModel.predicates,
                breakpointIndex = 0,
                isDarkModeEnabled = false,
                offerState = OfferUiState(
                    currentOfferIndex = 1,
                    lastOfferIndex = 1,
                    viewableItems = 1,
                    creativeCopy = persistentMapOf(),
                    breakpoints = persistentMapOf(),
                    customState = persistentMapOf(),
                ),
            )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is custom state and condition is 'is', when the custom state value matches, it should evaluate true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.CustomState(condition = OrderableWhenUiCondition.Is, key = "my_state", value = 2)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult =
            evaluatePredicates(
                predicates = uiModel.predicates,
                breakpointIndex = 0,
                isDarkModeEnabled = false,
                offerState = OfferUiState(
                    currentOfferIndex = 1,
                    lastOfferIndex = 1,
                    viewableItems = 1,
                    creativeCopy = persistentMapOf(),
                    breakpoints = persistentMapOf(),
                    customState = persistentMapOf("my_state" to 2),
                ),
            )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is custom state and condition is 'is', when the custom state is missing, it should evaluate false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.CustomState(condition = OrderableWhenUiCondition.Is, key = "my_state", value = 2)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult =
            evaluatePredicates(
                predicates = uiModel.predicates,
                breakpointIndex = 0,
                isDarkModeEnabled = false,
                offerState = OfferUiState(
                    currentOfferIndex = 1,
                    lastOfferIndex = 1,
                    viewableItems = 1,
                    creativeCopy = persistentMapOf(),
                    breakpoints = persistentMapOf(),
                    customState = persistentMapOf(),
                ),
            )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is custom state and condition is 'is-not', when the custom state is missing, it should evaluate true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.CustomState(condition = OrderableWhenUiCondition.IsNot, key = "my_state", value = 2)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult =
            evaluatePredicates(
                predicates = uiModel.predicates,
                breakpointIndex = 0,
                isDarkModeEnabled = false,
                offerState = OfferUiState(
                    currentOfferIndex = 1,
                    lastOfferIndex = 1,
                    viewableItems = 1,
                    creativeCopy = persistentMapOf(),
                    breakpoints = persistentMapOf(),
                    customState = persistentMapOf(),
                ),
            )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is custom state and condition is 'is', when the custom state value is not matching, it should evaluate false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.CustomState(condition = OrderableWhenUiCondition.Is, key = "my_state", value = 2)
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult =
            evaluatePredicates(
                predicates = uiModel.predicates,
                breakpointIndex = 0,
                isDarkModeEnabled = false,
                offerState = OfferUiState(
                    currentOfferIndex = 1,
                    lastOfferIndex = 1,
                    viewableItems = 1,
                    creativeCopy = persistentMapOf(),
                    breakpoints = persistentMapOf(),
                    customState = persistentMapOf("my_state" to 1),
                ),
            )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is staticString and input is 'test', when the value is 'test' and condition is 'is', then evaluate should return true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.StaticString(condition = EqualityWhenUiCondition.Is, input = "test", value = "test")
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    @Test
    fun `given predicate target is staticString and input is 'test', when the value is 'test' and condition is 'is-not', then evaluate should return false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.StaticString(
                condition = EqualityWhenUiCondition.IsNot,
                input = "test",
                value = "test",
            )
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is staticString and input is 'test', when the value is 'nottest' and condition is 'is', then evaluate should return false`() {
        // Arrange
        val predicate =
            WhenUiPredicate.StaticString(
                condition = EqualityWhenUiCondition.Is,
                input = "test",
                value = "nottest",
            )
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertFalse(evaluationResult)
    }

    @Test
    fun `given predicate target is staticString and input is 'test', when the value is 'nottest' and condition is 'is-not', then evaluate should return true`() {
        // Arrange
        val predicate =
            WhenUiPredicate.StaticString(
                condition = EqualityWhenUiCondition.IsNot,
                input = "test",
                value = "nottest",
            )
        val uiModel = createWhenUiModel(predicate)

        // Act
        val evaluationResult = evaluatePredicates(
            predicates = uiModel.predicates,
            breakpointIndex = 0,
            isDarkModeEnabled = false,
            offerState = OfferUiState(
                currentOfferIndex = 1,
                lastOfferIndex = 1,
                viewableItems = 1,
                creativeCopy = persistentMapOf(),
                breakpoints = persistentMapOf(),
                customState = persistentMapOf(),
            ),
        )

        // Assert
        assertTrue(evaluationResult)
    }

    private fun createWhenUiModel(vararg predicate: WhenUiPredicate): LayoutSchemaUiModel.WhenUiModel = LayoutSchemaUiModel.WhenUiModel(
        predicates = predicate.toList().toImmutableList(),
        children = persistentListOf(),
        transition = WhenUiTransition(
            inTransition = EnterTransition.None,
            outTransition = ExitTransition.None,
        ),
        hide = WhenUiHidden.Visually,
    )
}
