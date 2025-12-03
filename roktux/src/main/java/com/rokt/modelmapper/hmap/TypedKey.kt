package com.rokt.modelmapper.hmap

/**
 * Key to be used by [HMap].
 *
 * It holds a type that can be used for typesafe get and set operations.
 * It only supports using a [String] as the discerning key value.
 * It does not support parameterized valued types (e.g. List<String>) and
 * does not check this at compile time!
 * To safely use parameterized types, wrap them in a data class first.
 *
 * Since generics cannot be reified at the class level in Kotlin,
 * the invocation method is used as a workaround.
 */

class TypedKey<T> @PublishedApi internal constructor(val type: Class<out T>, val key: String) {

    companion object {
        inline operator fun <reified T : Any> invoke(key: String): TypedKey<T> = TypedKey(T::class.java, key)
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (key.hashCode())
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TypedKey<*>

        if (type != other.type) return false
        if (key != other.key) return false

        return true
    }
}
