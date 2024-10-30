package com.core.testutils.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class DcuiNodeJson(
    val jsonString: String = "",
    val jsonFile: String = "",
    val loadComponent: Boolean = true,
)
