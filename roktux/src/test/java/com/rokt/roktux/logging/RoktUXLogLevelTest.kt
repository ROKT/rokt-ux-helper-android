package com.rokt.roktux.logging

import org.junit.Assert.assertEquals
import org.junit.Test

class RoktUXLogLevelTest {

    @Test
    fun `priority ordering should be VERBOSE lowest and NONE highest`() {
        // Arrange & Act & Assert
        assert(RoktUXLogLevel.VERBOSE.priority < RoktUXLogLevel.DEBUG.priority)
        assert(RoktUXLogLevel.DEBUG.priority < RoktUXLogLevel.INFO.priority)
        assert(RoktUXLogLevel.INFO.priority < RoktUXLogLevel.WARNING.priority)
        assert(RoktUXLogLevel.WARNING.priority < RoktUXLogLevel.ERROR.priority)
        assert(RoktUXLogLevel.ERROR.priority < RoktUXLogLevel.NONE.priority)
    }

    @Test
    fun `fromPriority should return correct level for valid priorities`() {
        // Arrange & Act & Assert
        assertEquals(RoktUXLogLevel.VERBOSE, RoktUXLogLevel.fromPriority(0))
        assertEquals(RoktUXLogLevel.DEBUG, RoktUXLogLevel.fromPriority(1))
        assertEquals(RoktUXLogLevel.INFO, RoktUXLogLevel.fromPriority(2))
        assertEquals(RoktUXLogLevel.WARNING, RoktUXLogLevel.fromPriority(3))
        assertEquals(RoktUXLogLevel.ERROR, RoktUXLogLevel.fromPriority(4))
        assertEquals(RoktUXLogLevel.NONE, RoktUXLogLevel.fromPriority(5))
    }

    @Test
    fun `fromPriority should return NONE for unknown priority`() {
        // Arrange & Act & Assert
        assertEquals(RoktUXLogLevel.NONE, RoktUXLogLevel.fromPriority(-1))
        assertEquals(RoktUXLogLevel.NONE, RoktUXLogLevel.fromPriority(99))
    }
}
