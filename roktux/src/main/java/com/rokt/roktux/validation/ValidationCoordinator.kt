package com.rokt.roktux.validation

internal enum class ValidationStatus {
    VALID,
    INVALID,
}

internal class ValidationCoordinator {
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

    fun unregisterField(key: String, owner: Any) {
        synchronized(lock) {
            if (registrations[key]?.owner === owner) {
                registrations.remove(key)
            }
        }
    }

    fun validate(fields: List<String>): Boolean = fields.map { key -> validate(key) }.all { isValid -> isValid }

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
