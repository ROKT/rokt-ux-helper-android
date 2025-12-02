package com.rokt.core.testutils.assertion

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.test.platform.app.InstrumentationRegistry
import kotlin.math.roundToInt

fun SemanticsNodeInteraction.assertHeightFit() = assert(hasSameHeightOfWindow())

fun SemanticsNodeInteraction.assertWidthFit() = assert(hasSameWidthOfWindow())

fun SemanticsNodeInteraction.assertWidthWithPercentage(percentage: Int) = assert(hasWidthPercentage(percentage))

fun SemanticsNodeInteraction.assertHeightWithPercentage(percentage: Int) = assert(hasHeightPercentage(percentage))

fun SemanticsNodeInteraction.assertHeightWrapContent() = assert(hasWrapContentHeightInWindow())

fun SemanticsNodeInteraction.assertWidthWrapContent() = assert(hasWrapContentWidthInWindow())

private fun hasSameHeightOfWindow() = SemanticsMatcher("Height is not fit") { node ->
    val windowHeight = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.heightPixels
    (windowHeight - node.positionInWindow.y.toInt()) == node.layoutInfo.height
}

private fun hasSameWidthOfWindow() = SemanticsMatcher("Width is not fit") { node ->
    val windowWidth = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.widthPixels
    (windowWidth - node.positionInWindow.x.toInt()) == node.layoutInfo.width
}

private fun hasWidthPercentage(percentage: Int) =
    SemanticsMatcher("Width is not matching the percentage $percentage") { node ->
        val windowWidth =
            InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.widthPixels
        val computedWidth = (percentage.toFloat() / 100) * (windowWidth.toFloat() - node.positionInWindow.x)
        computedWidth.roundToInt() == node.layoutInfo.width
    }

private fun hasHeightPercentage(percentage: Int) =
    SemanticsMatcher("Height is not matching the percentage $percentage") { node ->
        val windowHeight =
            InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.heightPixels
        val computedHeight = (percentage.toFloat() / 100) * (windowHeight.toFloat() - node.positionInWindow.y)
        computedHeight.roundToInt() == node.layoutInfo.height
    }

private fun hasWrapContentHeightInWindow() = SemanticsMatcher("Height is not wrap-content") { node ->
    val windowHeight = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.heightPixels
    (windowHeight - node.positionInWindow.y.toInt()) != node.layoutInfo.height
}

private fun hasWrapContentWidthInWindow() = SemanticsMatcher("Width is not wrap-content") { node ->
    val windowWidth = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.widthPixels
    (windowWidth - node.positionInWindow.x.toInt()) != node.layoutInfo.width
}
