package com.rokt.core.testutils.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class DcuiOfferJson(val jsonString: String = "", val jsonFile: String = "")
