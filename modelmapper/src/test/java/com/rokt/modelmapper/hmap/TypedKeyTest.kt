package com.rokt.modelmapper.hmap

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class TypedKeyTest {

    @Test
    fun `invoke with parameterized type throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            TypedKey<List<String>>("key")
        }
    }

    @Test
    fun `invocation with key sets key`() {
        val tKey = TypedKey<String>("key")
        assertEquals("key", tKey.key)
    }
}
