package com.rokt.modelmapper.hmap

/**
 * Simple Map supporting heterogeneously typed values.
 *
 * Uses [TypedKey] as the Key.
 * Values can be any nullable or non-nullable type.
 */
class HMap {
    @PublishedApi
    internal val map = mutableMapOf<TypedKey<*>, Any?>()
}

/**
 * Setter
 */
inline operator fun <reified T> HMap.set(key: TypedKey<T>, value: T?) {
    map[key] = value
}

/**
 * Getter
 *
 * It acts as more of a getOrNull method and does not distinguish
 * between values being null due to the key not being present,
 * the requested type being incorrect,
 * or due to the value itself being null.
 */
inline operator fun <reified T : Any> HMap.get(key: TypedKey<T>): T? {
    return map[key] as T?
}

inline operator fun <reified T : Any> HMap.get(key: String): T? {
    return map[TypedKey<T>(key)] as T?
}
