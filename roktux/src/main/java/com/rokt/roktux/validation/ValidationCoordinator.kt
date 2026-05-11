package com.rokt.roktux.validation

internal enum class ValidationStatus {
    VALID,
    INVALID,
}

// Per-layout registry where Compose fields register validators by key, and trigger components
// (e.g. submit buttons) call `validate(...)` before emitting events.
internal class ValidationCoordinator {
    // `owner` identifies the registering composable so a stale DisposableEffect cleanup from an
    // outgoing component cannot remove a fresh registration the incoming component took for the same key.
    private data class Registration(
        val owner: Any,
        val validation: () -> ValidationStatus,
        val onStatusChange: (ValidationStatus) -> Unit,
        val lastStatus: ValidationStatus? = null,
    )

    private val lock = Any()
    private val registrations = mutableMapOf<String, Registration>()

    fun registerField(
        key: String,
        owner: Any,
        validation: () -> ValidationStatus,
        onStatusChange: (ValidationStatus) -> Unit,
    ) {
        synchronized(lock) {
            registrations[key] = Registration(
                owner = owner,
                validation = validation,
                onStatusChange = onStatusChange,
            )
        }
    }

    // No-op when the slot has already been taken over by another owner — protects against
    // out-of-order Compose dispose/register sequences during recomposition.
    fun unregisterField(key: String, owner: Any) {
        synchronized(lock) {
            if (registrations[key]?.owner === owner) {
                registrations.remove(key)
            }
        }
    }

    // Eagerly validates every key (no short-circuit) so all `onStatusChange` callbacks fire and
    // every invalid field surfaces its error on a single submit attempt.
    fun validate(fields: List<String>): Boolean = fields.map { key -> validate(key) }.all { isValid -> isValid }

    // Unregistered keys are treated as VALID so callers can request validation for optional fields
    // without having to know which ones are currently mounted. The `validation()` closure and
    // `onStatusChange` callback are invoked outside the lock to avoid reentrancy deadlocks when
    // they trigger Compose recomposition or register/unregister calls.
    fun validate(key: String): Boolean {
        val registration = synchronized(lock) { registrations[key] } ?: return true
        val status = registration.validation()

        synchronized(lock) {
            if (registrations[key]?.owner === registration.owner) {
                registrations[key] = registration.copy(lastStatus = status)
            }
        }

        registration.onStatusChange(status)
        return status == ValidationStatus.VALID
    }
}
