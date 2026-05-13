package com.rokt.roktux.component;

import androidx.compose.material3.ExperimentalMaterial3Api;
import androidx.compose.material3.ModalBottomSheetProperties;

// TODO: Delete this file and inline ModalBottomSheetProperties() after the SDK's Compose
// BOM is bumped to one targeting Material3 1.5.0+ stable, where the deprecated/legacy
// ctors no longer affect us.
final class BottomSheetPropertiesCompat {
    private BottomSheetPropertiesCompat() {
    }

    @ExperimentalMaterial3Api
    static ModalBottomSheetProperties create() {
        // Kotlin compiles ModalBottomSheetProperties() to a synthetic default constructor for the
        // SDK's current Compose BOM. Calling from Java targets the stable no-arg JVM constructor.
        return new ModalBottomSheetProperties();
    }
}
