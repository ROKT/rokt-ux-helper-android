package com.rokt.demoapp.ui.screen.layouts.model

data class PreviewData(
    val tagId: String,
    val previewId: String,
    val versionId: String,
    val creativeIds: List<String>,
    val language: String?,
    val layoutVariantIds: List<String>?,
)
