package com.rokt.roktux.component

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.core.testutils.annotations.DcuiNodeJson
import com.rokt.roktux.testutil.BaseDcuiEspressoTest
import com.rokt.roktux.viewmodel.layout.LayoutContract
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimerStateTriggerComponentTest : BaseDcuiEspressoTest() {

    @Test
    @DcuiNodeJson(jsonFile = "TimerStateTriggerComponent/TimerStateTrigger_Basic.json")
    fun testTimerStateTriggerBasicFunctionality() {
        // Wait for the timer to complete (100ms delay)
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(150) // Advance by more than the 100ms delay
        composeTestRule.waitForIdle()

        // Verify the correct event was captured
        val capturedEvents = getCapturedEvents()
        assertTrue(
            "Expected SetCustomState event with key 'TimerTestState' and value 1",
            capturedEvents.any { event ->
                event is LayoutContract.LayoutEvent.SetCustomState &&
                    event.key == "TimerTestState" &&
                    event.value == 1
            },
        )
    }

    @Test
    @DcuiNodeJson(jsonFile = "TimerStateTriggerComponent/TimerStateTrigger_Zero_Delay.json")
    fun testTimerStateTriggerWithZeroDelay() {
        // With zero delay, the event should fire immediately after composition
        composeTestRule.waitForIdle()

        // Verify the correct event was captured
        val capturedEvents = getCapturedEvents()
        assertTrue(
            "Expected SetCustomState event with key 'ZeroDelayState' and value 10",
            capturedEvents.any { event ->
                event is LayoutContract.LayoutEvent.SetCustomState &&
                    event.key == "ZeroDelayState" &&
                    event.value == 10
            },
        )
    }

    @Test
    @DcuiNodeJson(jsonFile = "TimerStateTriggerComponent/TimerStateTrigger_Long_Delay.json")
    fun testTimerStateTriggerWithLongDelay() {
        // Before the delay completes, no event should be fired
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(500) // Advance halfway through 1000ms delay
        composeTestRule.waitForIdle()

        val capturedEventsBeforeDelay = getCapturedEvents()
        assertFalse(
            "No SetCustomState event should be fired before delay completes",
            capturedEventsBeforeDelay.any { event ->
                event is LayoutContract.LayoutEvent.SetCustomState &&
                    event.key == "LongDelayState"
            },
        )

        // Advance time to complete the full delay
        composeTestRule.mainClock.advanceTimeBy(600) // Complete the remaining delay + buffer
        composeTestRule.waitForIdle()

        // Get the captured events and verify it
        val capturedEventsAfterDelay = getCapturedEvents()
        assertTrue(
            "Expected SetCustomState event with key 'LongDelayState' and value 5 after delay",
            capturedEventsAfterDelay.any { event ->
                event is LayoutContract.LayoutEvent.SetCustomState &&
                    event.key == "LongDelayState" &&
                    event.value == 5
            },
        )
    }

    @Test
    @DcuiNodeJson(jsonFile = "TimerStateTriggerComponent/TimerStateTrigger_Basic.json")
    fun testTimerStateTriggerEventOnlyFiresOnce() {
        // Wait for the timer to complete
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(150) // Complete the 100ms delay
        composeTestRule.waitForIdle()

        val firstEventCount = getCapturedEvents().count { event ->
            event is LayoutContract.LayoutEvent.SetCustomState &&
                event.key == "TimerTestState" &&
                event.value == 1
        }

        // Advance time further to ensure no additional events are fired
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()

        val secondEventCount = getCapturedEvents().count { event ->
            event is LayoutContract.LayoutEvent.SetCustomState &&
                event.key == "TimerTestState" &&
                event.value == 1
        }

        assertEquals(
            "Timer event should only fire once",
            firstEventCount,
            secondEventCount,
        )
        assertEquals(
            "Expected exactly one SetCustomState event",
            1,
            firstEventCount,
        )
    }
}
