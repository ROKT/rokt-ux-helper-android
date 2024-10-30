package com.core.testutils

import io.mockk.clearAllMocks
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before

open class MockkUnitTest {

    open fun onCreate() {}

    open fun onDestroy() {}

    @Before
    fun setUp() {
        onCreate()
    }

    @After
    fun tearDown() {
        onDestroy()
        unmockkAll()
        clearAllMocks()
    }
}
