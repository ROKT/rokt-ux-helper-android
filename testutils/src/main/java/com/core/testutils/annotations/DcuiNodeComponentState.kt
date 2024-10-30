package com.core.testutils.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class DcuiNodeComponentState(
    val currentOffer: Int = 0,
    val totalOffer: Int = 0,
    val viewableItems: IntArray = [1],
    val creativeCopy: Array<DcuiCreativeCopy> = [],
    val breakpoints: Array<DcuiBreakpoint> = [], // For UXHelper
)

@Target(AnnotationTarget.FIELD)
annotation class DcuiCreativeCopy(
    val key: String,
    val value: String,
)
