package com.rokt.core.di

fun interface Factory<T> {
    fun get(component: Component): T
}

internal val UNINITIALIZED = Any()
