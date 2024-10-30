package com.rokt.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.roktux.utils.getDeviceName
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowBuild

@RunWith(AndroidJUnit4::class)
class UtilsTest {

    @Before
    fun setUp() {
        ShadowBuild.setManufacturer("google")
        ShadowBuild.setModel("Pixel 9📱•")
    }

    @Test
    fun testGetDeviceName() {
        val deviceName = getDeviceName()
        assertEquals("Google Pixel 9", deviceName)
    }
}
