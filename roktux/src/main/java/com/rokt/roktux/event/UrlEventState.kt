package com.rokt.roktux.event

import java.util.concurrent.atomic.AtomicBoolean

data class UrlEventState(
    val url: String,
    val responseUrl: Boolean,
    val isClosed: AtomicBoolean = AtomicBoolean(false),
)
