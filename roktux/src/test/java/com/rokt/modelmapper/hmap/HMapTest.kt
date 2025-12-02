package com.rokt.modelmapper.hmap

import org.junit.Assert.*
import org.junit.Test

class HMapTest {

    @Test
    fun `set inserts into map`() {
        val hMap = HMap()
        hMap[TypedKey<String>("test")] = "hello"
        assertEquals("hello", hMap.map[TypedKey<String>("test")])
    }

    @Test
    fun `get with valid type retrieves from map`() {
        val hMap = HMap()
        hMap.map[TypedKey<String>("test")] = "hello"
        assertEquals("hello", hMap.get<String>(TypedKey<String>("test")))
    }

    @Test
    fun `get with non-matching type returns null`() {
        val hMap = HMap()
        hMap.map[TypedKey<String>("test")] = "hello"
        assertNull(hMap.get<Int>(TypedKey<Int>("test")))
    }

    @Test
    fun `get with no matching key returns null`() {
        val hMap = HMap()
        assertNull(hMap.get<String>(TypedKey<String>("test")))
    }
}
