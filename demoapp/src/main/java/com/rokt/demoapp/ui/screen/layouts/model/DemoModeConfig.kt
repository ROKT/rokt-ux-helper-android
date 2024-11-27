package com.rokt.demoapp.ui.screen.layouts.model

data class DemoModeConfig(val layouts: List<DemoLayoutConfig>)

data class DemoLayoutConfig(
    val layoutId: String,
    val versionId: String,
    val targetElementSelector: String,
    val slots: List<DemoLayoutSlotConfig>,
)

data class DemoLayoutSlotConfig(val layoutVariantId: String, val creativeId: String)
