package com.rokt.core.di

abstract class Component(
    internal val modules: List<Module>,
    internal val parentComponents: List<Component> = emptyList(),
) {

    private val knownFactories = mutableMapOf<Pair<Class<out Any?>, String?>, Factory<out Any?>>()

    /**
     * Retrieve an instance of type [T] from the current component or its parent components.
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(type: Class<T>, name: String? = null): T {
        // Attempt to find a factory in the current component or parent components
        val typeFactory = knownFactories[type to name] as? Factory<T>
            ?: modules.firstNotNullOfOrNull { module ->
                module.get(type, name)
            }
            ?: findInParentComponentsRecursively(parentComponents, type, name)
                ?.also { factory ->
                    knownFactories[type to name] = factory as Factory<out Any?>
                }

        return typeFactory?.get(this)
            ?: throw IllegalStateException("No factory found for type $type with name $name")
    }

    fun <T> getOrNull(type: Class<T>, name: String? = null): T? {
        try {
            return get(type, name)
        } catch (e: IllegalStateException) {
            return null
        }
    }

    /**
     * Retrieve an instance of type [T] using reified generics.
     */
    inline fun <reified T> get(name: String? = null): T = get(T::class.java, name)
}

/**
 * Recursively search parent components for a factory of the specified type and name.
 */
private fun <T> findInParentComponentsRecursively(
    parentComponents: List<Component>,
    type: Class<T>,
    name: String?,
): Factory<T>? {
    for (parent in parentComponents) {
        // Check the modules in the current parent component
        val factory = parent.modules.firstNotNullOfOrNull { module ->
            module.get(type, name) as? Factory<T>
        }
        if (factory != null) return factory

        // Recurse into the parent component's parents
        val recursiveFactory = findInParentComponentsRecursively(parent.parentComponents, type, name)
        if (recursiveFactory != null) return recursiveFactory
    }
    return null
}
