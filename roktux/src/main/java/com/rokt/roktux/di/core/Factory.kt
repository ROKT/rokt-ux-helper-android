package com.rokt.roktux.di.core

internal fun interface Factory<T> {
    fun get(component: Component): T
}

internal val UNINITIALIZED = Any()
