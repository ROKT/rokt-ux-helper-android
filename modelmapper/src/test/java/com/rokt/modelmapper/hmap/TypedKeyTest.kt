package com.rokt.modelmapper.hmap

import org.junit.Assert.assertEquals
import org.junit.Test

class TypedKeyTest {

    @Test
    fun `invocation with key sets key`() {
        val tKey = TypedKey<String>("key")
        assertEquals("key", tKey.key)
    }
}
