package io.embrace.android.embracesdk.internal.session.orchestrator

import io.embrace.android.embracesdk.internal.comms.delivery.DeliveryService
import io.embrace.android.embracesdk.internal.payload.Envelope
import io.embrace.android.embracesdk.internal.payload.SessionPayload
import io.embrace.android.embracesdk.internal.session.orchestrator.SessionSnapshotType.JVM_CRASH
import io.embrace.android.embracesdk.internal.session.orchestrator.SessionSnapshotType.NORMAL_END

internal class V1SessionPayloadStore(
    private val deliveryService: DeliveryService
) : SessionPayloadStore {

    override fun storeSessionPayload(envelope: Envelope<SessionPayload>, transitionType: TransitionType) {
        val type = when (transitionType) {
            TransitionType.CRASH -> JVM_CRASH
            else -> NORMAL_END
        }
        deliveryService.sendSession(envelope, type)
    }

    override fun cacheSessionSnapshot(envelope: Envelope<SessionPayload>) {
        deliveryService.sendSession(envelope, SessionSnapshotType.PERIODIC_CACHE)
    }
}
