package com.rokt.core.testutils.assertion

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.unit.dp

fun SemanticsNodeInteraction.assertBorderProperties(borderWidth: Float, borderColor: String, borderRadius: Float) =
    assert(hasBorderProperties(borderWidth, borderColor, borderRadius))

private fun hasBorderProperties(borderWidth: Float, borderColor: String, borderRadius: Float) =
    SemanticsMatcher("Border properties not present") { node ->
        val expectedBorderModifier = Modifier.border(
            width = borderWidth.dp,
            color = Color(android.graphics.Color.parseColor(borderColor)),
            shape = RoundedCornerShape(borderRadius.dp),
        )
        node.layoutInfo.getModifierInfo().map { it.modifier }.contains(expectedBorderModifier)
    }
