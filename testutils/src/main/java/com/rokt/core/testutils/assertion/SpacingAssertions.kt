package com.rokt.core.testutils.assertion

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.unit.dp

fun SemanticsNodeInteraction.assertPaddingValues(top: Int, end: Int, bottom: Int, start: Int) =
    assert(hasPaddingValues(start = start, top = top, end = end, bottom = bottom))

fun SemanticsNodeInteraction.assertOffsetValues(x: Int, y: Int) = assert(hasOffsetValues(x, y))

private fun hasPaddingValues(top: Int, end: Int, bottom: Int, start: Int) =
    SemanticsMatcher("Padding modifier with top $top, end $end, bottom $bottom, start $start missing") { node ->
        val expectedPaddingModifier = Modifier.padding(start = start.dp, top = top.dp, end = end.dp, bottom = bottom.dp)
        val expectedPaddingModifier2 = Modifier.padding(PaddingValues(start.dp, top.dp, end.dp, bottom.dp))
        node.layoutInfo.getModifierInfo().any {
            it.modifier == expectedPaddingModifier || it.modifier == expectedPaddingModifier2
        }
    }

private fun hasOffsetValues(x: Int, y: Int) = SemanticsMatcher("Offset modifier with X $x, Y $y missing") { node ->
    val expectedOffsetModifier = Modifier.offset(x = x.dp, y = y.dp)
    node.layoutInfo.getModifierInfo().map { it.modifier }.contains(expectedOffsetModifier)
}
