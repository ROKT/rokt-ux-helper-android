package com.rokt.core.testutils.rule

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Prints the compose node tree when there is a failure
 */
class TestNodeTreeRule(private val composeTestRule: ComposeContentTestRule) : TestWatcher() {
    override fun failed(e: Throwable?, description: Description?) {
        composeTestRule.onRoot().printToLog("${description?.methodName}")
    }
}
