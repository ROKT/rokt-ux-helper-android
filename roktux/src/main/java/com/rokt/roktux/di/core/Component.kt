package com.rokt.roktux.di.core

internal abstract class Component(
    private val modules: List<Module>,
    private val parentComponents: List<Component> = listOf(),
) {

    private val knownFactories = mutableMapOf<Pair<Class<out Any?>, String?>, Factory<out Any?>>()

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(type: Class<T>, name: String? = null): T {
        val typeFactory =
            knownFactories[type to name] as Factory<T>?
                ?: modules.firstNotNullOfOrNull { module -> module.get(type, name) }
                ?: parentComponents.flatMap { it.modules }
                    .firstNotNullOf { module -> module.get(type, name) as Factory<T> }
                    .also { typeFactory -> knownFactories[type to name] = typeFactory }
        return typeFactory.get(this)
    }
}

internal inline fun <reified T> Component.get(name: String? = null) = get(T::class.java, name)
