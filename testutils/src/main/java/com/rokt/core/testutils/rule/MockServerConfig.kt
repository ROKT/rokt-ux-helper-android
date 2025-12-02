package com.rokt.core.testutils.rule

@Target(AnnotationTarget.FUNCTION)
annotation class MockServerConfig(
    val defaultResponseSuccess: Boolean = true,
    val routeConfigs: Array<RouteConfig> = [],
)
