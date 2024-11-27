package com.core.testutils.rule

@Target(AnnotationTarget.FUNCTION)
annotation class ScreenshotConfig(val captureOnSuccess: Boolean = false)
