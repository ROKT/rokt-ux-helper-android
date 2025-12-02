package com.rokt.core.testutils.annotations

const val DCUI_COMPONENT_TAG = "TestComponent"

@Target(AnnotationTarget.FUNCTION)
annotation class DcuiConfig(
    val componentTag: String = DCUI_COMPONENT_TAG,
    val windowSize: WindowSize = WindowSize(),
    val breakpointIndex: Int = 0,
    val isDarkModeEnabled: Boolean = false,
    val testInInnerLayout: Boolean = false,
    val pseudoState: TestPseudoState = TestPseudoState(),
)

annotation class WindowSize(val width: Int = 100, val height: Int = 100)

annotation class TestPseudoState(
    val isPressed: Boolean = false,
    val isFocused: Boolean = false,
    val isHovered: Boolean = false,
    val isDisabled: Boolean = false,
)
