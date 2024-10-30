package com.core.testutils.rule

@Target(AnnotationTarget.FUNCTION)
annotation class MockServerConfig(
    val defaultResponseSuccess: Boolean = true,
    val routeConfigs: Array<RouteConfig> = [],
)
