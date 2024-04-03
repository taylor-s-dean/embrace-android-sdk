package io.embrace.android.embracesdk.features

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.embrace.android.embracesdk.IntegrationTestRule
import io.embrace.android.embracesdk.arch.schema.SchemaDefaultName
import io.embrace.android.embracesdk.findEvent
import io.embrace.android.embracesdk.findSessionSpan
import io.embrace.android.embracesdk.recordSession
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class CustomBreadcrumbFeatureTest {

    @Rule
    @JvmField
    val testRule: IntegrationTestRule = IntegrationTestRule()

    @Test
    fun `custom breadcrumb feature`() {
        with(testRule) {
            val message = checkNotNull(harness.recordSession {
                embrace.addBreadcrumb("Hello, world!")
            })
            val breadcrumb = message.findSessionSpan().findEvent(SchemaDefaultName.CUSTOM_BREADCRUMB)
            assertEquals("Hello, world!", breadcrumb.attributes["message"])
        }
    }
}
