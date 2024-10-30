package com.rokt.roktux.testutil.assertions

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert

fun SemanticsNodeInteraction.assertGraphicsLayer(alpha: Float) = assert(hasGraphicsLayer(alpha))

fun hasGraphicsLayer(alpha: Float) = SemanticsMatcher("Graphics layer with alpha:$alpha missing") { node ->
    val expectedGraphicsLayerModifier = Modifier.graphicsLayer(alpha = alpha)
    node.layoutInfo.getModifierInfo().map { it.modifier }.contains(expectedGraphicsLayerModifier)
}
