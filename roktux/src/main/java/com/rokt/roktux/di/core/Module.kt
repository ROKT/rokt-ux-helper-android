package com.rokt.roktux.di.core

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class Module {
    private val typeFactories = mutableMapOf<Pair<Class<out Any?>, String?>, Factory<out Any?>>()
    private val lock = ReentrantLock()

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(type: Class<T>, name: String? = null): Factory<T>? = typeFactories[type to name] as Factory<T>?

    fun <T> provide(type: Class<T>, factory: Factory<T>, name: String? = null) {
        typeFactories[type to name] = factory
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> provideModuleScoped(type: Class<T>, name: String? = null, lazy: Boolean = false, factory: Factory<T>) {
        lock.withLock {
            val singletonFactory: Factory<T> = if (lazy) {
                // Lazy initialization: instance is created only when first accessed
                val instanceHolder = LazyInstanceHolder(factory)
                Factory { component -> instanceHolder.getInstance(component) }
            } else {
                // Eager initialization: instance is created immediately
                var instance: Any? = UNINITIALIZED
                Factory { component ->
                    if (instance === UNINITIALIZED) {
                        instance = factory.get(component)
                    }
                    instance as T
                }
            }
            typeFactories[type to name] = singletonFactory
        }
    }

    inline fun <reified R, reified P : R> Module.bind(name: String? = null) {
        provide(
            R::class.java,
            { component ->
                component[P::class.java]
            },
            name,
        )
    }

    inline fun <reified T> Module.provide(noinline factory: Component.() -> T, name: String? = null) =
        provide(T::class.java, factory, name)

    inline fun <reified T> Module.provideModuleScoped(
        name: String? = null,
        lazy: Boolean = false,
        noinline factory: Component.() -> T,
    ) = provideModuleScoped(T::class.java, name, false, factory)
}

/**
 * Helper class for handling thread-safe lazy initialization.
 */
private class LazyInstanceHolder<T>(private val factory: Factory<T>) {
    @Volatile
    private var instance: Any? = UNINITIALIZED

    fun getInstance(component: Component): T {
        if (instance === UNINITIALIZED) {
            synchronized(this) {
                if (instance === UNINITIALIZED) {
                    instance = factory.get(component)
                }
            }
        }
        return instance as T
    }
}
