package io.embrace.android.embracesdk.internal.capture

import io.embrace.android.embracesdk.internal.arch.datasource.DataSourceState
import io.embrace.android.embracesdk.internal.capture.crumbs.BreadcrumbDataSource
import io.embrace.android.embracesdk.internal.capture.crumbs.PushNotificationDataSource
import io.embrace.android.embracesdk.internal.capture.crumbs.RnActionDataSource
import io.embrace.android.embracesdk.internal.capture.crumbs.TapDataSource
import io.embrace.android.embracesdk.internal.capture.crumbs.ViewDataSource
import io.embrace.android.embracesdk.internal.capture.crumbs.WebViewUrlDataSource
import io.embrace.android.embracesdk.internal.capture.memory.MemoryWarningDataSource
import io.embrace.android.embracesdk.internal.capture.session.SessionPropertiesDataSource
import io.embrace.android.embracesdk.internal.capture.webview.WebViewDataSource

public interface FeatureModule {
    public val memoryWarningDataSource: DataSourceState<MemoryWarningDataSource>
    public val breadcrumbDataSource: DataSourceState<BreadcrumbDataSource>
    public val viewDataSource: DataSourceState<ViewDataSource>
    public val pushNotificationDataSource: DataSourceState<PushNotificationDataSource>
    public val tapDataSource: DataSourceState<TapDataSource>
    public val webViewUrlDataSource: DataSourceState<WebViewUrlDataSource>
    public val rnActionDataSource: DataSourceState<RnActionDataSource>
    public val sessionPropertiesDataSource: DataSourceState<SessionPropertiesDataSource>
    public val webViewDataSource: DataSourceState<WebViewDataSource>
}
