package com.core.testutils.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class DcuiBreakpoints(val breakpoints: Array<DcuiBreakpoint>)

annotation class DcuiBreakpoint(val key: String, val value: Int)
