package com.rokt

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rokt.roktux.BuildConfig
import com.rokt.roktux.RoktUx
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class RoktUxTest {

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        shadowOf(context.packageManager).getInternalMutablePackageInfo(context.packageName).versionName = "1.0.0"
    }

    @Test
    fun `getSdkConfig should return sdk config with correct version and integration information`() {
        // Arrange
        // Act
        val sdkConfig = RoktUx.getIntegrationConfig(ApplicationProvider.getApplicationContext())

        // Assert
        with(sdkConfig.version) {
            assertEquals(BuildConfig.SDK_VERSION, this)
            assertTrue(isValidSemver(this))
        }
        with(sdkConfig.layoutSchemaVersion) {
            assertEquals(BuildConfig.SCHEMA_VERSION, this)
            assertTrue(isValidSemver(this))
        }
        assertEquals("com.rokt.roktux.test", sdkConfig.packageName)
        assertEquals("1.0.0", sdkConfig.packageVersion)
        assertEquals("UX Helper Android", sdkConfig.name)
        assertEquals("Android", sdkConfig.operatingSystem)
        assertEquals("Android", sdkConfig.framework)
        assertEquals("Android", sdkConfig.platform)
        assertEquals("Phone", sdkConfig.deviceType)
        assertEquals("en_US", sdkConfig.deviceLocale)
        assertTrue(isValidJson(sdkConfig.toJsonString()))
    }

    private fun isValidJson(data: String): Boolean = try {
        Json.parseToJsonElement(data)
        true
    } catch (e: Exception) {
        false
    }

    private fun isValidSemver(version: String): Boolean = Regex(
        """^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?(?:\+([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?$""",
    ).matches(version)
}
