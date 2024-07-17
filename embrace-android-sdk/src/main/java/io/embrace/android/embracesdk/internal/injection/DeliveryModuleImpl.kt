package io.embrace.android.embracesdk.internal.injection

import io.embrace.android.embracesdk.internal.comms.delivery.DeliveryService
import io.embrace.android.embracesdk.internal.comms.delivery.EmbraceDeliveryService
import io.embrace.android.embracesdk.internal.comms.delivery.NoopDeliveryService
import io.embrace.android.embracesdk.internal.worker.WorkerName
import io.embrace.android.embracesdk.internal.worker.WorkerThreadModule

internal class DeliveryModuleImpl(
    initModule: InitModule,
    workerThreadModule: WorkerThreadModule,
    storageModule: StorageModule,
    essentialServiceModule: EssentialServiceModule,
) : DeliveryModule {

    override val deliveryService: DeliveryService by singleton {
        val apiService = essentialServiceModule.apiService
        if (apiService == null) {
            NoopDeliveryService()
        } else {
            EmbraceDeliveryService(
                storageModule.deliveryCacheManager,
                apiService,
                workerThreadModule.backgroundWorker(WorkerName.DELIVERY_CACHE),
                initModule.jsonSerializer,
                initModule.logger
            )
        }
    }
}