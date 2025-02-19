package com.rokt.core.testutils.assertion

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.unit.Dp

fun SemanticsNodeInteraction.assertBackgroundColor(color: String) = assert(hasBackgroundColor(color))

fun SemanticsNodeInteraction.assertBackgroundImage() = assert(hasBackgroundImageNode())

fun hasBackgroundColor(color: String, cornerRadius: Dp? = null) =
    SemanticsMatcher("Background color = $color missing") { node ->
        val expectedBackgroundModifier = cornerRadius?.let {
            Modifier.background(
                Color(android.graphics.Color.parseColor(color)),
                shape = RoundedCornerShape(cornerRadius),
            )
        } ?: Modifier.background(Color(android.graphics.Color.parseColor(color)))
        node.layoutInfo.getModifierInfo().map { it.modifier }.contains(expectedBackgroundModifier)
    }

private fun hasBackgroundImageNode() = SemanticsMatcher("Background image node not present") { node ->
    val role: Role = node.children[0].config[SemanticsProperties.Role]
    role == Role.Image
}
