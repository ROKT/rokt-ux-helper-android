package com.rokt.roktux.di.core

fun interface Factory<T> {
    fun get(component: Component): T
}

internal val UNINITIALIZED = Any()
