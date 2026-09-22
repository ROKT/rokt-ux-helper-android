package com.rokt.roktux.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

private interface FakeDispatcherOwner
private class ImplementingContext(base: Context) :
    ContextWrapper(base),
    FakeDispatcherOwner
private const val NONEXISTENT_CLASS = "com.rokt.roktux.utils.DoesNotExist"

@RunWith(AndroidJUnit4::class)
class NavigationEventDispatcherCompatTest {

    private val appContext get() = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `returns true when the owner class does not exist on the classpath`() {
        assertTrue(isNavHostDispatcherAvailable(appContext, ownerClassName = NONEXISTENT_CLASS))
    }

    @Test
    fun `returns true today with the real class name, since roktux pins navigation-compose below the requirement`() {
        assertTrue(isNavHostDispatcherAvailable(appContext))
    }

    @Test
    fun `returns false when the owner class exists but nothing in the context chain implements it`() {
        val context = ContextWrapper(appContext)
        assertFalse(isNavHostDispatcherAvailable(context, ownerClassName = FakeDispatcherOwner::class.java.name))
    }

    @Test
    fun `returns true when the context itself implements the owner class`() {
        val context = ImplementingContext(appContext)
        assertTrue(isNavHostDispatcherAvailable(context, ownerClassName = FakeDispatcherOwner::class.java.name))
    }

    @Test
    fun `walks up nested ContextWrapper layers to find an implementing owner`() {
        val context = ContextWrapper(ContextWrapper(ImplementingContext(appContext)))
        assertTrue(isNavHostDispatcherAvailable(context, ownerClassName = FakeDispatcherOwner::class.java.name))
    }
}
