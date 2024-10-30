package com.rokt.roktux

import android.content.Context
import android.os.Build
import com.rokt.roktux.utils.getDeviceLocale
import com.rokt.roktux.utils.getDeviceName
import com.rokt.roktux.utils.getPackageVersion

public object RoktUx {

    /**
     * Get the Rokt Integration configuration. Data to be included in the Rokt API request.
     *
     * @param context The Android Context.
     * @return The SDK configuration.
     */
    fun getIntegrationConfig(context: Context) = RoktIntegrationConfig(
        name = SDK_NAME,
        version = BuildConfig.SDK_VERSION,
        packageName = context.packageName,
        packageVersion = context.getPackageVersion(),
        layoutSchemaVersion = BuildConfig.SCHEMA_VERSION,
        framework = SDK_FRAMEWORK,
        platform = SDK_PLATFORM,
        operatingSystem = SDK_OPERATING_SYSTEM,
        operatingSystemVersion = Build.VERSION.RELEASE ?: "",
        deviceType = SDK_DEVICE_TYPE,
        deviceLocale = context.getDeviceLocale(),
        deviceModel = getDeviceName(),
        metadata = emptyMap(), // Metadata for future use
    )
}

private const val SDK_NAME = "UX Helper Android"
private const val SDK_FRAMEWORK = "Android" // Update this dynamically based on the framework
private const val SDK_PLATFORM = "Android"
private const val SDK_OPERATING_SYSTEM = "Android"
private const val SDK_DEVICE_TYPE = "Phone" // Update this dynamically based on the device type
