package com.rokt.roktux.validation

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ValidationCoordinatorTest {

    @Test
    fun `missing field is valid`() {
        val coordinator = ValidationCoordinator()

        assertThat(coordinator.validate("missing")).isTrue()
    }

    @Test
    fun `validate runs every requested field before returning aggregate result`() {
        val coordinator = ValidationCoordinator()
        val owner = Any()
        val statuses = mutableListOf<String>()

        coordinator.registerField(
            key = "first",
            owner = owner,
            validation = { ValidationStatus.INVALID },
            onStatusChange = { statuses += "first:$it" },
        )
        coordinator.registerField(
            key = "second",
            owner = owner,
            validation = { ValidationStatus.VALID },
            onStatusChange = { statuses += "second:$it" },
        )

        assertThat(coordinator.validate(listOf("first", "second"))).isFalse()
        assertThat(statuses).containsExactly("first:INVALID", "second:VALID")
    }

    @Test
    fun `unregister only removes field for matching owner`() {
        val coordinator = ValidationCoordinator()
        val owner = Any()
        val otherOwner = Any()
        var validationCount = 0

        coordinator.registerField(
            key = "field",
            owner = owner,
            validation = {
                validationCount += 1
                ValidationStatus.INVALID
            },
            onStatusChange = {},
        )

        coordinator.unregisterField(key = "field", owner = otherOwner)
        assertThat(coordinator.validate("field")).isFalse()
        assertThat(validationCount).isEqualTo(1)

        coordinator.unregisterField(key = "field", owner = owner)
        assertThat(coordinator.validate("field")).isTrue()
        assertThat(validationCount).isEqualTo(1)
    }

    @Test
    fun `registering the same key replaces previous owner`() {
        val coordinator = ValidationCoordinator()
        val firstOwner = Any()
        val secondOwner = Any()

        coordinator.registerField(
            key = "field",
            owner = firstOwner,
            validation = { ValidationStatus.INVALID },
            onStatusChange = {},
        )
        coordinator.registerField(
            key = "field",
            owner = secondOwner,
            validation = { ValidationStatus.VALID },
            onStatusChange = {},
        )

        coordinator.unregisterField(key = "field", owner = firstOwner)

        assertThat(coordinator.validate("field")).isTrue()
    }
}
