package com.rokt.modelmapper.data

sealed interface BindData {
    data class Value(val text: String) : BindData
    data class State(val state: BindState) : BindData
    object Undefined : BindData
}

enum class BindState {
    OFFER_POSITION,
}
