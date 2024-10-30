package com.rokt.roktux.viewmodel.base

internal class BaseContract {

    internal interface BaseEvent

    internal sealed interface BaseViewState<out T> {
        data class Success<T>(val value: T) : BaseViewState<T>
        data class Error(val throwable: Throwable) : BaseViewState<Nothing>
        object Empty : BaseViewState<Nothing>
    }

    internal interface BaseEffect
}
