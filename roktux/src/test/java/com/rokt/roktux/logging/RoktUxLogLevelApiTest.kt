package com.rokt.roktux.logging

import com.rokt.roktux.RoktUx
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RoktUxLogLevelApiTest {

    @Before
    fun setUp() {
        RoktUXLogger.logLevel = RoktUXLogLevel.NONE
    }

    @After
    fun tearDown() {
        RoktUXLogger.logLevel = RoktUXLogLevel.NONE
    }

    @Test
    fun `setLogLevel should propagate to RoktUXLogger`() {
        // Arrange & Act
        RoktUx.setLogLevel(RoktUXLogLevel.DEBUG)

        // Assert
        assertEquals(RoktUXLogLevel.DEBUG, RoktUXLogger.logLevel)
    }

    @Test
    fun `setLogLevel to NONE should disable logging`() {
        // Arrange
        RoktUx.setLogLevel(RoktUXLogLevel.DEBUG)

        // Act
        RoktUx.setLogLevel(RoktUXLogLevel.NONE)

        // Assert
        assertEquals(RoktUXLogLevel.NONE, RoktUXLogger.logLevel)
    }

    @Test
    fun `setLogLevel should accept all valid levels`() {
        // Arrange & Act & Assert
        for (level in RoktUXLogLevel.values()) {
            RoktUx.setLogLevel(level)
            assertEquals(level, RoktUXLogger.logLevel)
        }
    }
}
