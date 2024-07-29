package io.embrace.android.embracesdk.internal.capture

import io.embrace.android.embracesdk.fakes.FakeConfigService
import io.embrace.android.embracesdk.fakes.FakeOpenTelemetryModule
import io.embrace.android.embracesdk.fakes.injection.FakeCoreModule
import io.embrace.android.embracesdk.fakes.injection.FakeInitModule
import org.junit.Assert.assertNotNull
import org.junit.Test

internal class FeatureModuleImplTest {

    @Test
    fun testFeatureModule() {
        val module = FeatureModuleImpl(
            coreModule = FakeCoreModule(),
            initModule = FakeInitModule(),
            otelModule = FakeOpenTelemetryModule(),
            configService = FakeConfigService()
        )
        assertNotNull(module.breadcrumbDataSource)
        assertNotNull(module.viewDataSource)
        assertNotNull(module.memoryWarningDataSource)
        assertNotNull(module.pushNotificationDataSource)
        assertNotNull(module.tapDataSource)
        assertNotNull(module.rnActionDataSource)
        assertNotNull(module.sessionPropertiesDataSource)
        assertNotNull(module.webViewDataSource)
    }
}
