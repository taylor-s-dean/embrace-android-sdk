package io.embrace.android.embracesdk.internal.utils

import android.content.Context
import io.embrace.android.embracesdk.internal.capture.session.SessionPropertiesService
import io.embrace.android.embracesdk.internal.config.ConfigService
import io.embrace.android.embracesdk.internal.injection.AndroidServicesModule
import io.embrace.android.embracesdk.internal.injection.AnrModule
import io.embrace.android.embracesdk.internal.injection.CoreModule
import io.embrace.android.embracesdk.internal.injection.CrashModule
import io.embrace.android.embracesdk.internal.injection.CustomerLogModule
import io.embrace.android.embracesdk.internal.injection.DataCaptureServiceModule
import io.embrace.android.embracesdk.internal.injection.DataContainerModule
import io.embrace.android.embracesdk.internal.injection.DataSourceModule
import io.embrace.android.embracesdk.internal.injection.DeliveryModule
import io.embrace.android.embracesdk.internal.injection.EssentialServiceModule
import io.embrace.android.embracesdk.internal.injection.InitModule
import io.embrace.android.embracesdk.internal.injection.NativeModule
import io.embrace.android.embracesdk.internal.injection.OpenTelemetryModule
import io.embrace.android.embracesdk.internal.injection.PayloadModule
import io.embrace.android.embracesdk.internal.injection.SessionModule
import io.embrace.android.embracesdk.internal.injection.StorageModule
import io.embrace.android.embracesdk.internal.injection.SystemServiceModule
import io.embrace.android.embracesdk.internal.injection.WorkerThreadModule
import io.embrace.android.embracesdk.internal.logging.EmbLogger
import io.embrace.android.embracesdk.internal.payload.AppFramework
import java.io.OutputStream

/**
 * Typealias for a function that writes to an [OutputStream]. This is used to make it
 * easier to pass around logic for serializing data to arbitrary streams.
 */
internal typealias SerializationAction = (outputStream: OutputStream) -> Unit

/**
 * Function that returns an instance of [CoreModule]. Matches the signature of the constructor for [CoreModuleImpl]
 */
internal typealias CoreModuleSupplier = (
    context: Context,
    logger: EmbLogger
) -> CoreModule

/**
 * Function that returns an instance of [WorkerThreadModule]. Matches the signature of the constructor for [WorkerThreadModuleImpl]
 */
internal typealias WorkerThreadModuleSupplier = (initModule: InitModule) -> WorkerThreadModule

/**
 * Function that returns an instance of [SystemServiceModule]. Matches the signature of the constructor for [SystemServiceModuleImpl]
 */
internal typealias SystemServiceModuleSupplier = (
    coreModule: CoreModule,
    versionChecker: VersionChecker
) -> SystemServiceModule

/**
 * Function that returns an instance of [AndroidServicesModule]. Matches the signature of the constructor for [AndroidServicesModuleImpl]
 */
internal typealias AndroidServicesModuleSupplier = (
    initModule: InitModule,
    coreModule: CoreModule,
    workerThreadModule: WorkerThreadModule,
) -> AndroidServicesModule

/**
 * Function that returns an instance of [StorageModule]. Matches the signature of the constructor for [StorageModuleImpl]
 */
internal typealias StorageModuleSupplier = (
    initModule: InitModule,
    coreModule: CoreModule,
    workerThreadModule: WorkerThreadModule,
) -> StorageModule

/**
 * Function that returns an instance of [EssentialServiceModule]. Matches the signature of the constructor for [EssentialServiceModuleImpl]
 */
internal typealias EssentialServiceModuleSupplier = (
    initModule: InitModule,
    openTelemetryModule: OpenTelemetryModule,
    coreModule: CoreModule,
    workerThreadModule: WorkerThreadModule,
    systemServiceModule: SystemServiceModule,
    androidServicesModule: AndroidServicesModule,
    storageModule: StorageModule,
    customAppId: String?,
    customerLogModuleProvider: Provider<CustomerLogModule>,
    dataSourceModuleProvider: Provider<DataSourceModule>,
    framework: AppFramework,
    configServiceProvider: (framework: AppFramework) -> ConfigService?
) -> EssentialServiceModule

/**
 * Function that returns an instance of [DataCaptureServiceModule]. Matches the signature of the constructor for
 * [DataCaptureServiceModuleImpl]
 */
internal typealias DataCaptureServiceModuleSupplier = (
    initModule: InitModule,
    openTelemetryModule: OpenTelemetryModule,
    essentialServiceModule: EssentialServiceModule,
    workerThreadModule: WorkerThreadModule,
    versionChecker: VersionChecker,
    dataSourceModule: DataSourceModule
) -> DataCaptureServiceModule

/**
 * Function that returns an instance of [DeliveryModule]. Matches the signature of the constructor for [DeliveryModuleImpl]
 */
internal typealias DeliveryModuleSupplier = (
    initModule: InitModule,
    workerThreadModule: WorkerThreadModule,
    storageModule: StorageModule,
    essentialServiceModule: EssentialServiceModule,
) -> DeliveryModule

/**
 * Function that returns an instance of [AnrModule]. Matches the signature of the constructor for [AnrModuleImpl]
 */

internal typealias AnrModuleSupplier = (
    initModule: InitModule,
    essentialServiceModule: EssentialServiceModule,
    workerModule: WorkerThreadModule,
    otelModule: OpenTelemetryModule
) -> AnrModule

/**
 * Function that returns an instance of [CustomerLogModule]. Matches the signature of the constructor for [CustomerLogModuleImpl]
 */

internal typealias CustomerLogModuleSupplier = (
    initModule: InitModule,
    openTelemetryModule: OpenTelemetryModule,
    androidServicesModule: AndroidServicesModule,
    essentialServiceModule: EssentialServiceModule,
    deliveryModule: DeliveryModule,
    workerThreadModule: WorkerThreadModule,
    payloadModule: PayloadModule,
) -> CustomerLogModule

/**
 * Function that returns an instance of [NativeModule]. Matches the signature of the constructor for [NativeModuleImpl]
 */

internal typealias NativeModuleSupplier = (
    initModule: InitModule,
    coreModule: CoreModule,
    storageModule: StorageModule,
    essentialServiceModule: EssentialServiceModule,
    deliveryModule: DeliveryModule,
    androidServicesModule: AndroidServicesModule,
    workerThreadModule: WorkerThreadModule
) -> NativeModule

/**
 * Function that returns an instance of [DataContainerModule]. Matches the signature of the constructor for [DataContainerModuleImpl]
 */
internal typealias DataContainerModuleSupplier = (
    initModule: InitModule,
    workerThreadModule: WorkerThreadModule,
    essentialServiceModule: EssentialServiceModule,
    deliveryModule: DeliveryModule,
    startTime: Long
) -> DataContainerModule

/**
 * Function that returns an instance of [DataSourceModule]. Matches the signature of the constructor for [DataSourceModuleImpl]
 */

internal typealias DataSourceModuleSupplier = (
    initModule: InitModule,
    coreModule: CoreModule,
    openTelemetryModule: OpenTelemetryModule,
    essentialServiceModule: EssentialServiceModule,
    systemServiceModule: SystemServiceModule,
    androidServicesModule: AndroidServicesModule,
    workerThreadModule: WorkerThreadModule,
    anrModule: AnrModule
) -> DataSourceModule

/**
 * Function that returns an instance of [SessionModule]. Matches the signature of the constructor for [SessionModuleImpl]
 */

internal typealias SessionModuleSupplier = (
    initModule: InitModule,
    openTelemetryModule: OpenTelemetryModule,
    androidServicesModule: AndroidServicesModule,
    essentialServiceModule: EssentialServiceModule,
    nativeModule: NativeModule,
    deliveryModule: DeliveryModule,
    workerThreadModule: WorkerThreadModule,
    dataSourceModule: DataSourceModule,
    payloadModule: PayloadModule,
    dataCaptureServiceModule: DataCaptureServiceModule,
    dataContainerModule: DataContainerModule,
    customerLogModule: CustomerLogModule
) -> SessionModule

/**
 * Function that returns an instance of [CrashModule]. Matches the signature of the constructor for [CrashModuleImpl]
 */

internal typealias CrashModuleSupplier = (
    initModule: InitModule,
    storageModule: StorageModule,
    essentialServiceModule: EssentialServiceModule,
    nativeModule: NativeModule,
    sessionModule: SessionModule,
    anrModule: AnrModule,
    androidServicesModule: AndroidServicesModule,
    logModule: CustomerLogModule,
) -> CrashModule

/**
 * Function that returns an instance of [PayloadModule]. Matches the signature of the constructor for [PayloadModuleImpl]
 */
internal typealias PayloadModuleSupplier = (
    initModule: InitModule,
    coreModule: CoreModule,
    androidServicesModule: AndroidServicesModule,
    essentialServiceModule: EssentialServiceModule,
    systemServiceModule: SystemServiceModule,
    workerThreadModule: WorkerThreadModule,
    nativeModule: NativeModule,
    otelModule: OpenTelemetryModule,
    anrModule: AnrModule,
    sessionPropertiesServiceProvider: Provider<SessionPropertiesService>
) -> PayloadModule
