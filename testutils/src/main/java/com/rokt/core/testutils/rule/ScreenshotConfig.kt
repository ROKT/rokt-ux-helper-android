package com.rokt.core.testutils.rule

@Target(AnnotationTarget.FUNCTION)
annotation class ScreenshotConfig(val captureOnSuccess: Boolean = false)
