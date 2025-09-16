package com.rokt.core.testutils.assertion

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert

fun SemanticsNodeInteraction.assertAlpha(alpha: Float) = assert(hasAlpha(alpha))

fun hasAlpha(alpha: Float) = SemanticsMatcher("Alpha = $alpha missing") { node ->
    val expectedAlphaModifier = Modifier.alpha(alpha)
    node.layoutInfo.getModifierInfo().map { it.modifier }.contains(expectedAlphaModifier)
}
