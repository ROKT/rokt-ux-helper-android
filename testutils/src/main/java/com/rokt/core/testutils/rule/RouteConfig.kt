package com.rokt.core.testutils.rule

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Repeatable
annotation class RouteConfig(
    val path: String,
    val responseConfig: ResponseConfig,
    val requestValidation: RequestValidation = RequestValidation(),
)

@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class ResponseConfig(
    val responseResourceFile: String,
    val headers: Array<HeaderValue> = [],
    val responseCode: Int = 200,
)

@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class RequestValidation(val headers: Array<HeaderValue> = [], val body: String = "")

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Repeatable
annotation class HeaderValue(val key: String, val value: String)
