package com.rokt.core.testutils.mockserver

data class CapturedEvent(val sessionId: String, val eventType: String, val parentGuid: String, val token: String)
