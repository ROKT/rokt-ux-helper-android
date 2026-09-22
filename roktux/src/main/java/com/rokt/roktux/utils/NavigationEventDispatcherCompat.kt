package com.rokt.roktux.utils

import android.content.Context
import android.content.ContextWrapper

private const val NAVIGATION_EVENT_DISPATCHER_OWNER_CLASS = "androidx.navigationevent.NavigationEventDispatcherOwner"

/**
 * navigation-compose 2.10+ requires a `NavigationEventDispatcher` from the host Activity
 * (provided automatically once the host resolves androidx.activity 1.12+). Whether that
 * requirement applies at all, and whether the host satisfies it, depends entirely on
 * dependency versions the *host app* resolves, not on any version roktux itself declares, so
 * this can only be determined at runtime, via reflection, rather than a compile-time check.
 *
 * Returns true when calling `NavHost` is safe: either the resolved navigation-compose predates
 * the requirement, or the host Activity satisfies it.
 *
 * [ownerClassName] defaults to the real androidx class and only exists so tests can substitute
 * a fake class name, since the real one requires a navigation-compose version this module
 * cannot compile against yet (see PR #336).
 */
internal fun isNavHostDispatcherAvailable(
    context: Context,
    ownerClassName: String = NAVIGATION_EVENT_DISPATCHER_OWNER_CLASS,
): Boolean {
    val ownerClass = try {
        Class.forName(ownerClassName)
    } catch (e: ClassNotFoundException) {
        return true
    }
    return try {
        var current: Context? = context
        while (current != null) {
            if (ownerClass.isInstance(current)) return true
            current = (current as? ContextWrapper)?.baseContext
        }
        false
    } catch (e: Exception) {
        false
    }
}
