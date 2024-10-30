package com.rokt.roktux.testutil

import androidx.compose.ui.test.junit4.createComposeRule
import com.core.testutils.rule.TestNodeTreeRule
import com.core.testutils.rule.TestScreenshotCaptureRule
import com.rokt.roktux.viewmodel.base.BaseContract
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain
import org.robolectric.shadows.ShadowLog

abstract class BaseDcuiEspressoTest {

    val composeTestRule = createComposeRule()
    val dcuiComponentRule: DcuiComponentRule = DcuiComponentRule(composeTestRule)

    @get:Rule
    val ruleChain = RuleChain.outerRule(composeTestRule)
        .around(dcuiComponentRule)
        .around(TestNodeTreeRule(composeTestRule))
        .around(TestScreenshotCaptureRule(composeTestRule))

    @Before
    fun setup() {
        ShadowLog.stream = System.out
    }

    internal fun getCapturedEvents(): List<BaseContract.BaseEvent> = dcuiComponentRule.capturedEvents
}
